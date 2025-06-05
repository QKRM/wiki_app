package com.example.wiki_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 카테고리 텍스트
            Text(
                text = when(category) {
                    "goat" -> "Goat"
                    "cow" -> "Cow"
                    "chicken" -> "Chicken"
                    "crops" -> "Crops"
                    else -> category
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 카테고리 이미지
            Image(
                painter = painterResource(
                    id = when(category) {
                        "goat" -> R.drawable.goat_image
                        "cow" -> R.drawable.cow_image
                        "chicken" -> R.drawable.chicken_image
                        "crops" -> R.drawable.crops_image
                        else -> R.drawable.default_image
                    }
                ),
                contentDescription = "Category Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPageScreen(navController: NavController, category: String) {
    val posts = FileUtils.getCategoryPosts(LocalContext.current, category)
    var searchQuery by remember { mutableStateOf("") }
    var filteredPosts by remember { mutableStateOf(posts) }
    
    // 검색어가 변경될 때마다 게시글 필터링
    LaunchedEffect(searchQuery, posts) {
        filteredPosts = if (searchQuery.isEmpty()) {
            posts
        } else {
            posts.filter { post ->
                // HTML 태그와 CSS 속성값들을 제거
                val cleanContent = post.content
                    .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "") // style 태그 제거
                    .replace(Regex("<[^>]*>"), "") // 나머지 HTML 태그 제거
                    .replace(Regex("\\s+"), " ") // 연속된 공백을 하나로
                    .trim()
                
                post.title.contains(searchQuery, ignoreCase = true) ||
                cleanContent.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        topBar = {
            Column {
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
                // 검색창
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Enter search term") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF2E7D32),
                        unfocusedIndicatorColor = Color(0xFF2E7D32),
                        cursorColor = Color(0xFF2E7D32)
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F8E9))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPosts) { post ->
                PostCard(
                    post = post,
                    category = category,
                    navController = navController,
                    searchQuery = searchQuery
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    category: String,
    navController: NavController,
    searchQuery: String
) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = post.title,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
            if (searchQuery.isNotEmpty() && post.content.contains(searchQuery, ignoreCase = true)) {
                Spacer(modifier = Modifier.height(8.dp))
                val cleanContent = post.content.replace(Regex("<[^>]*>"), "")
                Text(
                    text = "...${cleanContent.substring(
                        maxOf(0, cleanContent.indexOf(searchQuery, ignoreCase = true) - 20),
                        minOf(cleanContent.length, cleanContent.indexOf(searchQuery, ignoreCase = true) + searchQuery.length + 20)
                    )}...",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
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