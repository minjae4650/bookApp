package com.example.bookapp

import Contact
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contactList: MutableList<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // ViewHolder 클래스 정의
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.contactImage)
        val nameTextView: TextView = itemView.findViewById(R.id.contactName)
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
        holder.contactImage.setImageResource(contact.profileImage)

        // 프로필 사진 설정
        // 프로필 이미지 리소스를 contact 객체에 따라 설정
        when (contact.profileImage) {
            R.drawable.default_profile -> holder.contactImage.setImageResource(R.drawable.default_profile)
            R.drawable.image1 -> holder.contactImage.setImageResource(R.drawable.image1)
            R.drawable.image2 -> holder.contactImage.setImageResource(R.drawable.image2)
            R.drawable.image3 -> holder.contactImage.setImageResource(R.drawable.image3)
            R.drawable.image4 -> holder.contactImage.setImageResource(R.drawable.image4)
            R.drawable.image5 -> holder.contactImage.setImageResource(R.drawable.image5)
            R.drawable.image6 -> holder.contactImage.setImageResource(R.drawable.image6)
            else -> holder.contactImage.setImageResource(R.drawable.default_profile) // 기본 이미지
        }

        // 클릭 이벤트 추가 - 상세 페이지 이동을 위해 클릭 시 onItemClick 호출
        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    // 아이템 수 반환
    override fun getItemCount() = contactList.size
}
