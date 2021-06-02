package com.wmp.wheresmyphone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WebViewFragment extends Fragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        WebView browser = (WebView) getView().findViewById(R.id.webview_1);

        //TODO replace this with the IP Address specified in settings
        browser.loadUrl("http://wheresmyphone.ts.r.appspot.com/");
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setSupportZoom(true);
        browser.getSettings().setDisplayZoomControls(false);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.setInitialScale(150);
    }
}
