package com.example.cis3515_1.Screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView


//@Composable
/*fun <WebViewClient : Any> WebViewSample(
    url: String,
    webViewClient: android.webkit.WebViewClient = WebViewClient()
) {

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                this.webViewClient = webViewClient
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

class CustomWebViewClient: WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if(url != null && url.startsWith("https://templeu.instructure.com")){
            view?.loadUrl(url)
            return true
        }
        return false
    }
}*/

