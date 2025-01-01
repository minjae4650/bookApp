package com.example.bookapp.data

import android.content.Context
import com.example.bookapp.tab2.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookPreferences(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("books", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveBooks(books: List<Book>) {
        val json = gson.toJson(books)
        sharedPreferences.edit().putString("book_list", json).apply()
    }

    fun getBooks(): MutableList<Book> {
        val json = sharedPreferences.getString("book_list", null) ?: return mutableListOf()
        val type = object : TypeToken<List<Book>>() {}.type
        return gson.fromJson<List<Book>>(json, type).toMutableList()
    }
}
