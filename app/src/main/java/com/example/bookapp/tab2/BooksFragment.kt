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
import androidx.recyclerview.widget.LinearLayoutManager
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

    // 팝업에서 사용할 ImageView (Add / Detail 등에서 공통으로 사용 가능)
    private var popupImageView: ImageView? = null

    // “편집 중” 혹은 “새로 추가” 시 임시로 쓸 Book
    private var tempBook: Book? = null

    // 사용자가 목록에서 선택한 Book (편집 시)
    private var selectedBook: Book? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_books, container, false)

        // SharedPreferences 초기화
        bookPreferences = BookPreferences(requireContext())

        // 저장된 책 목록 불러오기
        val books = bookPreferences.getBooks()

        // Initialize RecyclerView
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = BookAdapter(books) { book ->
            // 아이템 클릭 시 -> 상세 팝업(댓글 가능) 띄우기
            showDetailPopup(book)
        }
        recyclerView.adapter = bookAdapter

        // 플로팅 버튼 클릭 시: 새 책 추가 팝업
        val floatingActionButton: FloatingActionButton = rootView.findViewById(R.id.add_book)
        floatingActionButton.setOnClickListener {
            showAddPopup()
        }

        return rootView
    }

    /**
     * 갤러리에서 선택한 이미지를 앱 내부 저장소로 복사 -> 그 경로 반환
     */
    private fun copyImageToAppStorage(uri: Uri): String? {
        return try {
            // 1) contentResolver로 InputStream 열기
            val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)
                ?: return null

            // 2) 내부 저장소(filesDir) 쪽에 파일 생성
            val fileName = "bookapp_img_${System.currentTimeMillis()}.jpg"
            val outputDir = requireContext().filesDir
            val outputFile = File(outputDir, fileName)

            // 3) 파일 복사
            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            // 4) 파일 경로 반환
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * “새 책 추가” 팝업
     */
    private fun showAddPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)

        // 팝업 사이즈 설정
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
        val deleteButton = dialog.findViewById<Button>(R.id.buttonDelete)

        // 새 책 추가시에는 delete 버튼 숨기는 게 자연스러울 수도 있음
        deleteButton.visibility = View.GONE

        // tempBook 초기화
        tempBook = Book(title = "", imageResId = R.drawable.image_placeholder)

        // 기본 이미지
        popupImageView?.setImageResource(R.drawable.image_placeholder)

        // 버튼 문구를 "ADD" 로 변경
        addButton.text = "ADD"

        // 이미지 클릭 시 -> 갤러리에서 선택
        popupImageView?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // ADD 버튼 클릭
        addButton.setOnClickListener {
            val newBook = tempBook?.copy(
                title = bookTitleEditText.text.toString(),
                author = bookAuthorEditText.text.toString(),
                review = bookReviewEditText.text.toString()
            ) ?: return@setOnClickListener

            val books = bookAdapter.getBooks()

            books.add(0, newBook)
            bookAdapter.notifyItemInserted(0)

            // SharedPreferences 저장
            bookPreferences.saveBooks(books)

            dialog.dismiss()
        }

        // close 버튼
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * “책 상세/댓글” 팝업
     */
    private fun showDetailPopup(book: Book) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.edit_detail_popup)

        // 팝업 사이즈
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // 참조
        popupImageView = dialog.findViewById(R.id.bookImage)
        val editIcon = dialog.findViewById<ImageView>(R.id.editIcon)
        val bookTitleTextView = dialog.findViewById<TextView>(R.id.bookTitleTextView)
        val bookTitleEditText = dialog.findViewById<EditText>(R.id.bookTitleEditText)
        val bookAuthorTextView = dialog.findViewById<TextView>(R.id.bookAuthorTextView)
        val bookAuthorEditText = dialog.findViewById<EditText>(R.id.bookAuthorEditText)
        val bookReviewTextView = dialog.findViewById<TextView>(R.id.bookReviewTextView)
        val bookReviewEditText = dialog.findViewById<EditText>(R.id.bookReviewEditText)
        val editButton = dialog.findViewById<Button>(R.id.editButton)
        val deleteButton = dialog.findViewById<Button>(R.id.buttonDelete)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        val commentEditText = dialog.findViewById<EditText>(R.id.commentEditText)
        val addCommentButton = dialog.findViewById<Button>(R.id.addCommentButton)
        val commentsRecyclerView = dialog.findViewById<RecyclerView>(R.id.commentsRecyclerView)

        editIcon.visibility = View.GONE

        // 초기 상태: TextView에 책 정보 표시, EditText는 GONE
        bookAuthorTextView.text = "Author : ${book.author}"
        bookReviewTextView.text = "Description : ${book.review}"

        // 선택된 Book 기억
        selectedBook = book
        tempBook = book.copy()

        // 이미지 세팅
        if (!book.imageFilePath.isNullOrEmpty()) {
            val file = File(book.imageFilePath)
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                popupImageView?.setImageBitmap(bitmap)
            } else {
                popupImageView?.setImageResource(book.imageResId)
            }
        } else {
            popupImageView?.setImageResource(book.imageResId)
        }

        // 제목 표시
        bookTitleTextView.text = "Title: ${book.title}"
        bookTitleEditText.visibility = View.GONE

        // 댓글 리사이클러뷰 설정
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val commentAdapter = CommentAdapter(book.comments)
        commentsRecyclerView.adapter = commentAdapter

        // 댓글 추가 버튼
        addCommentButton.setOnClickListener {
            val newComment = commentEditText.text.toString().trim()
            if (newComment.isNotEmpty()) {
                // 실제 book 객체에 추가
                book.comments.add(newComment)
                commentAdapter.notifyItemInserted(book.comments.size - 1)
                commentsRecyclerView.scrollToPosition(book.comments.size - 1)
                commentEditText.setText("")

                // 즉시 SharedPreferences에 저장
                bookPreferences.saveBooks(bookAdapter.getBooks())
            }
        }

        // Edit 버튼 (토글 방식)
        editButton.setOnClickListener {
            // EditText가 visible이면 -> Save 시점
            val isEditing = (bookTitleEditText.visibility == View.VISIBLE)
            if (isEditing) {
                selectedBook?.let { sel ->
                    // 제목, 저자, 감상평 업데이트
                    sel.title = bookTitleEditText.text.toString()
                    sel.author = bookAuthorEditText.text.toString()
                    sel.review = bookReviewEditText.text.toString()

                    // 이미지 경로 업데이트
                    if (!tempBook?.imageFilePath.isNullOrEmpty()) {
                        sel.imageFilePath = tempBook?.imageFilePath
                    }

                    // 어댑터 갱신
                    val position = bookAdapter.getBooks().indexOf(sel)
                    bookAdapter.updateBook(position, sel)

                    // SharedPreferences에 업데이트된 리스트 저장
                    bookPreferences.saveBooks(bookAdapter.getBooks())

                    // TextView에 새 값 표시
                    bookTitleTextView.text = "Title : ${sel.title}"
                    bookAuthorTextView.text = "Author : ${sel.author}"
                    bookReviewTextView.text = sel.review
                }

                // TextView 보이기, EditText 숨기기
                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE
                bookReviewTextView.visibility = View.VISIBLE
                bookTitleEditText.visibility = View.GONE
                bookAuthorEditText.visibility = View.GONE
                bookReviewEditText.visibility = View.GONE

                editButton.text = "Edit"
                editIcon.visibility = View.GONE

                // SharedPreferences 저장
                bookPreferences.saveBooks(bookAdapter.getBooks())
            } else {
                bookTitleEditText.setText(book.title)
                bookAuthorEditText.setText(book.author)
                bookReviewEditText.setText(book.review)

                bookTitleTextView.visibility = View.GONE
                bookAuthorTextView.visibility = View.GONE
                bookReviewTextView.visibility = View.GONE

                bookTitleEditText.visibility = View.VISIBLE
                bookAuthorEditText.visibility = View.VISIBLE
                bookReviewEditText.visibility = View.VISIBLE

                editButton.text = "Save"
                editIcon.visibility = View.VISIBLE
            }
        }

        // 삭제 버튼
        deleteButton.setOnClickListener {
            val books = bookAdapter.getBooks()
            val position = books.indexOf(book)
            if (position != -1) {
                books.removeAt(position)
                bookAdapter.notifyItemRemoved(position)
                bookPreferences.saveBooks(books)
            }
            Toast.makeText(requireContext(), "책이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Close 버튼
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 이미지 클릭 이벤트
        val imageClickListener = View.OnClickListener {
            // 편집 모드일 때만 갤러리에서 선택할 수 있게 한다거나,
            // 아니면 풀 스크린으로 보여주는 등 필요 로직
            val isEditing = (bookTitleEditText.visibility == View.VISIBLE)
            if (isEditing) {
                // 갤러리에서 이미지 선택
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            } else {
                // 풀 스크린 이미지
                val fullScreenDialog = Dialog(requireContext())
                fullScreenDialog.setContentView(R.layout.fullscreen_image_layout)
                val fullScreenImage = fullScreenDialog.findViewById<SubsamplingScaleImageView>(R.id.fullScreenImage)

                if (!book.imageFilePath.isNullOrEmpty()) {
                    val file = File(book.imageFilePath)
                    if (file.exists()) {
                        fullScreenImage.setImage(ImageSource.uri(Uri.fromFile(file)))
                    } else {
                        fullScreenImage.setImage(ImageSource.resource(book.imageResId))
                    }
                } else {
                    fullScreenImage.setImage(ImageSource.resource(R.drawable.image_placeholder))
                }
                fullScreenDialog.show()
            }
        }
        popupImageView?.setOnClickListener(imageClickListener)
        editIcon.setOnClickListener(imageClickListener)

        dialog.show()
    }

    /**
     * 갤러리에서 이미지 선택 후 돌아옴
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedUri: Uri? = data?.data
            if (selectedUri != null) {
                // 내부 저장소로 복사
                val copiedPath = copyImageToAppStorage(selectedUri)
                if (copiedPath != null) {
                    tempBook?.imageFilePath = copiedPath
                    // 미리보기
                    val bitmap = android.graphics.BitmapFactory.decodeFile(copiedPath)
                    popupImageView?.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(requireContext(), "이미지 복사 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
