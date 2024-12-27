package com.example.bookapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.R
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class ContactDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        // View 초기화
        val profileImageView: ImageView = findViewById(R.id.profileImageView)
        val nameTextView: TextView = findViewById(R.id.editTextName)
        val phoneTextView: TextView = findViewById(R.id.editTextPhone)
        val instaTextView: TextView = findViewById(R.id.editTextInsta)
        val choosePhotoButton: Button = findViewById(R.id.buttonChoosePhoto)


        // Intent로부터 데이터 받기
        val name = intent.getStringExtra("CONTACT_NAME") ?: ""
        val phone = intent.getStringExtra("CONTACT_PHONE") ?: ""
        val insta = intent.getStringExtra("CONTACT_INSTAGRAM") ?: ""

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
    }
    // 이미지 선택 팝업을 표시하는 함수
    private fun showImageSelectionDialog(profileImageView: ImageView) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
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
