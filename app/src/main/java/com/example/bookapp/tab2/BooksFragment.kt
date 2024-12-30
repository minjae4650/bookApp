package com.example.bookapp.tab2

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class BooksFragment : Fragment() {
    private val REQUEST_IMAGE_PICK = 100
    private var selectedBook: Book? = null
    private lateinit var bookAdapter: BookAdapter
    private var popupImageView: ImageView? = null // Popup ImageView reference
    private var tempBook: Book? = null // Temporary book object

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_books, container, false)

        // Sample data
        val books = mutableListOf(
            Book("추가하기", R.drawable.edit_draw),
            Book("서울의 밤", R.drawable.book1, "저자 A", "이 책은 흥미로워요."),
            Book("도시 이야기", R.drawable.book2, "저자 B", "여행에 관한 이야기입니다.")
        )

        // Initialize RecyclerView
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = BookAdapter(books) { book ->
            if (book.title == "추가하기") {
                showAddPopup()
                return@BookAdapter
            }
            selectedBook = book
            tempBook = book.copy() // Create a temporary copy for editing
            showEditPopup(tempBook)
        }
        recyclerView.adapter = bookAdapter

        return rootView
    }

    private fun showAddPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        popupImageView = dialog.findViewById(R.id.bookImage)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReviewEditText = dialog.findViewById<EditText>(R.id.bookReviewEditText)
        val addButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        tempBook = Book(title = "", imageResId = R.drawable.image_placeholder)

        popupImageView?.setImageResource(R.drawable.image_placeholder)
        bookTitleEditText.visibility = View.VISIBLE
        bookAuthorEditText.visibility = View.VISIBLE
        bookReviewEditText.visibility = View.VISIBLE
        addButton.text = "추가"

        popupImageView?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        addButton.setOnClickListener {
            val newBook = tempBook?.copy(
                title = bookTitleEditText.text.toString(),
                author = bookAuthorEditText.text.toString(),
                review = bookReviewEditText.text.toString()
            ) ?: return@setOnClickListener

            if (tempBook?.imageUri != null) {
                newBook.imageUri = tempBook?.imageUri
            }

            // 새 책을 리스트 맨 앞에 삽입
            val books = bookAdapter.getBooks()
            books.add(1, newBook) // "추가하기" 바로 다음에 추가
            bookAdapter.notifyItemInserted(1)

            dialog.dismiss()
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditPopup(book: Book?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        popupImageView = dialog.findViewById(R.id.bookImage)
        val editIcon = dialog.findViewById<ImageView>(R.id.editIcon)
        val bookTitleTextView = dialog.findViewById<TextView>(R.id.bookTitleTextView)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorTextView = dialog.findViewById<TextView>(R.id.bookAuthorTextView)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReviewTextView = dialog.findViewById<TextView>(R.id.bookReviewTextView)
        val bookReviewEditText = dialog.findViewById<EditText>(R.id.bookReviewEditText)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        if (book != null) {
            if (book.imageUri != null) {
                popupImageView?.setImageURI(book.imageUri)
            } else {
                popupImageView?.setImageResource(book.imageResId)
            }
            bookTitleTextView.text = "제목: ${book.title}"
            bookAuthorTextView.text = "저자: ${book.author}"
            bookReviewTextView.text = book.review
        }

        editIcon.visibility = View.GONE
        bookTitleEditText.visibility = View.GONE
        bookAuthorEditText.visibility = View.GONE
        bookReviewEditText.visibility = View.GONE

        editButton.setOnClickListener {
            val isEditing = bookTitleEditText.visibility == View.VISIBLE

            if (isEditing) {
                selectedBook?.let {
                    it.title = bookTitleEditText.text.toString()
                    it.author = bookAuthorEditText.text.toString()
                    it.review = bookReviewEditText.text.toString()

                    if (tempBook?.imageUri != null) {
                        it.imageUri = tempBook?.imageUri
                    }

                    bookTitleTextView.text = "제목: ${it.title}"
                    bookAuthorTextView.text = "저자: ${it.author}"
                    bookReviewTextView.text = it.review

                    val position = bookAdapter.getBooks().indexOf(it)
                    bookAdapter.updateBook(position, it)
                }

                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE
                bookReviewTextView.visibility = View.VISIBLE

                bookTitleEditText.visibility = View.GONE
                bookAuthorEditText.visibility = View.GONE
                bookReviewEditText.visibility = View.GONE

                editIcon.visibility = View.GONE
                editButton.text = "수정"
            } else {
                bookTitleTextView.visibility = View.GONE
                bookAuthorTextView.visibility = View.GONE
                bookReviewTextView.visibility = View.GONE

                bookTitleEditText.visibility = View.VISIBLE
                bookAuthorEditText.visibility = View.VISIBLE
                bookReviewEditText.visibility = View.VISIBLE

                bookTitleEditText.setText(selectedBook?.title)
                bookAuthorEditText.setText(selectedBook?.author)
                bookReviewEditText.setText(selectedBook?.review)

                editIcon.visibility = View.VISIBLE
                editButton.text = "저장"
            }
        }

        val imageClickListener = View.OnClickListener {
            if (bookTitleEditText.visibility == View.VISIBLE) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            } else {
                val fullScreenDialog = Dialog(requireContext())
                fullScreenDialog.setContentView(R.layout.fullscreen_image_layout)

                val fullScreenImage = fullScreenDialog.findViewById<ImageView>(R.id.fullScreenImage)
                if (book?.imageUri != null) {
                    fullScreenImage.setImageURI(book.imageUri)
                } else {
                    fullScreenImage.setImageResource(book?.imageResId ?: R.drawable.plus_draw)
                }

                fullScreenDialog.show()
            }
        }

        popupImageView?.setOnClickListener(imageClickListener)
        editIcon.setOnClickListener(imageClickListener)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                tempBook?.imageUri = selectedImageUri
                popupImageView?.setImageURI(selectedImageUri)
            }
        }
    }
}
