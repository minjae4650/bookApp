package com.example.bookapp.tab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bookapp.R

class EmptyFragment(private val content: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty, container, false)
        val textView: TextView = view.findViewById(R.id.textContent)
        textView.text = content
        return view
    }
}
