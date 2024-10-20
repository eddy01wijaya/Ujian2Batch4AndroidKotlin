package com.eddy01wijaya.ujian2batch4androidkotlin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eddy01wijaya.ujian2batch4androidkotlin.viewmodels.OutstandingViewModel
import com.google.firebase.firestore.FirebaseFirestore

class UpdateActivity : AppCompatActivity() {
    lateinit var headerUpdate: TextView
    lateinit var txtNamaUpdate: EditText
    lateinit var txtAlamatUpdate: EditText
    lateinit var txtOutstandingUpdate: EditText
    lateinit var btnAction: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var docId: String
    private lateinit var viewModel: OutstandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.action_form)
        initComponent()
    }

    fun initComponent() {
        headerUpdate = findViewById(R.id.txtHeaderAction)
        txtNamaUpdate = findViewById(R.id.txtNamaAction)
        txtAlamatUpdate = findViewById(R.id.txtAlamatAction)
        txtOutstandingUpdate = findViewById(R.id.txtOutstandingAction)
        btnAction = findViewById(R.id.btnSubmitAction)
        db = FirebaseFirestore.getInstance()

        headerUpdate.setText("Update Outstanding Data")
        btnAction.setText("Update")

        // Get the document ID from the Intent
        docId = intent.getStringExtra("DOC_ID") ?: return

        // Fetch data from Firestore
        fetchDataFromFirestore(docId)

        btnAction.setOnClickListener{
            updateDataInFirestore(docId)
        }
    }

    private fun fetchDataFromFirestore(docId: String) {
        db.collection("outstanding")  // Replace with your collection name
            .document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Assuming the fields are named "nama", "alamat", and "outstanding"
                    val nama = document.getString("nama") ?: ""
                    val alamat = document.getString("alamat") ?: ""
                    val outstanding = document.getDouble("outstanding")?.toString() ?: ""

                    // Set the fetched data to the respective EditTexts
                    txtNamaUpdate.setText(nama)
                    txtAlamatUpdate.setText(alamat)
                    txtOutstandingUpdate.setText(outstanding)
                } else {
                    Log.d("UpdateActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("UpdateActivity", "Get failed with ", exception)
            }
    }

    private fun updateDataInFirestore(docId: String) {
        // Get updated values from EditTexts
        val updatedNama = txtNamaUpdate.text.toString()
        val updatedAlamat = txtAlamatUpdate.text.toString()
        val updatedOutstanding = txtOutstandingUpdate.text.toString().toDoubleOrNull() ?: 0.0  // Convert to Double, default to 0.0 if invalid
        if (updatedNama.isNotEmpty() && updatedAlamat.isNotEmpty() && updatedOutstanding>0.0) {
            // Create a map of the updated data
            val updatedData: MutableMap<String, Any> = hashMapOf(
                "nama" to updatedNama,
                "alamat" to updatedAlamat,
                "outstanding" to updatedOutstanding
            )
            viewModel = ViewModelProvider(this).get(OutstandingViewModel::class.java)
            viewModel.updateOutstanding(docId,updatedData)
            setResult(Activity.RESULT_OK)
            finish() // Close the activity or navigate to another screen
        }else {
            Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show()
        }

    }
}