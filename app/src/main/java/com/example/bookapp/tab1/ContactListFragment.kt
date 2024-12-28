package com.example.bookapp.tab1

import ContactPreferences
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R

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

        // 롱 클릭 리스너 설정 (삭제 팝업)
        contactAdapter.setOnItemLongClickListener { contact ->
            showDeleteContactDialog(contact)
        }

        addContactButton.setOnClickListener {
            showAddContactDialog()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // 필요시 목록 갱신
        // onResume에서 다시 데이터를 불러오지 않고, 직접 어댑터에서 업데이트가 반영되도록 처리
        contactAdapter.notifyDataSetChanged()
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_contact_popup, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editName)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.editPhone)
        val instaEditText = dialogView.findViewById<EditText>(R.id.editInsta)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add com.example.bookapp.tab1.Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val insta = instaEditText.text.toString()

                // 유효성 검사 후 연락처 추가
                if (validateAllFields(nameEditText, phoneEditText, instaEditText)) {
                    val defaultProfileImage = R.drawable.default_profile
                    val newContact = Contact(name, phone, insta, defaultProfileImage)
                    contactList.add(newContact)
                    contactPreferences.saveContacts(contactList)
                    contactAdapter.notifyItemInserted(contactList.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // 실시간 검증을 위한 TextWatcher 추가
        nameEditText.addTextChangedListener(createTextWatcher { text ->
            if (text.length > 15 || !text.matches("^[a-zA-Z]*$".toRegex())) {
                nameEditText.error = "Name must be in English and 15 characters or less"
            } else {
                nameEditText.error = null
            }
        })

        phoneEditText.addTextChangedListener(createTextWatcher { text ->
            if (!text.matches("^010-\\d{4}-\\d{4}$".toRegex())) {
                phoneEditText.error = "Phone number must be in the format 010-xxxx-xxxx"
            } else {
                phoneEditText.error = null
            }
        })

        instaEditText.addTextChangedListener(createTextWatcher { text ->
            if (!text.startsWith("@")) {
                instaEditText.error = "Instagram must start with @"
            } else {
                instaEditText.error = null
            }
        })
    }

    // TextWatcher를 생성하는 함수
    private fun createTextWatcher(onTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

    // 모든 필드에 대한 최종 검증 함수
    private fun validateAllFields(
        nameEditText: EditText,
        phoneEditText: EditText,
        instaEditText: EditText
    ): Boolean {
        var isValid = true

        val name = nameEditText.text.toString()
        if (name.length > 15 || !name.matches("^[a-zA-Z]*$".toRegex())) {
            nameEditText.error = "Name must be in English and 15 characters or less"
            isValid = false
        }

        val phone = phoneEditText.text.toString()
        if (!phone.matches("^010-\\d{4}-\\d{4}$".toRegex())) {
            phoneEditText.error = "Phone number must be in the format 010-xxxx-xxxx"
            isValid = false
        }

        val insta = instaEditText.text.toString()
        if (!insta.startsWith("@")) {
            instaEditText.error = "Instagram must start with @"
            isValid = false
        }

        return isValid
    }



    private fun showDeleteContactDialog(contact: Contact) {
        AlertDialog.Builder(activity)
            .setTitle("Delete com.example.bookapp.tab1.Contact")
            .setMessage("Are you sure you want to delete this contact?")
            .setPositiveButton("Delete") { _, _ ->
                val position = contactList.indexOf(contact)
                if (position != -1) {
                    // 리스트에서 연락처 삭제
                    contactList.removeAt(position)

                    // 변경된 목록을 SharedPreferences에 저장
                    contactPreferences.saveContacts(contactList)

                    // 어댑터에 변경 사항 반영 (삭제된 항목만 갱신)
                    contactAdapter.notifyItemRemoved(position)

                    // 삭제된 항목 이후의 항목들 갱신
                    contactAdapter.notifyItemRangeChanged(position, contactList.size - position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}