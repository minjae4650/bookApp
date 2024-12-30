package com.example.bookapp.tab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

class ContactAdapter(
    private val contactList: MutableList<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // 롱 클릭 리스너를 위한 변수
    private var onItemLongClick: ((Contact) -> Unit)? = null

    // 롱 클릭 리스너 설정 메서드
    fun setOnItemLongClickListener(listener: (Contact) -> Unit) {
        onItemLongClick = listener
    }

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

        // 프로필 이미지 설정
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

        // 클릭 이벤트 - 상세 페이지 이동
        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }

        // 롱 클릭 이벤트 처리
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(contact)
            true // 롱 클릭이 처리되었음을 알려줌
        }
    }

    // 아이템 수 반환
    override fun getItemCount() = contactList.size
}
