import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.bookapp.R
//import com.example.bookapp.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineStart
import java.io.ByteArrayOutputStream
import android.util.Base64 // android.util.Base64 사용
import kotlin.io.encoding.ExperimentalEncodingApi

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

    // 비트맵을 Base64로 인코딩하여 저장하고 리소스 ID 반환
    // 비트맵을 Base64로 인코딩하여 저장하고 리소스 ID 반환
    fun saveBitmapAsResource(bitmap: Bitmap): Int {
        // Bitmap을 Base64로 인코딩하여 SharedPreferences에 저장
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT) // android.util.Base64 사용

        // SharedPreferences에 Base64로 인코딩된 이미지를 저장
        sharedPreferences.edit().putString("saved_image", encodedImage).apply()

        // 실제 리소스 ID를 반환 (예시로 다른 이미지 리소스를 반환)
        return R.drawable.default_profile // 기본 프로필 이미지 리소스 ID 반환 (saved_image 리소스를 설정)
    }

    /// 저장된 이미지를 Base64에서 디코딩하여 비트맵으로 반환
    fun getSavedImage(): Bitmap? {
        val encodedImage = sharedPreferences.getString("saved_image", null)
        return if (encodedImage != null) {
            val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } else {
            null
        }
    }
}
