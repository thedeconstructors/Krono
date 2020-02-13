package com.deconstructors.krono.activities.friends;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.deconstructors.krono.R;

public class Menu3_Friends extends AppCompatActivity
{
    private WebView m_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu3__friends);

        m_webView = findViewById(R.id.web_view);
        WebSettings webSettings = m_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        m_webView.loadUrl("file:///android_asset/definitely_not_a_turtle_dancing_in_the_shower.gif");
    }
}
