package com.example.networkingpr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract.CommonDataKinds.Website
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import org.json.JSONObject.NULL

class DetailActivity : AppCompatActivity() {

    lateinit var browser: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val url = intent.getStringExtra("URL")
            browser = findViewById(R.id.webView)

            if (url != null) {
                browser.settings.javaScriptEnabled = true
                browser.settings.setSupportZoom(true)


                browser.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                        browser.visibility = View.VISIBLE
                    }

                }

                browser.loadUrl(url)

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        browser.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        browser.restoreState(savedInstanceState)
    }

    override fun onBackPressed() {

        if(browser.canGoBack()){
            browser.goBack()
        }else{
            super.onBackPressed()
        }
    }

}