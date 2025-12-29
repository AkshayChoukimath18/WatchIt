package com.example.watchit.data.remote

import android.util.Log

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import okhttp3.OkHttpClient
import okhttp3.Protocol

import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup


class LinkPreviewFetcher(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun fetch(url: String): LinkPreview? = withContext(Dispatchers.IO) {
        try {

            val request = Request.Builder()
                .url(url)
                .header("User-Agent","Mozilla/5.0")
                .build()

            client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) return@withContext null

                val body = response.body?.string() ?: return@withContext null
                val doc = Jsoup.parse(body)

                Log.d("Preview_Debug", doc.toString())
                fun meta(props: String): String? =
                    doc.selectFirst("meta[property=$props]")?.attr("content")?.takeIf { it.isNotBlank() }
                val  ogTitle = meta("og:title")
                val ogDesc = meta("og:description")
                val ogImage = meta("og:image")
                val title = ogTitle ?: doc.title().takeIf { it.isNotBlank() }
                val desc = ogDesc ?: doc.selectFirst("meta[name=description]")?.attr("content")?.takeIf { it.isNotBlank() }

                LinkPreview(
                    url = url,
                    title = title,
                    description = desc,
                    imageUrl = ogImage.toString()
                )


            }
        }catch (e: Exception){
            delay(500)
            null
        }
    }


}