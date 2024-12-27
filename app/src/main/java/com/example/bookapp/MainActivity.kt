package com.example.bookapp

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.tab2.Book
import com.example.bookapp.tab2.BookAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 샘플 데이터 생성
        val books = mutableListOf<Book>(
            Book("추가하기", R.drawable.plus_draw), // 추가하기 버튼
            Book("서울의 밤", R.drawable.book1),
            Book("도시 이야기", R.drawable.book2)
        )

        // RecyclerView 초기화
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2열 그리드 레이아웃
        recyclerView.adapter = BookAdapter(books) { book ->
            if (book.title == "추가하기") {
                showEditPopup(null) // 추가하기 팝업
            } else {
                showEditPopup(book) // 기존 책 정보 팝업
            }
        }
    }

    private fun showEditPopup(book: Book?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_layout)

        // 팝업 크기 설정
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, // 가로 전체 화면
                WindowManager.LayoutParams.MATCH_PARENT  // 세로 전체 화면
            )
        }

        // 팝업 내부 요소 초기화
        val bookImage = dialog.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = dialog.findViewById<TextView>(R.id.bookTitle)
        val bookAuthor = dialog.findViewById<TextView>(R.id.bookAuthor)
        val bookReview = dialog.findViewById<EditText>(R.id.bookReview)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        // 데이터 초기화
        if (book != null) {
            bookImage.setImageResource(book.imageResId)
            bookTitle.text = book.title
            bookAuthor.text = "저자 정보 없음" // 필요하면 책의 저자 정보로 대체
        } else {
            bookImage.setImageResource(R.drawable.plus_draw)
            bookTitle.text = "새로운 책 추가"
            bookAuthor.text = ""
        }

        // 닫기 버튼
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 수정 버튼
        editButton.setOnClickListener {
            // 입력 데이터 처리
            val updatedReview = bookReview.text.toString()
            // 데이터를 저장하거나 RecyclerView 업데이트 로직 추가
            dialog.dismiss()
        }

        dialog.show()
    }
}
