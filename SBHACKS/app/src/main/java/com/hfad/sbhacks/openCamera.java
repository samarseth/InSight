//
//package com.hfad.sbhacks;
//
//import android.content.Intent;
//import android.graphics.Camera;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.widget.Button;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//
//private class openCamera extends AsyncTask<URL, Integer, Long> {
//    SurfaceHolder.Callback callback;
//
//    public openCamera(SurfaceHolder.Callback callback){
//        this.callback = callback;
//    }
//
//    protected void doInBackground(){
//
//    }
//    int TAKE_PHOTO_CODE = 0;
//    public static int count = 0;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_open_camera);
//
//        // Here, we are making a folder named picFolder to store
//        // pics taken by the camera using this application.
//        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
//        File newdir = new File(dir);
//        newdir.mkdirs();
//
//        Button capture = (Button) findViewById(R.id.btnCapture);
//        capture.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                // Here, the counter will be incremented each time, and the
//                // picture taken by camera will be stored as 1.jpg,2.jpg
//                // and likewise.
//                count++;
//                String file = dir+count+".jpg";
//                File newfile = new File(file);
//                try {
//                    newfile.createNewFile();
//                }
//                catch (IOException e)
//                {
//                }
//
//                Uri outputFileUri = Uri.fromFile(newfile);
//
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//
//                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
//            Log.d("CameraDemo", "Pic saved");
//            Intent intent = new Intent(this, bestGuess.class);
//            startActivity(intent);
//        }
//    }
//}
//
