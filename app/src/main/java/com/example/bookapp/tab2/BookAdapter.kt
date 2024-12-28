package com.example.bookapp.tab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class BookAdapter(
    private val books: MutableList<Book>, // MutableList로 변경
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        if (book.imageUri != null) {
            holder.bookImage.setImageURI(book.imageUri)
        } else {
            holder.bookImage.setImageResource(book.imageResId)
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = books.size

    fun getBooks(): MutableList<Book> = books

    // 데이터 업데이트 메서드 추가
    fun updateBook(position: Int, updatedBook: Book) {
        books[position] = updatedBook
        notifyItemChanged(position)
    }
}
