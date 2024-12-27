package com.example.bookapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default Fragment
        loadFragment(ContactListFragment())

        // Bottom Navigation Listener
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_contacts -> {
                    loadFragment(ContactListFragment())
                    true
                }
                R.id.nav_tab2 -> {
                    loadFragment(EmptyFragment("Tab 2 Content"))
                    true
                }
                R.id.nav_tab3 -> {
                    loadFragment(EmptyFragment("Tab 3 Content"))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
