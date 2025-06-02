package com.example.wiki_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wiki_app.ui.components.ArticleText
import com.example.wiki_app.utils.FileUtils
import com.example.wiki_app.utils.Post

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WikiApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "categoryList") {
        composable("categoryList") {
            CategoryListScreen(navController)
        }
        composable("categoryPage/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryPageScreen(navController, category)
        }
        composable("postPage/{category}/{fileName}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            PostPageScreen(navController, category, fileName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController) {
    val categories = listOf("goat", "cow", "chicken", "crops")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Good Farmers Wiki") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F8E9))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category = category, navController = navController)
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("categoryPage/$category") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = when(category) {
                    "goat" -> "Goat"
                    "cow" -> "Cow"
                    "chicken" -> "Chicken"
                    "crops" -> "Crops"
                    else -> category
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPageScreen(navController: NavController, category: String) {
    val posts = FileUtils.getCategoryPosts(LocalContext.current, category)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(when(category) {
                    "goat" -> "Goat"
                    "cow" -> "Cow"
                    "chicken" -> "Chicken"
                    "crops" -> "Crops"
                    else -> category
                }) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F8E9))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                PostCard(post = post, category = category, navController = navController)
            }
        }
    }
}

@Composable
fun PostCard(post: Post, category: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("postPage/$category/${post.path.split("/").last()}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = post.title,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPageScreen(navController: NavController, category: String, fileName: String) {
    val content = FileUtils.getPostContent(LocalContext.current, category, fileName)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileName.replace(".html", "")) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        ArticleText(
            content = content,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

private val LocalNavController = staticCompositionLocalOf<NavController> { 
    error("CompositionLocal LocalNavController not present") 
}