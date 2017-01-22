package com.hfad.sbhacks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.io.IOException;
//import com.imgur.api3example.login.ImgurAuthorization;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OAuthTestActivity extends Activity {

    public static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private static final String AUTHORIZATION_URL = "https://api.imgur.com/oauth2/authorize";
    private static final String CLIENT_ID = "CLIENT_ID";

    private LinearLayout rootView;

    private String accessToken;
    private String refreshToken;

    private String picturePath = "";
    private Button send;

    private String uploadedImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(llp);
        rootView.addView(tv);
        setContentView(rootView);

        String action = getIntent().getAction();

        if (action == null || !action.equals(Intent.ACTION_VIEW)) { // We need access token to use Imgur's api

            tv.setText("Start OAuth Authorization");

            Uri uri = Uri.parse(AUTHORIZATION_URL).buildUpon()
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .appendQueryParameter("response_type", "token")
                    .appendQueryParameter("state", "init")
                    .build();

            Intent intent = new Intent();
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else { // Now we have the token, can do the upload

            tv.setText("Got Access Token");

            Uri uri = getIntent().getData();
            Log.d("Got imgurs access token", uri.toString());
            String uriString = uri.toString();
            String paramsString = "http://callback?" + uriString.substring(uriString.indexOf("#") + 1);
            Log.d("tag", paramsString);
            List<NameValuePair> params = URLEncodedUtils.parse(URI.create(paramsString), "utf-8");
            Log.d("tag", Arrays.toString(params.toArray(new NameValuePair[0])));

            for (NameValuePair pair : params) {
                if (pair.getName().equals("access_token")) {
                    accessToken = pair.getValue();
                } else if (pair.getName().equals("refresh_token")) {
                    refreshToken = pair.getValue();
                }
            }

            Log.d("tag", "access_token = " + accessToken);
            Log.d("tag", "refresh_token = " + refreshToken);

            Button chooseImage = new Button(this);
            rootView.addView(chooseImage);
            chooseImage.setText("Choose an image");
            chooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                }
            });

            send = new Button(this);
            rootView.addView(send);
            send.setText("send to imgur");
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (picturePath != null && picturePath.length() > 0 &&
                            accessToken != null && accessToken.length() > 0) {
                        (new UploadToImgurTask()).execute(picturePath);
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (send == null) return;
        if (picturePath == null || picturePath.length() == 0) {
            send.setVisibility(View.GONE);
        } else {
            send.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "request code : " + requestCode + ", result code : " + resultCode);
        if (data == null) {
            Log.d("tag" , "data is null");
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.d("tag", "image path : " + picturePath);
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Here is the upload task
    class UploadToImgurTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            final String upload_to = "https://api.imgur.com/3/upload";

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(upload_to);

            try {
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addPart("image", new FileBody(new File(params[0])))
                        .build();

                httpPost.setHeader("Authorization", "Bearer " + accessToken);
                httpPost.setEntity(entity);

                final HttpResponse response = httpClient.execute(httpPost,
                        localContext);

                final String response_string = EntityUtils.toString(response
                        .getEntity());

                final JSONObject json = new JSONObject(response_string);

                Log.d("tag", json.toString());

                JSONObject data = json.optJSONObject("data");
                uploadedImageUrl = data.optString("link");
                Log.d("tag", "uploaded image url : " + uploadedImageUrl);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean.booleanValue()) { // after sucessful uploading, show the image in web browser
                Button openBrowser = new Button(OAuthTestActivity.this);
                rootView.addView(openBrowser);
                openBrowser.setText("Open Browser");
                openBrowser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(uploadedImageUrl));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }

}

//
//public abstract class ImgurUploadTask extends AsyncTask<Void, Void, String> {
//
//    private static final String TAG = ImgurUploadTask.class.getSimpleName();
//
//    private static final String UPLOAD_URL = "https://api.imgur.com/3/image";
//
//    private Activity mActivity;
//    private Uri mImageUri;  // local Uri to upload
//
//    public ImgurUploadTask(Uri imageUri, Activity activity) {
//        this.mImageUri = imageUri;
//        this.mActivity = activity;
//    }
//
//    @Override
//    protected String doInBackground(Void... params) {
//        InputStream imageIn;
//        try {
//            imageIn = mActivity.getContentResolver().openInputStream(mImageUri);
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "could not open InputStream", e);
//            return null;
//        }
//
//        HttpURLConnection conn = null;
//        InputStream responseIn = null;
//
//        try {
//            conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
//            conn.setDoOutput(true);
//
//            ImgurAuthorization.getInstance().addToHttpURLConnection(conn);
//
//            OutputStream out = conn.getOutputStream();
//            copy(imageIn, out);
//            out.flush();
//            out.close();
//
//            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                responseIn = conn.getInputStream();
//                return onInput(responseIn);
//            }
//            else {
//                Log.i(TAG, "responseCode=" + conn.getResponseCode());
//                responseIn = conn.getErrorStream();
//                StringBuilder sb = new StringBuilder();
//                Scanner scanner = new Scanner(responseIn);
//                while (scanner.hasNext()) {
//                    sb.append(scanner.next());
//                }
//                Log.i(TAG, "error response: " + sb.toString());
//                return null;
//            }
//        } catch (Exception ex) {
//            Log.e(TAG, "Error during POST", ex);
//            return null;
//        } finally {
//            try {
//                responseIn.close();
//            } catch (Exception ignore) {}
//            try {
//                conn.disconnect();
//            } catch (Exception ignore) {}
//            try {
//                imageIn.close();
//            } catch (Exception ignore) {}
//        }
//    }
//
//    private static int copy(InputStream input, OutputStream output) throws IOException {
//        byte[] buffer = new byte[8192];
//        int count = 0;
//        int n = 0;
//        while (-1 != (n = input.read(buffer))) {
//            output.write(buffer, 0, n);
//            count += n;
//        }
//        return count;
//    }
//
//    protected String onInput(InputStream in) throws Exception {
//        StringBuilder sb = new StringBuilder();
//        Scanner scanner = new Scanner(in);
//        while (scanner.hasNext()) {
//            sb.append(scanner.next());
//        }
//
//        JSONObject root = new JSONObject(sb.toString());
//        String id = root.getJSONObject("data").getString("id");
//        String deletehash = root.getJSONObject("data").getString("deletehash");
//
//        Log.i(TAG, "new imgur url: http://imgur.com/" + id + " (delete hash: " + deletehash + ")");
//        return id;
//    }
//
//}
