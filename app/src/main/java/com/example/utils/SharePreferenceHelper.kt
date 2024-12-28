package com.example.utils

import android.content.Context
import com.example.bookapp.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val prefs = context.getSharedPreferences("ContactsApp", Context.MODE_PRIVATE)

    fun saveContacts(contactList: List<Contact>) {
        val json = Gson().toJson(contactList)
        prefs.edit().putString("contacts", json).apply()
    }

    fun getContacts(): List<Contact> {
        val json = prefs.getString("contacts", null)
        return if (json != null) {
            val type = object : TypeToken<List<Contact>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
