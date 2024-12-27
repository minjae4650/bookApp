package com.example.bookapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
    private val REQUEST_IMAGE_PICK = 100
    private var selectedBook: Book? = null
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sample data
        val books = mutableListOf<Book>(
            Book("추가하기", R.drawable.plus_draw),
            Book("서울의 밤", R.drawable.book1, "저자 A", "이 책은 흥미로워요."),
            Book("도시 이야기", R.drawable.book2, "저자 B", "여행에 관한 이야기입니다.")
        )

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        bookAdapter = BookAdapter(books) { book ->
            selectedBook = book
            showEditPopup(book)
        }
        recyclerView.adapter = bookAdapter
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
        val editIcon = dialog.findViewById<ImageView>(R.id.editIcon)
        val bookTitleTextView = dialog.findViewById<TextView>(R.id.bookTitleTextView)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorTextView = dialog.findViewById<TextView>(R.id.bookAuthorTextView)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReviewTextView = dialog.findViewById<TextView>(R.id.bookReviewTextView)
        val bookReviewEditText = dialog.findViewById<EditText>(R.id.bookReviewEditText)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        // Initialize data
        if (book != null) {
            if (book.imageUri != null) {
                bookImage.setImageURI(book.imageUri)
            } else {
                bookImage.setImageResource(book.imageResId)
            }
            bookTitleTextView.text = "제목: ${book.title}"
            bookAuthorTextView.text = "저자: ${book.author}"
            bookReviewTextView.text = book.review
        }

        editIcon.visibility = View.GONE

        // Enable editing
        editButton.setOnClickListener {editButton.setOnClickListener {
            val isEditing = bookTitleTextView.visibility == View.VISIBLE

            if (isEditing) {
                // Switch to edit mode
                bookTitleTextView.visibility = View.GONE
                bookAuthorTextView.visibility = View.GONE
                bookReviewTextView.visibility = View.GONE

                bookTitleEditText.visibility = View.VISIBLE
                bookAuthorEditText.visibility = View.VISIBLE
                bookReviewEditText.visibility = View.VISIBLE

                bookTitleEditText.setText(book?.title)
                bookAuthorEditText.setText(book?.author)
                bookReviewEditText.setText(book?.review)

                editIcon.visibility = View.VISIBLE // Show edit icon in edit mode

                editButton.text = "저장"
            } else {
                // Save changes
                book?.let {
                    it.title = bookTitleEditText.text.toString()
                    it.author = bookAuthorEditText.text.toString()
                    it.review = bookReviewEditText.text.toString()
                    bookAdapter.notifyDataSetChanged()
                }

                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE
                bookReviewTextView.visibility = View.VISIBLE

                bookTitleEditText.visibility = View.GONE
                bookAuthorEditText.visibility = View.GONE
                bookReviewEditText.visibility = View.GONE

                editIcon.visibility = View.GONE // Hide edit icon when saved

                editButton.text = "수정"
            }
        }

            val isEditing = bookTitleTextView.visibility == View.VISIBLE

            if (isEditing) {
                // Switch to edit mode
                bookTitleTextView.visibility = View.GONE
                bookAuthorTextView.visibility = View.GONE
                bookReviewTextView.visibility = View.GONE

                bookTitleEditText.visibility = View.VISIBLE
                bookAuthorEditText.visibility = View.VISIBLE
                bookReviewEditText.visibility = View.VISIBLE

                bookTitleEditText.setText(book?.title)
                bookAuthorEditText.setText(book?.author)
                bookReviewEditText.setText(book?.review)

                editIcon.visibility = View.VISIBLE // Show edit icon in edit mode

                editButton.text = "저장"
            } else {
                // Save changes
                book?.let {
                    it.title = bookTitleEditText.text.toString()
                    it.author = bookAuthorEditText.text.toString()
                    it.review = bookReviewEditText.text.toString()
                    bookAdapter.notifyDataSetChanged()
                }

                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE
                bookReviewTextView.visibility = View.VISIBLE

                bookTitleEditText.visibility = View.GONE
                bookAuthorEditText.visibility = View.GONE
                bookReviewEditText.visibility = View.GONE

                editIcon.visibility = View.GONE // Hide edit icon when saved

                editButton.text = "수정"
            }
        }

        // Click event for image and edit icon
        val imageClickListener = View.OnClickListener {
            if (bookTitleTextView.visibility == View.VISIBLE) {
                // Fullscreen image view when not in editing mode
                val fullScreenDialog = Dialog(this)
                fullScreenDialog.setContentView(R.layout.fullscreen_image_layout)

                val fullScreenImage = fullScreenDialog.findViewById<ImageView>(R.id.fullScreenImage)
                if (book?.imageUri != null) {
                    fullScreenImage.setImageURI(book.imageUri)
                } else {
                    fullScreenImage.setImageResource(book?.imageResId ?: R.drawable.plus_draw)
                }

                fullScreenDialog.show()
            } else {
                // Open gallery to select image when in editing mode
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            }
        }
        bookImage.setOnClickListener(imageClickListener)
        editIcon.setOnClickListener(imageClickListener)

        // Close popup
        closeButton.setOnClickListener {
            editIcon.visibility = View.GONE
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                selectedBook?.let {
                    it.imageUri = selectedImageUri
                    showEditPopup(it) // Reload the popup to reflect the updated image
                }
            }
        }
    }
}
