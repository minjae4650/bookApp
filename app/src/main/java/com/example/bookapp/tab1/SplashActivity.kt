package com.example.bookapp.tab1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.bookapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        // ImageView 가져오기
        val splashImage: ImageView = findViewById(R.id.splashImage)

        // GIF 로드 (Glide 사용)
        Glide.with(this).load(R.drawable.splash).into(splashImage)

        // 타이머가 끝나면 MainActivity로 이동
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish() // 현재 액티비티 종료
        }, 3000) // 3초 후 이동
    }
}
