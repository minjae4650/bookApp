package com.example.bookapp.tab2

data class Book(
    var title: String = "",
    var imageResId: Int = 0,
    var author: String = "",
    var review: String = "",
    var imageFilePath: String? = null  // 내부 저장소에 복사된 파일 경로
)
