package com.example.bookapp.tab2

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R
import java.io.File

class BookAdapter(
    private val books: MutableList<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        // 1) 내부 저장소에 복사된 파일이 있으면 그 이미지 로드
        if (!book.imageFilePath.isNullOrEmpty()) {
            val file = File(book.imageFilePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.bookImage.setImageBitmap(bitmap)
            } else {
                // 파일이 없으면 기본 리소스 사용
                holder.bookImage.setImageResource(book.imageResId)
            }
        } else {
            // 2) imageFilePath가 없으면 (ex. "추가하기" 아이템)
            holder.bookImage.setImageResource(book.imageResId)
        }

        // 아이템 클릭
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = books.size

    // 외부에서 리스트 참조가 필요하면 사용
    fun getBooks(): MutableList<Book> = books

    // 특정 position의 Book 객체를 업데이트 후 notify
    fun updateBook(position: Int, updatedBook: Book) {
        books[position] = updatedBook
        notifyItemChanged(position)
    }
}
