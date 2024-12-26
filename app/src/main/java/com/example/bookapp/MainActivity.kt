package com.example.bookapp

import com.example.bookapp.tab2.BookAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.tab2.Book

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 샘플 데이터
        val books = listOf(
            Book("서울에 밤이 내렸을 때", "지은이1", R.drawable.book1),
            Book("서울에 삽니다", "지은이2", R.drawable.book2),
            Book("다시 서울", "지은이3", R.drawable.book3),
            Book("한강길 따라 서울 둘러보기", "지은이4", R.drawable.book4),
            Book("내가 알지 못했던 도심 속 서울", "지은이5", R.drawable.book5)
        )
        // RecyclerView 초기화
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // 세로 방향 기본 설정
        recyclerView.adapter = BookAdapter(books) // 어댑터 연결
    }
}