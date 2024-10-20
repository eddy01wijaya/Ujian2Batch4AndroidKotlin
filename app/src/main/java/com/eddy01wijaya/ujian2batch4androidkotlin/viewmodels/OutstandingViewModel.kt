package com.eddy01wijaya.ujian2batch4androidkotlin.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eddy01wijaya.ujian2batch4androidkotlin.R
import com.eddy01wijaya.ujian2batch4androidkotlin.adapters.OutstandingAdapter
import com.eddy01wijaya.ujian2batch4androidkotlin.models.OutstandingData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant

class OutstandingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _outstandingList = MutableLiveData<List<OutstandingData>>()
    val outstandingList: LiveData<List<OutstandingData>> get() = _outstandingList

    private var currentPage: Long = 0 // Ubah menjadi Long
    private var currentIndex: DocumentSnapshot? = null
    private var previousIndexes = mutableListOf<DocumentSnapshot>() // Untuk menyimpan indeks sebelumnya
    val itemsPerPage: Long  = 5 // Ubah sesuai kebutuhan

    init {
        fetchOutstandingData()
    }

    fun fetchOutstandingData() {

        var query = db.collection("outstanding")
            .orderBy("nama")
            .limit(itemsPerPage)

        currentIndex?.let { query = query.startAfter(it) }

        query.get().addOnSuccessListener { documents ->
            val itemList = documents.map {
                it.toObject(OutstandingData::class.java).apply { id = it.id }
            }
            _outstandingList.value = itemList
            currentIndex?.let { previousIndexes.add(it) } // Simpan indeks sebelumnya
            currentIndex = documents.documents.lastOrNull()
            if (currentIndex != null) currentPage++ // Increment currentPage

        }
    }


    fun nextPage() = fetchOutstandingData()

    fun previousPage() {
        if (previousIndexes.isNotEmpty()) {
            // Ambil indeks sebelum yang terakhir
            previousIndexes.removeAt(previousIndexes.lastIndex) // Hapus indeks saat ini dari daftar
            currentIndex = if (previousIndexes.isNotEmpty()) previousIndexes.last() else null

            currentPage = maxOf(0, currentPage - 2) // Decrement currentPage
            fetchOutstandingData() // Ambil data untuk halaman sebelumnya
        } else {
            // Reset ke halaman pertama jika tidak ada halaman sebelumnya
            resetPagination()
        }
    }


    fun resetPagination() {
        currentIndex = null
        previousIndexes.clear() // Kosongkan daftar indeks sebelumnya
        currentPage = 0
        fetchOutstandingData()
    }

    fun getCurrentPageIndex() = currentPage

//    fun fetchOutstandingData() {
//        db.collection("outstanding")
//            .addSnapshotListener { snapshot, exception ->
//                if (exception != null) {
//                    Log.w("Error", "Listen failed.", exception)
//                    return@addSnapshotListener
//                }
//
//                val itemList = mutableListOf<OutstandingData>()
//                snapshot?.documents?.forEach { document ->
//                    val data = document.toObject(OutstandingData::class.java)
//                    if (data != null) {
//                        data.id = document.id // Ini untuk menyimpan ID dokumen
//                        itemList.add(data)
//                    }
//                }
//                _outstandingList.value = itemList
//            }
//    }


    fun addOutstanding(context: Context,dataOutstanding:MutableMap<String,Any>){
        db.collection("outstanding")
            .add(dataOutstanding)
            .addOnSuccessListener { documentReference ->
                //Log.d("Success", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(context, R.string.success_added, Toast.LENGTH_SHORT).show()
                resetPagination()
            }
            .addOnFailureListener { e ->
                //Log.w("Error", "Error adding document", e)
                Toast.makeText(context, "Error adding document "+e, Toast.LENGTH_LONG).show()
            }
    }

    fun updateOutstanding(docId: String,updatedData:MutableMap<String, Any>){
        // Update the document in Firestore
        db.collection("outstanding")  // Replace with your collection name
            .document(docId)
            .update(updatedData)
            .addOnSuccessListener {
                Log.d("UpdateActivity", "Document successfully updated!")
                // Optionally navigate back or show a success message
                resetPagination()

            }
            .addOnFailureListener { e ->
                Log.w("UpdateActivity", "Error updating document", e)
            }
    }

    fun deleteDialog(context : Context, docId : String){

        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        builder.setTitle("Remove the data!")
            .setMessage("Do you want to remove it?")
            .setPositiveButton("Yes") { dialog, id ->
                db.collection("outstanding").document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Data removed successfully.", Toast.LENGTH_SHORT).show()
                        resetPagination()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error removing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel") { dialog, id ->
            }
        builder.create().show()
    }


    fun searchOutstandingItem(query: String) {
        resetPagination()
        db.collection("outstanding")
            .orderBy("nama")
            .startAt(query)
            .endAt(query + '\uF8FF')  // This ensures you get all items starting with the query
            .get()
            .addOnSuccessListener { documents ->
                val searchResults = documents.map { doc ->
                    doc.toObject(OutstandingData::class.java).apply { id = doc.id }
                }
                currentPage = 0
                _outstandingList.value = searchResults
            }
            .addOnFailureListener { exception ->
                Log.e("Search Error", "Error fetching search results: ${exception.message}")
            }
    }


}
