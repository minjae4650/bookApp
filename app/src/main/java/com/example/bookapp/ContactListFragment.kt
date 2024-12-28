package com.example.bookapp

import ContactPreferences
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactListFragment : Fragment() {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactPreferences: ContactPreferences
    private var contactList = mutableListOf<Contact>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        contactPreferences = ContactPreferences(requireContext()) // ContactPreferences 초기화

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val addContactButton: Button = view.findViewById(R.id.addContactButton)

        // 어댑터 초기화
        contactList = contactPreferences.getContacts().toMutableList() // SharedPreferences에서 데이터 불러오기
        contactAdapter = ContactAdapter(contactList) { contact ->
            // 클릭 시, 수정할 연락처의 세부 정보로 이동
            val contactDetailFragment = ContactDetailFragment().apply {
                val bundle = Bundle().apply {
                    putString("CONTACT_NAME", contact.name)
                    putString("CONTACT_PHONE", contact.phone)
                    putString("CONTACT_INSTAGRAM", contact.insta)
                    putInt("CONTACT_IMAGE_RES_ID", contact.profileImage) // 이미지 리소스 ID 추가
                }
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, contactDetailFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = contactAdapter

        addContactButton.setOnClickListener {
            showAddContactDialog()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // onResume에서 연락처 목록을 갱신하여 동기화
        contactList = contactPreferences.getContacts().toMutableList()
        contactAdapter.notifyDataSetChanged() // 어댑터에 변경된 데이터를 반영
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editName)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.editPhone)
        val instaEditText = dialogView.findViewById<EditText>(R.id.editInsta)

        AlertDialog.Builder(activity)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val insta = instaEditText.text.toString()
                // 기본 프로필 이미지 설정
                val defaultProfileImage = R.drawable.default_profile

                if (name.isNotEmpty() && phone.isNotEmpty() && insta.isNotEmpty()) {
                    val newContact = Contact(name, phone, insta, profileImage = defaultProfileImage)
                    contactList.add(newContact)
                    contactPreferences.saveContacts(contactList) // 새로운 연락처 저장
                    contactAdapter.notifyItemInserted(contactList.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
