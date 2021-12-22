package com.example.handyboocue.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.handyboocue.R
import com.example.handyboocue.session.SessionManager
import com.example.handyboocue.statics.SessionName
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var ivBack: ImageView
    private lateinit var ivBuku: ImageView
    private lateinit var tvJudul: TextView
    private lateinit var tvPenulis: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvDesc: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager
    private lateinit var storageRoot: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Hooks
        ivBack = findViewById(R.id.iv_back_detail)
        ivBuku = findViewById(R.id.iv_buku_detail)
        tvJudul = findViewById(R.id.tv_judul_detail)
        tvPenulis = findViewById(R.id.tv_penulis_detail)
        tvGenre = findViewById(R.id.tv_genre_detail)
        tvDesc = findViewById(R.id.tv_desc_detail)
        progressBar = findViewById(R.id.progress_bar_detail)

        // Set Firebase
        storageRoot = FirebaseStorage.getInstance().getReference("buku")

        // Set Session
        sessionManager = SessionManager(this, SessionName.DETAIL_SESSION)

        setTampilan()

        // On Click
        ivBack.setOnClickListener(this)

    }

    private fun setTampilan() {
        val bukuModel = sessionManager.getEditSession()

        // Jika ada gambar
        if (bukuModel.gambar.isNotEmpty()) {
            progressBar.isVisible = true
            storageRoot.child(bukuModel.gambar).downloadUrl
                .addOnSuccessListener {
                    Glide.with(this).load(it).into(ivBuku)
                    progressBar.isVisible = false
                }
        }
        // Jika tidak ada gambar
        else {
            Glide.with(this).load(R.drawable.book).into(ivBuku)
        }

        tvJudul.text = bukuModel.judul
        tvPenulis.text = bukuModel.penulis
        tvGenre.text = bukuModel.genre
        tvDesc.text = bukuModel.deskripsi

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_back_detail -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}