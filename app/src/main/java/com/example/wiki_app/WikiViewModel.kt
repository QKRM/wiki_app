package com.example.wiki_app

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wiki_app.utils.FileUtils
import com.example.wiki_app.utils.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WikiViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _postContent = MutableStateFlow("")
    val postContent: StateFlow<String> = _postContent

    fun loadCategoryPosts(context: Context, category: String) {
        viewModelScope.launch {
            _posts.value = FileUtils.getCategoryPosts(context, category)
        }
    }

    fun loadPostContent(context: Context, category: String, fileName: String) {
        viewModelScope.launch {
            _postContent.value = FileUtils.getPostContent(context, category, fileName)
        }
    }
}
