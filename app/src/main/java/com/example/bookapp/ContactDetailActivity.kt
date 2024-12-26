package com.example.bookapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.R

class ContactDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        // View 초기화
        val profileImageView: ImageView = findViewById(R.id.profileImageView)
        val nameTextView: TextView = findViewById(R.id.detailName)
        val phoneTextView: TextView = findViewById(R.id.detailPhone)
        val instaTextView: TextView = findViewById(R.id.detailInsta)

        // Intent로부터 데이터 받기
        val name = intent.getStringExtra("CONTACT_NAME") ?: ""
        val phone = intent.getStringExtra("CONTACT_PHONE") ?: ""
        val insta = intent.getStringExtra("CONTACT_INSTAGRAM") ?: ""

        // 데이터 표시
        nameTextView.text = name
        phoneTextView.text = phone
        instaTextView.text = insta

        // 프로필 사진 기본 설정 (임시 이미지 사용)
        profileImageView.setImageResource(R.drawable.default_profile)
    }
}
