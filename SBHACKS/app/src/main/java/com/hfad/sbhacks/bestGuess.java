package com.hfad.sbhacks;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class bestGuess extends AppCompatActivity {
    String newUrl = "https://www.google.com/searchbyimage?site=search&sa=X&image_url=";
    //scrapeBestGuess(newUrl);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_guess);
        //scrapeBestGuess(newUrl);
        new scraper().execute();
    }

class scraper extends AsyncTask<Void, String, String> {
    String bestGuess = "empty string";

    @Override
    protected String doInBackground(Void... arg0) {
            Document doc;
            Element bestGuessElement;
            try {
                doc = Jsoup.connect(newUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36").get();
                bestGuessElement = doc.select("._gUb").first();

                if (bestGuessElement.hasText()) {
                    bestGuess = bestGuessElement.text();
               }

            } catch (IOException ie) {
                ie.printStackTrace();
            }
        return bestGuess;
    }

    @Override
    protected void onPostExecute(String bestGuess){

        TextView t = (TextView) findViewById(R.id.textView2);
        t.setText(bestGuess);
    }
}
}
