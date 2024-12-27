package com.example.bookapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment // Fragment 사용으로 변경됨

class ContactDetailFragment : Fragment() { // Activity에서 Fragment로 변경됨

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // LayoutInflater로 Fragment 레이아웃 생성
        val view = inflater.inflate(R.layout.fragment_contact_detail, container, false)

        // View 초기화 (findViewById를 view.findViewById로 변경)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val nameTextView: TextView = view.findViewById(R.id.editTextName)
        val phoneTextView: TextView = view.findViewById(R.id.editTextPhone)
        val instaTextView: TextView = view.findViewById(R.id.editTextInsta)
        val choosePhotoButton: Button = view.findViewById(R.id.buttonChoosePhoto)

        // Intent 대신 Fragment의 arguments에서 데이터 받기
        val name = arguments?.getString("CONTACT_NAME") ?: "" // 변경됨
        val phone = arguments?.getString("CONTACT_PHONE") ?: "" // 변경됨
        val insta = arguments?.getString("CONTACT_INSTAGRAM") ?: "" // 변경됨

        // 데이터 표시
        nameTextView.text = name
        phoneTextView.text = phone
        instaTextView.text = insta

        // 프로필 사진 기본 설정 (임시 이미지 사용)
        profileImageView.setImageResource(R.drawable.default_profile)

        // Choose Photo 버튼 클릭 시
        choosePhotoButton.setOnClickListener {
            showImageSelectionDialog(profileImageView)
        }

        return view // Fragment에서는 view를 반환
    }

    // 이미지 선택 팝업을 표시하는 함수
    private fun showImageSelectionDialog(profileImageView: ImageView) {
        val builder = AlertDialog.Builder(requireContext()) // requireContext() 사용
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_image_selection, null)

        // 팝업 레이아웃에 이미지 설정
        val defaultImageView: ImageView = dialogView.findViewById(R.id.imageDefault)
        val image2View: ImageView = dialogView.findViewById(R.id.image2)
        val image3View: ImageView = dialogView.findViewById(R.id.image3)

        // 클릭 리스너 설정
        defaultImageView.setOnClickListener {
            profileImageView.setImageResource(R.drawable.default_profile)
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

        builder.setView(dialogView)
        builder.show()
    }
}
