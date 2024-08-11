package alejandro.chango.firebase

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var editTextData: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextData = findViewById(R.id.editTextData)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerView = findViewById(R.id.recyclerView)

        db = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonSave.setOnClickListener {
            val data = editTextData.text.toString()
            saveDataToFirestore(data)
        }

        loadDataFromFirestore()
    }

    private fun saveDataToFirestore(data: String) {
        val userData = hashMapOf("data" to data)

        db.collection("users")
            .add(userData)
            .addOnSuccessListener { documentReference ->
                Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                loadDataFromFirestore()
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error adding document", e)
            }
    }

    private fun loadDataFromFirestore() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataList = mutableListOf<String>()
                    for (document in task.result) {
                        document.getString("data")?.let { dataList.add(it) }
                    }
                    recyclerView.adapter = MyRecyclerViewAdapter(dataList)
                } else {
                    Log.w("MainActivity", "Error getting documents.", task.exception)
                }
            }
    }
}
