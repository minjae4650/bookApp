package com.example.bookapp.tab2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class CommentAdapter(
    private val comments: MutableList<String>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var onItemLongClick: ((Int) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (Int) -> Unit) {
        onItemLongClick = listener
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentText.text = comment

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(position)
            true
        }
    }

    override fun getItemCount(): Int = comments.size
}
