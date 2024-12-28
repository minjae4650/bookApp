package com.example.bookapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookapp.tab2.BooksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default Fragment
        loadFragment(ContactListFragment(), false) // 기본 프래그먼트를 백스택에 추가하지 않음

        // Bottom Navigation Listener
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_contacts -> {
                    loadFragment(ContactListFragment(), false) // 연락처 목록
                    true
                }
                R.id.nav_tab2 -> {
                    loadFragment(BooksFragment(), false) // 탭 2
                    true
                }
                R.id.nav_tab3 -> {
                    loadFragment(EmptyFragment("Tab 3 Content"), false) // 탭 3
                    true
                }
                else -> false
            }
        }

        // 기본 화면 (연락처 목록)
        if (savedInstanceState == null) {
            loadFragment(ContactListFragment(), false)
        }
    }

    // Fragment를 로드하는 함수
    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        // 백스택에 추가할지 여부 확인
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
        return true
    }

    // 연락처 상세 화면을 표시하는 함수
    fun showContactDetail(name: String, phone: String, insta: String) {
        val contactDetailFragment = ContactDetailFragment().apply {
            arguments = Bundle().apply {
                putString("CONTACT_NAME", name)
                putString("CONTACT_PHONE", phone)
                putString("CONTACT_INSTAGRAM", insta)
            }
        }

        // 하단 탭 유지하면서 상세 화면으로 이동
        loadFragment(contactDetailFragment, true) // 백스택에 추가
    }
}
