import android.content.Context
import com.example.bookapp.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("contacts", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 연락처 리스트를 SharedPreferences에 저장
    fun saveContacts(contactList: List<Contact>) {
        val json = gson.toJson(contactList) // 리스트를 JSON 형식으로 변환
        sharedPreferences.edit().putString("contact_list", json).apply()
    }

    // SharedPreferences에서 연락처 리스트를 조회
    fun getContacts(): List<Contact> {
        val json = sharedPreferences.getString("contact_list", null) // JSON 형식으로 저장된 값 가져오기
        return if (json != null) {
            val type = object : TypeToken<List<Contact>>() {}.type // 제네릭 타입을 가져오기 위한 리플렉션
            gson.fromJson(json, type) // JSON을 Contact 리스트로 변환
        } else {
            emptyList() // 데이터가 없으면 빈 리스트 반환
        }
    }
}
