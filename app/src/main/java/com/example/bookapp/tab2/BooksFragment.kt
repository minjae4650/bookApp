package com.example.bookapp.tab2

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.bookapp.R
import com.example.bookapp.data.BookPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class BooksFragment : Fragment() {

    private val REQUEST_IMAGE_PICK = 100

    private lateinit var bookPreferences: BookPreferences
    private lateinit var bookAdapter: BookAdapter
    private var popupImageView: ImageView? = null
    private var tempBook: Book? = null
    private var selectedBook: Book? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_books, container, false)
        bookPreferences = BookPreferences(requireContext())
        val books = bookPreferences.getBooks()

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = BookAdapter(books) { book ->
            selectedBook = book
            tempBook = book.copy()
            showEditPopup(tempBook)
        }
        recyclerView.adapter = bookAdapter

        val floatingActionButton: FloatingActionButton = rootView.findViewById(R.id.add_book)
        floatingActionButton.setOnClickListener { showAddPopup() }

        return rootView
    }

    private fun copyImageToAppStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)
                ?: return null
            val fileName = "bookapp_img_${System.currentTimeMillis()}.jpg"
            val outputFile = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showAddPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        popupImageView = dialog.findViewById(R.id.bookImage)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReviewEditText = dialog.findViewById<EditText>(R.id.bookReviewEditText)
        val addButton = dialog.findViewById<Button>(R.id.editButton)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        tempBook = Book(title = "", imageFilePath = null)

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

            val books = bookAdapter.getBooks()
            books.add(newBook)
            bookAdapter.notifyItemInserted(books.size - 1)
            bookPreferences.saveBooks(books)
            dialog.dismiss()
        }

        closeButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showEditPopup(book: Book?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        popupImageView = dialog.findViewById(R.id.bookImage)
        val bookTitleTextView = dialog.findViewById<TextView>(R.id.bookTitleTextView)
        val bookAuthorTextView = dialog.findViewById<TextView>(R.id.bookAuthorTextView)
        val bookReviewTextView = dialog.findViewById<TextView>(R.id.bookReviewTextView)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val deleteButton = dialog.findViewById<Button>(R.id.buttonDelete)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        val commentsListView = dialog.findViewById<ListView>(R.id.commentsListView)
        val commentEditText = dialog.findViewById<EditText>(R.id.commentEditText)
        val addCommentButton = dialog.findViewById<Button>(R.id.addCommentButton)

        // 댓글 어댑터 초기화
        val commentsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            book?.comments ?: mutableListOf()
        )
        commentsListView.adapter = commentsAdapter

        // 댓글 추가 버튼 클릭 이벤트
        addCommentButton.setOnClickListener {
            val comment = commentEditText.text.toString().trim()
            if (comment.isNotEmpty()) {
                book?.comments?.add(comment)
                commentsAdapter.notifyDataSetChanged()
                commentEditText.text.clear()
                bookPreferences.saveBooks(bookAdapter.getBooks())
            }
        }

        // 댓글 삭제 기능 (길게 누르기)
        commentsListView.setOnItemLongClickListener { _, _, position, _ ->
            book?.comments?.let { comments ->
                val removedComment = comments.removeAt(position)
                commentsAdapter.notifyDataSetChanged()
                bookPreferences.saveBooks(bookAdapter.getBooks())
                Toast.makeText(requireContext(), "'$removedComment' 삭제됨", Toast.LENGTH_SHORT).show()
            }
            true
        }

        deleteButton.setOnClickListener {
            selectedBook?.let { sel ->
                val books = bookAdapter.getBooks()
                val position = books.indexOf(sel)
                books.removeAt(position)
                bookAdapter.notifyItemRemoved(position)
                bookPreferences.saveBooks(books)
            }
            dialog.dismiss()
        }

        closeButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedUri: Uri? = data?.data
            if (selectedUri != null) {
                val copiedPath = copyImageToAppStorage(selectedUri)
                if (copiedPath != null) {
                    tempBook?.imageFilePath = copiedPath
                    val bitmap = android.graphics.BitmapFactory.decodeFile(copiedPath)
                    popupImageView?.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(requireContext(), "이미지 복사 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
