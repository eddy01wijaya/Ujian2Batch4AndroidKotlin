package com.eddy01wijaya.ujian2batch4androidkotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.eddy01wijaya.ujian2batch4androidkotlin.R
import com.eddy01wijaya.ujian2batch4androidkotlin.models.OutstandingData
import com.eddy01wijaya.ujian2batch4androidkotlin.viewmodels.OutstandingViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore





class OutstandingAdapter(
    private val context: Context,
    private var items: List<OutstandingData>,
    private val onUpdateSelectedItem: (String) -> Unit,
    private val onDeleteSelectedItem: (String) -> Unit
) : RecyclerView.Adapter<OutstandingAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNama: TextView = view.findViewById(R.id.txtNamaItem)
        val txtAlamat: TextView = view.findViewById(R.id.txtAlamatItem)
        val txtOutstanding: TextView = view.findViewById(R.id.txtOutstandingItem)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteItem)
        val cardUpdate : CardView= view.findViewById(R.id.cardItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_outstanding, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.txtNama.text = item.nama
        holder.txtAlamat.text = item.alamat
        holder.txtOutstanding.text = String.format("%s%.2f", context.getString(R.string.currency), item.outstanding)

        holder.cardUpdate.setOnClickListener {
            onUpdateSelectedItem(item.id)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteSelectedItem(item.id)
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<OutstandingData>) {
        items = newList
        notifyDataSetChanged()
    }
}
