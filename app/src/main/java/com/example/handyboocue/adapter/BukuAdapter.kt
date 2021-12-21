package com.example.handyboocue.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.handyboocue.R
import com.example.handyboocue.model.BukuModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BukuAdapter(options: FirestoreRecyclerOptions<BukuModel>): FirestoreRecyclerAdapter<BukuModel, BukuAdapter.BukuViewHolder>(options) {
    private val storageRoot: StorageReference = FirebaseStorage.getInstance().getReference("buku")

    class BukuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBuku: ImageView = itemView.findViewById(R.id.iv_buku_row)
        val tvJudul: TextView = itemView.findViewById(R.id.tv_judul_row)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc_row)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_buku, parent, false)
        return BukuViewHolder(view)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int, model: BukuModel) {
        if (!model.gambar.isEmpty()) {
            storageRoot.child(model.gambar).downloadUrl
                .addOnSuccessListener {
                    if (it != null) {
                        Glide.with(holder.itemView.context).load(it).into(holder.ivBuku)
                    } else {
                        Glide.with(holder.itemView.context).load(R.drawable.book).into(holder.ivBuku)
                    }
                }
        } else {
            Glide.with(holder.itemView.context).load(R.drawable.book).into(holder.ivBuku)
        }
        holder.tvJudul.text = model.judul
        holder.tvDesc.text = model.deskripsi
    }

}