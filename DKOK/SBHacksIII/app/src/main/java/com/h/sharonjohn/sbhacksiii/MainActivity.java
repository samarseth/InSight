package com.h.sharonjohn.sbhacksiii;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    String keyword="bottle";

    public void RegAmazon(View view)

    {
        String regamazon="https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords="+keyword;
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(regamazon));
        startActivity(websiteIntent);
    }

    public void AmazonPrime(View view)

    {
        String AmazonPrime="https://www.amazon.com/s/ref=sr_nr_p_85_0?fst=as%3Aoff&rh=i%3Aaps%2Ck%3Abottle%2Cp_85%3A2470955011&keywords=" + keyword+ "&ie=UTF8&qid=1485065816&rnid=2470954011";
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AmazonPrime));
        startActivity(websiteIntent);
    }

    public void Target(View view)

    {
        String Target="http://www.target.com/s?searchTerm="+keyword;
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Target));
        startActivity(websiteIntent);
    }

    public void Ebay(View view)

    {
        String Ebay="http://www.ebay.com/sch/i.html?_from=R40&_trksid=p2050601.m570.l1313.TR12.TRC2.A0.H0.X" + keyword + ".TRS0&_nkw=bottle&_sacat=0";
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Ebay));
        startActivity(websiteIntent);
    }

    public void CostCo(View view)

    {
        String CostCo="https://www.costco.com/CatalogSearch?keyword=" + keyword + "&pageSize=96";
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CostCo));
        startActivity(websiteIntent);
    }
}

