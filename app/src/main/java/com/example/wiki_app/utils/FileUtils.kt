package com.example.wiki_app.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader

object FileUtils {
    private const val TAG = "FileUtils"

    fun getCategoryPosts(context: Context, category: String): List<Post> {
        val posts = mutableListOf<Post>()
        try {
            val files = context.assets.list("posts/$category") ?: return emptyList()
            Log.d(TAG, "Found files in category $category: ${files.joinToString()}")
            
            files.filter { it.endsWith(".html") }
                .map { fileName ->
                    val content = getPostContent(context, category, fileName)
                    Post(
                        title = fileName.replace(".html", ""),
                        content = content,
                        path = "posts/$category/$fileName"
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
        val filePath = "posts/$category/$fileName"
        return try {
            Log.d(TAG, "Trying to read file: $filePath")
            val inputStream = context.assets.open(filePath)
            val reader = BufferedReader(inputStream.reader())
            reader.use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading file $filePath: ${e.message}", e)
            e.printStackTrace()
            "파일을 찾을 수 없습니다: $filePath"
        }
    }
} 