package com.example.bookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contactList: MutableList<Contact>, private val onItemClick: (Contact) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // ViewHolder 클래스 정의
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contactName)
        // val phoneTextView: TextView = itemView.findViewById(R.id.contactPhone)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.nameTextView.text = contact.name
        // holder.phoneTextView.text = contact.phone

        // 클릭 이벤트 추가 - 상세 페이지 이동을 위해 클릭 시 onItemClick 호출
        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    // 아이템 수 반환
    override fun getItemCount() = contactList.size
}
