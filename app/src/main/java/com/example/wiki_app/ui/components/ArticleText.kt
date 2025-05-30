package com.example.wiki_app.ui.components

import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun ArticleText(
    content: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    allowFileAccess = true
                    allowContentAccess = true
                    mediaPlaybackRequiresUserGesture = false
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return false
                    }
                }
            }
        },
        update = { webView ->
            val fixedContent = content
                .replace(
                    Regex("""(?<!<img )src=\"([^\"]+)\"\s*/?>"""),
                    "<img src=\"$1\" />"
                )
                .replace(
                    Regex("src=\"images/"),
                    "src=\"posts/images/"
                )
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <base href="file:///android_asset/">
                    <style>
                        :root {
                            --primary-color: #2E7D32;
                            --primary-light: #4CAF50;
                            --primary-dark: #1B5E20;
                            --text-color: #333333;
                            --background-color: #F1F8E9;
                            --border-color: #A5D6A7;
                        }
                        
                        body {
                            font-family: sans-serif;
                            line-height: 1.6;
                            padding: 16px;
                            color: var(--text-color);
                            background-color: var(--background-color);
                        }
                        
                        h1, h2, h3, h4, h5, h6 {
                            color: var(--primary-dark);
                            border-bottom: 2px solid var(--border-color);
                            padding-bottom: 8px;
                            margin-top: 24px;
                        }
                        
                        h1 {
                            font-size: 24px;
                            color: var(--primary-color);
                        }
                        
                        h2 {
                            font-size: 20px;
                        }
                        
                        h3 {
                            font-size: 18px;
                        }
                        
                        p {
                            margin: 16px 0;
                        }
                        
                        a {
                            color: var(--primary-color);
                            text-decoration: none;
                        }
                        
                        a:hover {
                            text-decoration: underline;
                        }
                        
                        ul, ol {
                            padding-left: 24px;
                        }
                        
                        li {
                            margin: 8px 0;
                        }
                        
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin: 16px 0;
                            overflow-x: auto;
                            display: block;
                            background-color: white;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        
                        th, td {
                            border: 1px solid var(--border-color);
                            padding: 12px;
                            text-align: left;
                        }
                        
                        th {
                            background-color: var(--primary-color);
                            color: black;
                            font-weight: bold;
                        }
                        
                        tr:nth-child(even) {
                            background-color: #E8F5E9;
                        }
                        
                        tr:hover {
                            background-color: #C8E6C9;
                        }
                        
                        img {
                            max-width: 100%;
                            height: auto;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                            margin: 16px 0;
                        }
                        
                        blockquote {
                            border-left: 4px solid var(--primary-color);
                            margin: 16px 0;
                            padding: 8px 16px;
                            background-color: #E8F5E9;
                            border-radius: 0 8px 8px 0;
                        }
                        
                        code {
                            background-color: #E8F5E9;
                            padding: 2px 4px;
                            border-radius: 4px;
                            font-family: monospace;
                        }
                        
                        pre {
                            background-color: #E8F5E9;
                            padding: 16px;
                            border-radius: 8px;
                            overflow-x: auto;
                        }
                    </style>
                </head>
                <body>
                    $fixedContent
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("file:///android_asset/posts/", htmlContent, "text/html", "UTF-8", null)
        }
    )
} 