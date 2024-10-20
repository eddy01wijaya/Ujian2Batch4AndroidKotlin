package com.eddy01wijaya.ujian2batch4androidkotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddy01wijaya.ujian2batch4androidkotlin.adapters.OutstandingAdapter
import com.eddy01wijaya.ujian2batch4androidkotlin.models.OutstandingData
import com.eddy01wijaya.ujian2batch4androidkotlin.viewmodels.OutstandingViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var lstOutstanding: RecyclerView
    lateinit var btnAdd: Button
    lateinit var txtSearch: EditText
    lateinit var btnSearch: TextView
    lateinit var btnRefresh: Button
    lateinit var btnPrev: Button
    lateinit var btnNext: Button
    lateinit var txtCurrentPage : TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: OutstandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initComponent()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun initComponent(){
        lstOutstanding = findViewById(R.id.lstMain)
        btnAdd = findViewById(R.id.btnAddMain)
        txtSearch = findViewById(R.id.txtSearchMain)
        btnSearch = findViewById(R.id.btnSearchMain)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        txtCurrentPage = findViewById(R.id.txtCurrentPage)

        db = FirebaseFirestore.getInstance()

        lstOutstanding.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this).get(OutstandingViewModel::class.java)

        btnNext.setOnClickListener { viewModel.nextPage() }

        btnPrev.setOnClickListener { viewModel.previousPage() }

        viewModel.outstandingList.observe(this) { itemList ->
            lstOutstanding.adapter = OutstandingAdapter(this, itemList,
                onUpdateSelectedItem = { updateForm(it) },
                onDeleteSelectedItem = { viewModel.deleteDialog(this, it) }
            )
            txtCurrentPage.text = "${viewModel.getCurrentPageIndex()+1}" // Tampilkan halaman saat ini
            btnPrev.visibility = if (viewModel.getCurrentPageIndex() == 0L) View.GONE else View.VISIBLE
            btnNext.visibility = if (itemList.size < viewModel.itemsPerPage.toInt()) View.GONE else View.VISIBLE
        }


        btnAdd.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent,22)
        }

        btnSearch.setOnClickListener{
            val query = txtSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchOutstandingItem(query)
            } else {
                viewModel.resetPagination()
            }
        }

        btnRefresh.setOnClickListener{
            viewModel.resetPagination()
        }
    }

    fun updateForm(docId:String){
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("DOC_ID", docId)
        startActivityForResult(intent,22)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
            viewModel = ViewModelProvider(this).get(OutstandingViewModel::class.java)
            viewModel.resetPagination()
        }
    }

}