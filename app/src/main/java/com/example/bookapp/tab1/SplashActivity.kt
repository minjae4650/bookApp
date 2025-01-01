package com.example.bookapp.tab1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        // ImageView에서 GIF 로드
        val imageView: ImageView = findViewById(R.id.splashImageView)

        // Glide를 사용하여 GIF 로드
        Glide.with(this)
            .asGif()
            .load(R.drawable.splash) // splash 화면에 사용할 GIF 파일
            .into(imageView)

        // 타이머가 끝나면 내부 실행
        Handler().postDelayed({
            // 앱의 MainActivity로 넘어가기
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            // 현재 액티비티 닫기
            finish()
        }, 2000) // 2초
    }
}
