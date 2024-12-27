package com.example.bookapp

import android.app.AlertDialog
import android.content.Intent
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
            val intent = Intent(activity, ContactDetailActivity::class.java).apply {
                putExtra("CONTACT_NAME", contact.name)
                putExtra("CONTACT_PHONE", contact.phone)
                putExtra("CONTACT_INSTAGRAM", contact.insta)
            }
            startActivity(intent)
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
