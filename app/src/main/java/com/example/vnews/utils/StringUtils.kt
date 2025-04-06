package com.example.vnews.utils

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object StringUtils {
    fun encodeUrl(url: String): String {
        return URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    }
}
