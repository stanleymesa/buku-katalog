package com.example.handyboocue.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.handyboocue.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class InputActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tiJudul: TextInputLayout
    private lateinit var tiPenulis: TextInputLayout
    private lateinit var tiGenre: TextInputLayout
    private lateinit var tiDesc: TextInputLayout
    private lateinit var btnSave: MaterialButton
    private lateinit var btnPilihGambar: MaterialButton
    private lateinit var judul: String
    private lateinit var penulis: String
    private lateinit var genre: String
    private lateinit var desc: String
    private lateinit var firestoreRoot: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Hooks
        tiJudul = findViewById(R.id.ti_judul_input)
        tiPenulis = findViewById(R.id.ti_penulis_input)
        tiGenre = findViewById(R.id.ti_genre_input)
        tiDesc = findViewById(R.id.ti_deskripsi_input)
        btnSave = findViewById(R.id.btn_save)
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar_input)

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance()

        // On Click
        btnSave.setOnClickListener(this)

    }

    private fun validateJudul() {
        if (tiJudul.editText!!.text.isEmpty()) {
            tiJudul.error = "Judul tidak boleh kosong"
        } else {
            tiJudul.error = null
        }
    }

    private fun validatePenulis() {
        if (tiPenulis.editText!!.text.isEmpty()) {
            tiPenulis.error = "Penulis tidak boleh kosong"
        } else {
            tiPenulis.error = null
        }
    }

    private fun validateGenre() {
        if (tiGenre.editText!!.text.isEmpty()) {
            tiGenre.error = "Genre tidak boleh kosong"
        } else {
            tiGenre.error = null
        }
    }

    private fun validateDesc() {
        if (tiDesc.editText!!.text.isEmpty()) {
            tiDesc.error = "Deskripsi tidak boleh kosong"
        } else {
            tiDesc.error = null
        }
    }

    private fun allValidation(): Boolean {
        validateJudul()
        validatePenulis()
        validateGenre()
        validateDesc()

        if (tiJudul.error != null || tiPenulis.error != null || tiGenre.error != null || tiDesc.error != null) {
            return false
        }
        return true
    }

    private fun saveData() {
        if (allValidation()) {

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save -> {
                allValidation()
            }
        }
    }
}