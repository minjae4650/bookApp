package com.example.bookapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
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

        // Sample data
        val books = mutableListOf<Book>(
            Book("추가하기", R.drawable.plus_draw), // Add button
            Book("서울의 밤", R.drawable.book1),
            Book("도시 이야기", R.drawable.book2)
        )

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2-column grid layout
        recyclerView.adapter = BookAdapter(books) { book ->
            if (book.title == "추가하기") {
                showEditPopup(null) // Add popup
            } else {
                showEditPopup(book) // Edit existing book popup
            }
        }
    }

    private fun showEditPopup(book: Book?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_layout)

        // Set popup size
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }

        // Initialize popup elements
        val bookImage = dialog.findViewById<ImageView>(R.id.bookImage)
        val bookTitleTextView = dialog.findViewById<TextView>(R.id.bookTitleTextView)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorTextView = dialog.findViewById<TextView>(R.id.bookAuthorTextView)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReview = dialog.findViewById<EditText>(R.id.bookReview)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        // Initialize data
        if (book != null) {
            bookImage.setImageResource(book.imageResId)
            bookTitleTextView.text = book.title
            bookAuthorTextView.text = "저자 정보 없음" // Replace with book author if available
        } else {
            bookImage.setImageResource(R.drawable.plus_draw)
            bookTitleTextView.text = "새로운 책 추가"
            bookAuthorTextView.text = ""
        }

        // Close button
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Edit button logic
        editButton.setOnClickListener {
            val isEditing = bookTitleTextView.visibility == View.VISIBLE

            if (isEditing) {
                // Show EditTexts and hide TextViews
                bookTitleTextView.visibility = View.GONE
                bookAuthorTextView.visibility = View.GONE

                bookTitleEditText.visibility = View.VISIBLE
                bookAuthorEditText.visibility = View.VISIBLE

                bookTitleEditText.setText(bookTitleTextView.text.toString())
                bookAuthorEditText.setText(bookAuthorTextView.text.toString())

                editButton.text = "저장"
            } else {
                // Save edited content
                val newTitle = bookTitleEditText.text.toString()
                val newAuthor = bookAuthorEditText.text.toString()

                bookTitleTextView.text = newTitle
                bookAuthorTextView.text = newAuthor

                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE

                bookTitleEditText.visibility = View.GONE
                bookAuthorEditText.visibility = View.GONE

                if (book != null) {
                    book.title = newTitle
                }

                // Update RecyclerView if necessary
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}