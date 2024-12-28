package com.example.bookapp.tab1

import ContactPreferences
import android.os.Bundle
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

        // 저장 버튼 클릭 시 데이터 저장
        saveButton.setOnClickListener {
            val updatedContact = Contact(
                name = nameEditText.text.toString(),
                phone = phoneEditText.text.toString(),
                insta = instaEditText.text.toString(),
                profileImage = selectedProfileImageResId // 선택된 이미지 리소스 ID 사용
            )
            saveUpdatedContact(updatedContact)
        }

        // 사진 선택 버튼 클릭 시 팝업 표시
        choosePhotoButton.setOnClickListener {
            showImageSelectionDialog()
        }

        return view
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

    // 이미지 선택 팝업을 표시하는 함수
    private fun showImageSelectionDialog() {
        val builder = AlertDialog.Builder(requireContext())
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

            // 선택된 이미지를 ImageView에 설정
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
