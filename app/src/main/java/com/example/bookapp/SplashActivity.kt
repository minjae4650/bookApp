package com.example.bookapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        // 타이머가 끝나면 내부 실행
        Handler().postDelayed(Runnable {
            // 앱의 MainActivity로 넘어가기
            val i = Intent(this@SplashActivity,MainActivity::class.java)
            startActivity(i)
            // 현재 액티비티 닫기
            finish()
        }, 3000) // 3초
    }
}