package com.example.bookapp

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
    private val contactList = SampleData.contactList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val addContactButton: Button = view.findViewById(R.id.addContactButton)

        // 어댑터 초기화 및 클릭 이벤트 처리
        contactAdapter = ContactAdapter(contactList) { contact ->
            // 수정된 부분: FragmentTransaction을 사용하여 ContactDetailFragment 표시
            val contactDetailFragment = ContactDetailFragment().apply {
                val bundle = Bundle().apply {
                    putString("CONTACT_NAME", contact.name)
                    putString("CONTACT_PHONE", contact.phone)
                    putString("CONTACT_INSTAGRAM", contact.insta)
                }
                arguments = bundle // 전달할 데이터를 Fragment의 arguments에 넣음
            }

            // FragmentTransaction을 사용하여 Fragment 교체
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, contactDetailFragment) // fragmentContainer는 레이아웃 ID
                .addToBackStack(null) // 뒤로가기 지원
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = contactAdapter

        addContactButton.setOnClickListener {
            showAddContactDialog()
        }

        return view
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
                if (name.isNotEmpty() && phone.isNotEmpty() && insta.isNotEmpty()) {
                    val newContact = Contact(name, insta, phone)
                    contactList.add(newContact)
                    contactAdapter.notifyItemInserted(contactList.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
