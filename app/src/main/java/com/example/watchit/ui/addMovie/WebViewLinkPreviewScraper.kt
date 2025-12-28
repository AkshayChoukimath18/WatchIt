package com.example.watchit.ui.addMovie

import android.content.Context
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.DialogTitle
import com.example.watchit.data.remote.LinkPreview
import com.example.watchit.util.cleanTitle
import com.example.watchit.util.detectPlatform
import okhttp3.internal.platform.Platform
import org.json.JSONException
import org.json.JSONObject

class WebViewLinkPreviewScraper(context: Context) {
    private val webView: WebView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
    }

    private val extractionScript = """
    (function() {
        const get = (sel) => document.querySelector(sel)?.content || null;

        const title =
            get('meta[property="og:title"]') ||
            document.title ||
            null;

        const description =
            get('meta[property="og:description"]') ||
            document.querySelector('meta[name="description"]')?.content ||
            null;

        const imageUrl =
            get('meta[property="og:image"]') ||
            null;

        // 👉 Return a plain object; WebView will JSON-encode it for us
        return {
            title: title,
            description: description,
            imageUrl: imageUrl
        };
    })();
""".trimIndent()


    fun load(
        url: String,
        onResult: (LinkPreview?) -> Unit
    ) {
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                super.onPageFinished(view, finishedUrl)

                view?.evaluateJavascript(extractionScript) { raw ->
                    if (raw == null || raw == "null") {
                        onResult(null)
                        return@evaluateJavascript
                    }

                    try {
                        val obj = JSONObject(raw)

                        val platform = detectPlatform(url)
                        val title = obj.optString("title", null)
                            ?.takeIf { it.isNotBlank() }?.let {
                                cleanTitle(it, platform)
                            }


                        val description = obj.optString("description", null)
                            ?.takeIf { it.isNotBlank() }

                        val imageUrl = obj.optString("imageUrl", null)
                            ?.takeIf { it.isNotBlank() && it != "null" }

                        val preview = LinkPreview(
                            url = finishedUrl ?: url,
                            title = title,
                            description = description,
                            imageUrl = imageUrl
                        )

                        onResult(preview)

                    } catch (e: JSONException) {
                        Log.e("Preview", "Invalid JSON from JS: $raw", e)
                        onResult(null)
                    }
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                onResult(null)
            }
        }

        webView.loadUrl(url)
    }

    fun destroy() {
        webView.destroy()
    }


}