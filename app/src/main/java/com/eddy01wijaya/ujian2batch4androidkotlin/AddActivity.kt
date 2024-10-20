package com.eddy01wijaya.ujian2batch4androidkotlin

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eddy01wijaya.ujian2batch4androidkotlin.viewmodels.OutstandingViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant

class AddActivity : AppCompatActivity() {
    lateinit var headerAdd:TextView
    lateinit var txtNamaAdd:EditText
    lateinit var txtAlamatAdd:EditText
    lateinit var txtOutstandingAdd:EditText
    lateinit var btnAction:Button
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: OutstandingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.action_form)
        initComponent()
    }

    fun initComponent(){
        headerAdd = findViewById(R.id.txtHeaderAction)
        txtNamaAdd = findViewById(R.id.txtNamaAction)
        txtAlamatAdd = findViewById(R.id.txtAlamatAction)
        txtOutstandingAdd = findViewById(R.id.txtOutstandingAction)
        btnAction = findViewById(R.id.btnSubmitAction)
        db = FirebaseFirestore.getInstance()

        headerAdd.setText("Add Outstanding Data")
        btnAction.setText("Add")

        btnAction.setOnClickListener {
            val nama = txtNamaAdd.text.toString().trim()
            val alamat = txtAlamatAdd.text.toString().trim()
            val outstanding = txtOutstandingAdd.text.toString().trim()

            if (nama.isNotEmpty() && alamat.isNotEmpty() && outstanding.isNotEmpty()) {
                // Create a new user with a first and last name
                val outstanding = outstanding.toDoubleOrNull()?: 0.0
                val dataOutstanding:MutableMap<String,Any> = hashMapOf(
                    "id" to Instant.now().toString(),
                    "nama" to nama,
                    "alamat" to alamat,
                    "outstanding" to outstanding
                )

                viewModel = ViewModelProvider(this).get(OutstandingViewModel::class.java)
                viewModel.addOutstanding(this,dataOutstanding)
                setResult(Activity.RESULT_OK)
                finish()

            } else {
                Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show()
            }
        }
    }
}