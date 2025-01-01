package com.example.bookapp.tab2

import com.example.bookapp.R

data class Book(
    var title: String = "",
    var imageResId: Int = R.drawable.image_placeholder,
    var author: String = "",
    var review: String = "",
    var comments: MutableList<String> = mutableListOf(), // 댓글 리스트 추가
    var imageFilePath: String? = null  // 내부 저장소에 복사된 파일 경로
)
