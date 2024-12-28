package com.example.bookapp

import android.net.Uri

data class Contact(
    //val id: String, // 고유 ID
    var name: String,
    var insta : String,
    var phone: String,
    val profileImage: Int = R.drawable.default_profile
    //val profileImage: String? = null
)
