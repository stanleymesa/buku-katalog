package com.example.handyboocue.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BukuModel(
    val id: String = "",
    val judul: String = "",
    val penulis: String = "",
    val genre: String = "",
    val deskripsi: String = "",
    val gambar: String = "",
    val slug: ArrayList<String> = arrayListOf()
): Parcelable
