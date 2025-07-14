package com.example.wiki_app.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ArticleText(
    content: String,
    baseUrl: String, // baseUrl을 파라미터로 받도록 수정
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
        update = { webView: WebView ->
            // HTML 파일에 이미 <base> 태그가 있을 수 있으므로, 동적으로 base URL을 설정하는 것이 더 안전합니다.
            // 정규식으로 src 속성을 수정하는 대신, loadDataWithBaseURL을 올바르게 사용합니다.
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                    $content
                </body>
                </html>
            """.trimIndent()
            // loadDataWithBaseURL의 첫 번째 인자인 baseUrl을 올바르게 설정합니다.
            // 이 baseUrl은 HTML 파일의 위치를 기준으로 상대 경로를 해석하는 기준점이 됩니다.
            webView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null)
        }
    )
}
