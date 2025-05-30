package com.example.wiki_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wiki_app.ui.components.ArticleText
import com.example.wiki_app.ui.theme.Wiki_appTheme
import com.example.wiki_app.utils.FileUtils
import com.example.wiki_app.utils.Post

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Wiki_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WikiApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiApp() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = "categoryList") {
            composable("categoryList") {
                CategoryListScreen { category ->
                    navController.navigate("categoryPage/$category")
                }
            }
            composable("categoryPage/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                CategoryPageScreen(
                    category = category,
                    onPostClick = { fileName ->
                        navController.navigate("postPage/$category/$fileName")
                    }
                )
            }
            composable("postPage/{category}/{fileName}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
                PostPageScreen(category = category, fileName = fileName) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(onCategoryClick: (String) -> Unit) {
    val categories = listOf("goat", "cow", "chicken", "crops")
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Category List") }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(category) }
                        .padding(16.dp),
                    headlineContent = { Text(category) }
                )
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPageScreen(
    category: String,
    onPostClick: (String) -> Unit
) {
    val context = LocalContext.current
    val posts = remember(category) {
        FileUtils.getCategoryPosts(context, category)
    }
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("$category Articles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(posts) { post ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPostClick(post.title) }
                        .padding(16.dp),
                    headlineContent = { Text(post.title) }
                )
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPageScreen(
    category: String,
    fileName: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val content = remember(fileName, category) {
        FileUtils.getPostContent(context, category, "$fileName.html")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(fileName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ArticleText(
                    content = content,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private val LocalNavController = staticCompositionLocalOf<NavController> { 
    error("CompositionLocal LocalNavController not present") 
}