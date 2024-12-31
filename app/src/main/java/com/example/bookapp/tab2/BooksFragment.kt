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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BooksFragment : Fragment() {

    private val REQUEST_IMAGE_PICK = 100

    private lateinit var bookPreferences: BookPreferences
    private lateinit var bookAdapter: BookAdapter

    // 팝업에서 사용할 ImageView
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

        // 비어있으면 "추가하기" + 샘플데이터
        if (books.isEmpty()) {
            books.add(Book("추가하기", R.drawable.edit_draw))
        } else {
            // 이미 데이터가 있더라도, "추가하기"가 없으면 0번에 추가
            val hasAddItem = books.any { it.title == "추가하기" }
            if (!hasAddItem) {
                books.add(0, Book("추가하기", R.drawable.edit_draw))
            }
        }

        // Initialize RecyclerView
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = BookAdapter(books) { book ->
            if (book.title == "추가하기") {
                showAddPopup()
                return@BookAdapter
            }
            selectedBook = book
            tempBook = book.copy()
            showEditPopup(tempBook)
        }
        recyclerView.adapter = bookAdapter

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

            val books = bookAdapter.getBooks()
            books.add(1, newBook)
            bookAdapter.notifyItemInserted(1)

            // SharedPreferences 저장
            bookPreferences.saveBooks(books)

            dialog.dismiss()
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * “책 편집” 팝업
     */
    private fun showEditPopup(book: Book?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_archive_popup)

        // 전체화면 or 원하는 사이즈
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
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        if (book != null) {
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
            bookTitleTextView.text = "제목: ${book.title}"
            bookAuthorTextView.text = "저자: ${book.author}"
            bookReviewTextView.text = book.review
        }

        editIcon.visibility = View.GONE
        bookTitleEditText.visibility = View.GONE
        bookAuthorEditText.visibility = View.GONE
        bookReviewEditText.visibility = View.GONE
        editButton.text = "수정"

        editButton.setOnClickListener {
            val isEditing = (bookTitleEditText.visibility == View.VISIBLE)
            if (isEditing) {
                selectedBook?.let { sel ->
                    sel.title = bookTitleEditText.text.toString()
                    sel.author = bookAuthorEditText.text.toString()
                    sel.review = bookReviewEditText.text.toString()

                    // 내부 저장소 파일 경로 업데이트
                    if (!tempBook?.imageFilePath.isNullOrEmpty()) {
                        sel.imageFilePath = tempBook?.imageFilePath
                    }

                    // 어댑터 갱신
                    val position = bookAdapter.getBooks().indexOf(sel)
                    bookAdapter.updateBook(position, sel)

                    // SharedPreferences 저장
                    bookPreferences.saveBooks(bookAdapter.getBooks())

                    // TextView 업데이트
                    bookTitleTextView.text = "제목: ${sel.title}"
                    bookAuthorTextView.text = "저자: ${sel.author}"
                    bookReviewTextView.text = sel.review
                }

                bookTitleTextView.visibility = View.VISIBLE
                bookAuthorTextView.visibility = View.VISIBLE
                bookReviewTextView.visibility = View.VISIBLE
                editIcon.visibility = View.GONE
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
                val fullScreenImage = fullScreenDialog.findViewById<SubsamplingScaleImageView>(R.id.fullScreenImage)

                if (!book?.imageFilePath.isNullOrEmpty()) {
                    val file = File(book!!.imageFilePath)
                    if (file.exists()) {
                        // SubsamplingScaleImageView에 파일 경로로 이미지 설정
                        fullScreenImage.setImage(ImageSource.uri(Uri.fromFile(file)))
                    } else {
                        // 기본 리소스 이미지를 설정할 경우
                        fullScreenImage.setImage(ImageSource.resource(book!!.imageResId))
                    }
                } else {
                    // 기본 리소스 이미지를 설정
                    fullScreenImage.setImage(ImageSource.resource(book?.imageResId ?: R.drawable.edit_draw))
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
