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
    private var filteredList = mutableListOf<Contact>() // 필터링된 목록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        contactPreferences = ContactPreferences(requireContext()) // ContactPreferences 초기화

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val addContactButton: Button = view.findViewById(R.id.addContactButton)
        val searchBar: EditText = view.findViewById(R.id.search_bar)

        // 어댑터 초기화
        contactList = contactPreferences.getContacts().toMutableList() // SharedPreferences에서 데이터 불러오기
        filteredList = contactList.toMutableList()

        contactAdapter = ContactAdapter(filteredList) { contact ->
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

        // 검색바에 TextWatcher 추가
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        // 필요시 목록 갱신
        filteredList.clear()
        filteredList.addAll(contactList)
        contactAdapter.notifyDataSetChanged()
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.add_contact_popup, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editName)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.editPhone)
        val instaEditText = dialogView.findViewById<EditText>(R.id.editInsta)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Contact")
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
                    filterContacts("") // 전체 목록 갱신
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
            val errorMessage = validateInstagramId(text)
            instaEditText.error = errorMessage
        })
    }

    private fun createTextWatcher(onTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

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
        val instaError = validateInstagramId(insta)
        if (instaError != null) {
            instaEditText.error = instaError
            isValid = false
        }

        return isValid
    }

    private fun validateInstagramId(insta: String): String? {
        if (insta.length > 30) return "Instagram ID must be at most 30 characters long"
        if (insta.contains(" ")) return "Instagram ID cannot contain spaces"
        if (!insta.matches("^[a-z0-9_\\.]+$".toRegex())) return "Instagram ID can only contain letters, numbers, underscores, and periods"
        if (insta.startsWith(".") || insta.endsWith(".") || insta.contains("..")) return "Instagram ID cannot start or end with a period, and cannot contain consecutive periods"
        return null
    }

    private fun showDeleteContactDialog(contact: Contact) {
        AlertDialog.Builder(activity)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete this contact?")
            .setPositiveButton("Delete") { _, _ ->
                val position = contactList.indexOf(contact)
                if (position != -1) {
                    contactList.removeAt(position)
                    contactPreferences.saveContacts(contactList)
                    filterContacts("") // 전체 목록 갱신
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun filterContacts(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredList.clear()

        if (lowerCaseQuery.isEmpty()) {
            filteredList.addAll(contactList)
        } else {
            filteredList.addAll(contactList.filter { contact ->
                contact.name.lowercase().contains(lowerCaseQuery) ||
                        contact.phone.contains(lowerCaseQuery) ||
                        contact.insta.lowercase().contains(lowerCaseQuery)
            })
        }

        contactAdapter.notifyDataSetChanged()
    }
}
