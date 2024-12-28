package com.example.bookapp.tab1

import ContactPreferences
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.bookapp.R

class ContactDetailFragment : Fragment() {

    private lateinit var contactPreferences: ContactPreferences
    private var contact: Contact? = null
    private lateinit var profileImageView: ImageView
    private var selectedProfileImageResId: Int = R.drawable.default_profile // 기본 이미지로 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_detail, container, false)
        contactPreferences = ContactPreferences(requireContext())

        val nameEditText: EditText = view.findViewById(R.id.editName)
        val phoneEditText: EditText = view.findViewById(R.id.editPhone)
        val instaEditText: EditText = view.findViewById(R.id.editInstagram)
        val saveButton: Button = view.findViewById(R.id.buttonSave)
        val choosePhotoButton: Button = view.findViewById(R.id.buttonChoosePhoto)
        val callButton: Button = view.findViewById(R.id.buttonCall) // 추가된 버튼
        val instagramButton: Button = view.findViewById(R.id.buttonInstagram) // 추가된 버튼
        val deleteButton: Button = view.findViewById(R.id.buttonDelete) // 추가된 삭제 버튼
        profileImageView = view.findViewById(R.id.profileImage)

        // 전달받은 데이터를 초기화
        arguments?.let {
            val name = it.getString("CONTACT_NAME", "")
            val phone = it.getString("CONTACT_PHONE", "")
            val insta = it.getString("CONTACT_INSTAGRAM", "")
            val profileImage = it.getInt("CONTACT_IMAGE_RES_ID", R.drawable.default_profile)
            contact = Contact(name, phone, insta, profileImage)

            nameEditText.setText(contact?.name)
            phoneEditText.setText(contact?.phone)
            instaEditText.setText(contact?.insta)
            profileImageView.setImageResource(contact?.profileImage ?: R.drawable.default_profile)
            selectedProfileImageResId = contact?.profileImage ?: R.drawable.default_profile // 초기 프로필 이미지 설정
        }

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

        // 저장 버튼 클릭 시 데이터 저장
        saveButton.setOnClickListener {
            // 유효성 검사 후 저장
            if (validateAllFields(nameEditText, phoneEditText, instaEditText)) {
                val updatedContact = Contact(
                    name = nameEditText.text.toString(),
                    phone = phoneEditText.text.toString(),
                    insta = instaEditText.text.toString(),
                    profileImage = selectedProfileImageResId // 선택된 이미지 리소스 ID 사용
                )
                saveUpdatedContact(updatedContact)
            }
        }

        // 사진 선택 버튼 클릭 시 팝업 표시
        choosePhotoButton.setOnClickListener {
            showImageSelectionDialog()
        }

        // 전화 버튼 클릭 시 전화 앱 열기
        callButton.setOnClickListener {
            val phone = phoneEditText.text.toString()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            }
        }

        // 인스타그램 버튼 클릭 시 브라우저 열기
        instagramButton.setOnClickListener {
            val instaId = instaEditText.text.toString()
            if (instaId.isNotEmpty()) {
                val url = "https://www.instagram.com/$instaId"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        // 삭제 버튼 클릭 시 연락처 삭제
        deleteButton.setOnClickListener {
            // 삭제 확인 팝업을 띄운다
            showDeleteConfirmationDialog() }

        return view
    }

    // 삭제 확인 팝업을 표시하는 함수
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure to delete this contact?")
            .setCancelable(false) // 배경 클릭으로 닫히지 않게 설정
            .setPositiveButton("confirm") { dialog, id ->
                // 사용자가 확인을 클릭한 경우 연락처 삭제
                deleteContact()
            }
            .setNegativeButton("cancel") { dialog, id ->
                // 사용자가 취소를 클릭한 경우 아무 일도 하지 않음
                dialog.dismiss()
            }

        // 다이얼로그를 화면에 표시
        val alert = builder.create()
        alert.show()
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

    // Instagram ID 유효성 검사 함수
    private fun validateInstagramId(insta: String): String? {
        if (insta.length > 30) return "Instagram ID must be at most 30 characters long"
        if (insta.contains(" ")) return "Instagram ID cannot contain spaces"
        if (!insta.matches("^[a-z0-9_\\.]+$".toRegex())) return "Instagram ID can only contain letters, numbers, underscores, and periods"
        if (insta.startsWith(".") || insta.endsWith(".") || insta.contains("..")) return "Instagram ID cannot start or end with a period, and cannot contain consecutive periods"
        return null
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
        val instaError = validateInstagramId(insta)
        if (instaError != null) {
            instaEditText.error = instaError
            isValid = false
        }

        return isValid
    }

    private fun saveUpdatedContact(updatedContact: Contact) {
        val contactList = contactPreferences.getContacts().toMutableList()

        // 기존 데이터를 찾아 수정하거나 새로 추가
        val index = contactList.indexOfFirst { it.name == contact?.name && it.phone == contact?.phone }
        if (index != -1) {
            // 기존 연락처를 수정
            contactList[index] = updatedContact
        } else {
            // 새 연락처 추가
            contactList.add(updatedContact)
        }

        contactPreferences.saveContacts(contactList) // 수정된 데이터 저장

        // UI 갱신을 위해 데이터 전달
        parentFragmentManager.popBackStack() // 뒤로 가기
    }

    // 삭제된 연락처를 SharedPreferences에서 삭제하는 함수
    private fun deleteContact() {
        val contactList = contactPreferences.getContacts().toMutableList()

        // 연락처 목록에서 해당 연락처 삭제
        val index = contactList.indexOfFirst { it.name == contact?.name && it.phone == contact?.phone }
        if (index != -1) {
            contactList.removeAt(index)
            contactPreferences.saveContacts(contactList) // 변경된 리스트 저장
        }

        // 연락처 삭제 후 이전 화면으로 돌아가기
        parentFragmentManager.popBackStack()
    }

    // 이미지 선택 팝업을 표시하는 함수
    private fun showImageSelectionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.pick_mimoji_popup, null)

        val defaultImageView: ImageView = dialogView.findViewById(R.id.imageDefault)
        val image1View: ImageView = dialogView.findViewById(R.id.image1)
        val image2View: ImageView = dialogView.findViewById(R.id.image2)
        val image3View: ImageView = dialogView.findViewById(R.id.image3)
        val image4View: ImageView = dialogView.findViewById(R.id.image4)
        val image5View: ImageView = dialogView.findViewById(R.id.image5)
        val image6View: ImageView = dialogView.findViewById(R.id.image6)

        val imageClickListener = View.OnClickListener { view ->
            selectedProfileImageResId = when (view.id) {
                R.id.image1 -> R.drawable.image1
                R.id.image2 -> R.drawable.image2
                R.id.image3 -> R.drawable.image3
                R.id.image4 -> R.drawable.image4
                R.id.image5 -> R.drawable.image5
                R.id.image6 -> R.drawable.image6
                else -> R.drawable.default_profile
            }
            profileImageView.setImageResource(selectedProfileImageResId)
            builder.create().dismiss() // 다이얼로그 닫기
        }

        defaultImageView.setOnClickListener(imageClickListener)
        image1View.setOnClickListener(imageClickListener)
        image2View.setOnClickListener(imageClickListener)
        image3View.setOnClickListener(imageClickListener)
        image4View.setOnClickListener(imageClickListener)
        image5View.setOnClickListener(imageClickListener)
        image6View.setOnClickListener(imageClickListener)

        builder.setView(dialogView)
        builder.show()
    }
}
