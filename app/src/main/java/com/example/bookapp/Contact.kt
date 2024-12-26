package com.example.bookapp

import android.net.Uri

// Contact.kt
data class Contact(
    var name: String,
    var insta : String,
    var phone: String,
    // var profilePictureUri: Uri? // URI로 프로필 사진 저장
)
