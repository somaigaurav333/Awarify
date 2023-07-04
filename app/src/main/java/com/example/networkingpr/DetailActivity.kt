package com.example.networkingpr

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class DetailActivity : AppCompatActivity() {


    private lateinit var webView: WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val url = intent.getStringExtra("URL")

            webView = findViewById(R.id.webView)
            webView.settings.apply {
                javaScriptEnabled = true
            }

//            try {
//                webView.apply {
//                    webChromeClient = getChromeClient()
//                    webViewClient = getClient()
//                }
//                setContentView(webView)
//
//                if (url != null) {
//                    webView.loadUrl(url)
//                }
//            } catch (e: Exception) {

            webView.apply {
                clearFormData()
                settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }

            if (url != null) {
                webView.settings.javaScriptEnabled = true
                webView.settings.setSupportZoom(true)


                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                        webView.visibility = View.VISIBLE
                    }

                }

                webView.loadUrl(url)

            }
        }


    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }


//    private fun getChromeClient(): WebChromeClient {
//        return object : WebChromeClient() {
//
//        }
//    }
//
//
//    private fun getBitmapInputStream(
//        bitmap: Bitmap,
//        compressFormat: Bitmap.CompressFormat
//    ): InputStream {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(compressFormat, 80, byteArrayOutputStream)
//        val bitmapData: ByteArray = byteArrayOutputStream.toByteArray()
//        return ByteArrayInputStream(bitmapData)
//    }
//
//    private fun getClient(): WebViewClient {
//        return object : WebViewClient() {
//            override fun shouldInterceptRequest(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): WebResourceResponse? {
//                return super.shouldInterceptRequest(view, request)
//            }
//
//            @Deprecated("Deprecated in Java")
//            override fun shouldInterceptRequest(
//                view: WebView?,
//                url: String?
//            ): WebResourceResponse? {
//                if (url == null) {
//                    return super.shouldInterceptRequest(view, url as String)
//                }
//                return if (url.lowercase(Locale.ROOT).contains(".jpg") || url.lowercase(Locale.ROOT)
//                        .contains(".jpeg")
//                ) {
//                    val bitmap =
//                        Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .load(url).submit().get()
//                    WebResourceResponse(
//                        "image/jpg",
//                        "UTF-8",
//                        getBitmapInputStream(bitmap, Bitmap.CompressFormat.JPEG)
//                    )
//                } else if (url.lowercase(Locale.ROOT).contains(".png")) {
//                    val bitmap =
//                        Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .load(url).submit().get()
//                    WebResourceResponse(
//                        "image/png",
//                        "UTF-8",
//                        getBitmapInputStream(bitmap, Bitmap.CompressFormat.PNG)
//                    )
//                } else if (url.lowercase(Locale.ROOT).contains(".webp")) {
//                    val bitmap =
//                        Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .load(url).submit().get()
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        WebResourceResponse(
//                            "image/webp",
//                            "UTF-8",
//                            getBitmapInputStream(bitmap, Bitmap.CompressFormat.WEBP_LOSSY)
//                        )
//                    } else {
//                        TODO("VERSION.SDK_INT < R")
//                    }
//                } else {
//                    super.shouldInterceptRequest(view, url)
//                }
//
//            }
//        }
//    }

}

