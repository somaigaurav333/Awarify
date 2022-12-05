package com.example.networkingpr

data class News(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)