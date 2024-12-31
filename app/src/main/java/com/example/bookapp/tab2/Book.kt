package com.example.bookapp.tab2

data class Book(
    var title: String = "",
    var imageResId: Int = 0,
    var author: String = "",
    var review: String = "",
    var imageFilePath: String? = null,
    var comments: MutableList<String> = mutableListOf() // New field to store comments
)
