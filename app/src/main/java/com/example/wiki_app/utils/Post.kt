package com.example.wiki_app.utils

data class Post(
    val title: String,
    val content: String,
    val plainContent: String, // 검색을 위한 순수 텍스트 내용
    val path: String
) 