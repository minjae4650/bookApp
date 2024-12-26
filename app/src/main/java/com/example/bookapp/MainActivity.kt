package com.example.bookapp

import com.example.bookapp.tab2.BookAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.tab2.Book

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 샘플 데이터
        val books = listOf(
            Book("The Great Gatsby", "F. Scott Fitzgerald"),
            Book("To Kill a Mockingbird", "Harper Lee"),
            Book("1984", "George Orwell"),
            Book("Pride and Prejudice", "Jane Austen"),
            Book("The Catcher in the Rye", "J.D. Salinger")
        )

        // RecyclerView 초기화
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // 세로 방향 기본 설정
        recyclerView.adapter = BookAdapter(books) // 어댑터 연결
    }
}