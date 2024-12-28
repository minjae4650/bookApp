package com.example.bookapp.tab2

import android.net.Uri

data class Book(
    var title: String,
    val imageResId: Int = 0,
    var author: String = "미상",
    var review: String = "",
    var imageUri: Uri? = null // User-selected image URI
)
