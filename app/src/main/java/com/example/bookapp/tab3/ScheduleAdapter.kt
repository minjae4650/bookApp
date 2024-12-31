package com.example.bookapp.tab3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class ScheduleAdapter(private val schedules: List<ScheduleItem>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    data class ScheduleItem(val title: String, val color: Int)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorIndicator: View = view.findViewById(R.id.view_color_indicator)
        val titleTextView: TextView = view.findViewById(R.id.tv_schedule_title)
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
    }

    override fun getItemCount(): Int = schedules.size
}
