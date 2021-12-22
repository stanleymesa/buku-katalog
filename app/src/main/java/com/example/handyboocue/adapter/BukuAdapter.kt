package com.example.handyboocue.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.handyboocue.R
import com.example.handyboocue.model.BukuModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BukuAdapter(options: FirestoreRecyclerOptions<BukuModel>, val onItemClickCallback: OnItemClickCallback): FirestoreRecyclerAdapter<BukuModel, BukuAdapter.BukuViewHolder>(options) {
    private val storageRoot: StorageReference = FirebaseStorage.getInstance().getReference("buku")

    class BukuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBuku: ImageView = itemView.findViewById(R.id.iv_buku_row)
        val tvJudul: TextView = itemView.findViewById(R.id.tv_judul_row)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc_row)
        val space: Space = itemView.findViewById(R.id.space)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_buku, parent, false)
        return BukuViewHolder(view)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int, model: BukuModel) {
        setBindViewHolder(holder, model, position)
    }

    private fun setBindViewHolder(holder: BukuViewHolder, model: BukuModel, position: Int) {
        initiation(holder, model)
        retrieveGambar(holder, model)
        setMaxLines(holder, model)
        initInterface(holder, model)
        addSpace(holder, position)
    }

    private fun retrieveGambar(holder: BukuViewHolder, model: BukuModel) {
        if (!model.gambar.isEmpty()) {
            storageRoot.child(model.gambar).downloadUrl
                .addOnSuccessListener {
                    if (it != null) {
                        Glide.with(holder.itemView.context).load(it).into(holder.ivBuku)
                    } else {
                        Glide.with(holder.itemView.context).load(R.drawable.book).into(holder.ivBuku)
                    }
                }
                .addOnFailureListener {
                    Glide.with(holder.itemView.context).load(R.drawable.book).into(holder.ivBuku)
                }
        } else {
            Glide.with(holder.itemView.context).load(R.drawable.book).into(holder.ivBuku)
        }
    }

    private fun setMaxLines(holder: BukuViewHolder, model: BukuModel) {
        holder.tvJudul.post {
            if (holder.tvJudul.lineCount >= 2) {
                holder.tvDesc.maxLines = 2
            } else {
                holder.tvDesc.maxLines = 4
            }
        }
    }

    private fun initiation(holder: BukuViewHolder, model: BukuModel) {
        holder.tvJudul.text = model.judul
        holder.tvDesc.text = model.deskripsi
    }

    private fun initInterface(holder: BukuViewHolder, model: BukuModel) {
        holder.itemView.setOnClickListener {
            onItemClickCallback.onNormalPressed(model)
        }

        holder.itemView.setOnLongClickListener {
            onItemClickCallback.onLongPressed(model)
            false
        }
    }

    private fun addSpace(holder: BukuViewHolder, position: Int) {
        holder.space.isVisible = false
        if (position == itemCount - 1) {
            holder.space.isVisible = true
        }
    }

    interface OnItemClickCallback {
        fun onLongPressed(bukuModel: BukuModel)
        fun onNormalPressed(bukuModel: BukuModel)
    }

}