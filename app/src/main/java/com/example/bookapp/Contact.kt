package com.example.bookapp

import android.net.Uri

data class Contact(
    var name: String,
    var insta : String,
    var phone: String,
    val profileImagePath: String? = null
    // var profilePictureUri: Uri? // URI로 프로필 사진 저장
)
