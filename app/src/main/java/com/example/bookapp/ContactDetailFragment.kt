package com.example.bookapp

import ContactPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment // Fragment 사용으로 변경됨

class ContactDetailFragment : Fragment() {

    private lateinit var contactPreferences: ContactPreferences
    private var contact: Contact? = null
    private lateinit var profileImageView: ImageView

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
        val choosePhotoButton: Button = view.findViewById(R.id.buttonChoosePhoto) // 추가된 버튼
        profileImageView = view.findViewById(R.id.profileImage)

        // 전달받은 데이터를 초기화
        arguments?.let {
            val name = it.getString("CONTACT_NAME", "") // 기본값 설정
            val phone = it.getString("CONTACT_PHONE", "") // 기본값 설정
            val insta = it.getString("CONTACT_INSTAGRAM", "") // 기본값 설정
            val profileImage = it.getInt("CONTACT_IMAGE_RES_ID", R.drawable.default_profile) // 기본 이미지 설정
            contact = Contact(name, phone, insta, profileImage) // 모든 값이 전달되지 않더라도 기본값 사용

            nameEditText.setText(contact?.name)
            phoneEditText.setText(contact?.phone)
            instaEditText.setText(contact?.insta)
        }

        // 저장 버튼 클릭 시 데이터 저장
        saveButton.setOnClickListener {
            val updatedContact = Contact(
                name = nameEditText.text.toString(),
                phone = phoneEditText.text.toString(),
                insta = instaEditText.text.toString(),
                profileImage = contact?.profileImage ?: R.drawable.default_profile // 기존 이미지 유지
            )
            saveUpdatedContact(updatedContact)
        }

        // 사진 선택 버튼 클릭 시 팝업 표시
        choosePhotoButton.setOnClickListener {
            showImageSelectionDialog(profileImageView)
        }

        return view
    }

    private fun saveUpdatedContact(updatedContact: Contact) {
        val contactList = contactPreferences.getContacts().toMutableList()

        // 기존 데이터를 찾아 수정
        val index = contactList.indexOfFirst { it.name == contact?.name && it.phone == contact?.phone }
        if (index != -1) {
            contactList[index] = updatedContact
        }

        contactPreferences.saveContacts(contactList) // 수정된 데이터 저장
        parentFragmentManager.popBackStack() // 뒤로 가기
    }

    // 이미지 선택 팝업을 표시하는 함수
    private fun showImageSelectionDialog(profileImageView: ImageView) {
        val builder = AlertDialog.Builder(requireContext()) // requireContext() 사용
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_image_selection, null)

        // 팝업 레이아웃에 이미지 설정
        val defaultImageView: ImageView = dialogView.findViewById(R.id.imageDefault)
        val image1View: ImageView = dialogView.findViewById(R.id.image1)
        val image2View: ImageView = dialogView.findViewById(R.id.image2)
        val image3View: ImageView = dialogView.findViewById(R.id.image3)
        val image4View: ImageView = dialogView.findViewById(R.id.image4)
        val image5View: ImageView = dialogView.findViewById(R.id.image5)
        val image6View: ImageView = dialogView.findViewById(R.id.image6)

        // 클릭 리스너 설정
        defaultImageView.setOnClickListener {
            profileImageView.setImageResource(R.drawable.default_profile)
            builder.create().dismiss()
        }
        image1View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image1)
            builder.create().dismiss()
        }
        image2View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image2)
            builder.create().dismiss()
        }
        image3View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image3)
            builder.create().dismiss()
        }
        image4View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image4)
            builder.create().dismiss()
        }
        image5View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image5)
            builder.create().dismiss()
        }
        image6View.setOnClickListener {
            profileImageView.setImageResource(R.drawable.image6)
            builder.create().dismiss()
        }

        builder.setView(dialogView)
        builder.show()
    }
}
