import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.Contact
import com.example.bookapp.ContactAdapter
import com.example.bookapp.R
import com.example.bookapp.SampleData

class MainActivity : AppCompatActivity() {
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val addContactButton: Button = findViewById(R.id.addContactButton)

        contactAdapter = ContactAdapter(SampleData.contactList.toMutableList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        addContactButton.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.add_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editName)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.editPhone)
        val instaEditText = dialogView.findViewById<EditText>(R.id.editInsta)

        AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val insta = instaEditText.text.toString()
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    SampleData.contactList.add(Contact(name, insta, phone))
                    contactAdapter.notifyItemInserted(SampleData.contactList.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
