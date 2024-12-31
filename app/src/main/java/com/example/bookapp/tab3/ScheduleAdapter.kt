package com.example.bookapp.tab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class ScheduleAdapter(
    private val schedules: MutableList<ScheduleItem>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    data class ScheduleItem(val title: String, val color: Int)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorIndicator: View = view.findViewById(R.id.view_color_indicator)
        val titleTextView: TextView = view.findViewById(R.id.tv_schedule_title)
        val trashIcon: ImageView = view.findViewById(R.id.trashIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schedules[position]
        holder.colorIndicator.setBackgroundColor(item.color)
        holder.titleTextView.text = item.title

        // 트래쉬 아이콘 클릭 리스너
        holder.trashIcon.setOnClickListener {
            onDeleteClick(item.title) // 삭제 콜백 호출
            schedules.removeAt(position) // 리스트에서 삭제
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, schedules.size)
        }
    }

    override fun getItemCount(): Int = schedules.size
}
