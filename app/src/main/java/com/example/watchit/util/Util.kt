package com.example.watchit.util

import android.util.Patterns

fun extractUrl(text: String): String?{
    val matcher = Patterns.WEB_URL.matcher(text)
    return if (matcher.find()){
        matcher.group()
    }else{
        null
    }

}

fun detectPlatform(url: String): String{
    val lower = url.lowercase()
    return when {
        "netflix.com" in lower || "netflix.in" in lower -> "NETFLIX"
        "primevideo.com" in lower || "amazon.com" in lower || "primevideo.in" in lower || "amazon.in" in lower -> "PRIME VIDEO"
        "hotstar.com" in lower ||  "hotstar.in" in lower -> "JIOHOTSTAR"
        "youtube.com" in lower || "youtube.in" in lower -> "YOUTUBE"
        "zee5.com" in lower || "zee5.in" in lower -> "ZEE5"
        "sonyliv.com" in lower || "sonyliv.in" in lower -> "SONYLIV"
        else -> "UNKNOWN"
    }
}

fun cleanTitle(title: String, platform: String): String {
    val stopWords = listOf(
        "Full HD",
        "full hd",
        "HD",
        "hd",
        "Official",
        "official",
        "Trailer",
        "trailer",
        "|",         // handles "Watch X | Netflix"
        "-",         // handles "Watch X - Netflix"
        "on",        // handles "Watch X on Hotstar"
        "Online",    // "Watch X Online"
        "Streaming"  // "Watch X Streaming"
    )
    var title =  when(platform){
        "NETFLIX" -> title
            .split("|")
            .first()
            .removePrefix("Watch ")
            .removePrefix("watch")
            .trim()

        "PRIME VIDEO" -> title
            .removePrefix("Prime Video: ")
            .removePrefix("prime video: ")
            .trim()

        "ZEE5" -> title
            .removePrefix("Watch ")
            .removePrefix("watch ")
            .trim()

        "SONYLIV" -> title
            .removePrefix("Watch ")
            .removePrefix("watch ").trim()

        else -> title.trim()
    }

    for (word in stopWords){
        if (title.contains(word, ignoreCase = true)){
            title = title.substringBefore(word).trim()
        }
    }

    return title

}