package com.example.wiki_app.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader

object FileUtils {
    private const val TAG = "FileUtils"
    private var selectedCountry = "UG" // 기본값을 UG로 설정

    fun setSelectedCountry(country: String) {
        selectedCountry = country
    }

    fun getCategoryPosts(context: Context, category: String): List<Post> {
        val posts = mutableListOf<Post>()
        try {
            val files = context.assets.list("posts/$selectedCountry/$category") ?: return emptyList()
            Log.d(TAG, "Found files in category $category: ${files.joinToString()}")
            
            files.filter { it.endsWith(".html") }
                .map { fileName ->
                    val content = getPostContent(context, category, fileName)
                    val plainContent = content
                        .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "") // style 태그 제거
                        .replace(Regex("<[^>]*>"), "") // 나머지 HTML 태그 제거
                        .replace(Regex("\\s+"), " ") // 연속된 공백을 하나로
                        .trim()
                    Post(
                        title = fileName.replace(".html", ""),
                        content = content,
                        plainContent = plainContent,
                        path = "posts/$selectedCountry/$category/$fileName"
                    )
                }
                .sortedBy { it.title }
                .also { posts.addAll(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting category posts: ${e.message}", e)
            e.printStackTrace()
        }
        return posts
    }

    fun getPostContent(context: Context, category: String, fileName: String): String {
        val filePath = "posts/$selectedCountry/$category/$fileName"
        return try {
            Log.d(TAG, "Trying to read file: $filePath")
            val inputStream = context.assets.open(filePath)
            val reader = BufferedReader(inputStream.reader())
            reader.use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading file $filePath: ${e.message}", e)
            e.printStackTrace()
            "File not found: $filePath"
        }
    }
} 