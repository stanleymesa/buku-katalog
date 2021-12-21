package com.example.handyboocue.activities

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.handyboocue.R
import com.example.handyboocue.model.BukuModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class InputActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var tiJudul: TextInputLayout
    private lateinit var tiPenulis: TextInputLayout
    private lateinit var tiGenre: TextInputLayout
    private lateinit var tiDesc: TextInputLayout
    private lateinit var ivBuku: ImageView
    private lateinit var ivBack: ImageView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnPilihGambar: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var judul: String
    private lateinit var penulis: String
    private lateinit var genre: String
    private lateinit var desc: String
    private var uriBuku: Uri? = null
    private lateinit var firestoreRoot: FirebaseFirestore
    private lateinit var storageRoot: StorageReference

    object StaticData {
        const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Hooks
        tiJudul = findViewById(R.id.ti_judul_input)
        tiPenulis = findViewById(R.id.ti_penulis_input)
        tiGenre = findViewById(R.id.ti_genre_input)
        tiDesc = findViewById(R.id.ti_deskripsi_input)
        ivBuku = findViewById(R.id.iv_buku_input)
        ivBack = findViewById(R.id.iv_back)
        btnSave = findViewById(R.id.btn_save)
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar_input)
        progressBar = findViewById(R.id.progress_bar)


        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance()
        storageRoot = FirebaseStorage.getInstance().getReference("buku")

        // On Click
        btnSave.setOnClickListener(this)
        btnPilihGambar.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ivBuku.setOnLongClickListener(this)

    }


    private fun validateJudul() {
        if (tiJudul.editText!!.text.trim().isEmpty()) {
            tiJudul.error = "Judul tidak boleh kosong"
        } else {
            judul = tiJudul.editText!!.text.toString()
            tiJudul.error = null
        }
    }

    private fun validatePenulis() {
        if (tiPenulis.editText!!.text.trim().isEmpty()) {
            tiPenulis.error = "Penulis tidak boleh kosong"
        } else {
            penulis = tiPenulis.editText!!.text.toString()
            tiPenulis.error = null
        }
    }

    private fun validateGenre() {
        if (tiGenre.editText!!.text.trim().isEmpty()) {
            tiGenre.error = "Genre tidak boleh kosong"
        } else {
            genre = tiGenre.editText!!.text.toString()
            tiGenre.error = null
        }
    }

    private fun validateDesc() {
        if (tiDesc.editText!!.text.trim().isEmpty()) {
            tiDesc.error = "Deskripsi tidak boleh kosong"
        } else {
            desc = tiDesc.editText!!.text.toString()
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

    private fun openFileChooser() {
        val intent = Intent().setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, StaticData.PICK_IMAGE_REQUEST)
    }

    private fun getImageSize(image: Uri): Long {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageInByte: ByteArray = stream.toByteArray()
        val lengthbmp = imageInByte.size.toLong()
        return lengthbmp
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StaticData.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            if (getImageSize(data.data!!) > 1000000) {
                Toast.makeText(this, "Gambar melebihi 1 MB!", Toast.LENGTH_SHORT).show()
                return
            }
            uriBuku = data.data!!
            Glide.with(this).load(uriBuku).into(ivBuku)
        }
    }

    private fun getExtension(uri: Uri): String {
        val cR: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri)).toString()
    }

    private fun getSlug(judul: String): ArrayList<String> {
        val text = judul.lowercase().trim()
        val slug = arrayListOf<String>()
        for (i in judul.indices) {
            for (j in i until judul.length) {
                slug.add(text.substring(i, j+1))
            }
        }
        return slug
    }

    private fun refreshState(pb: Boolean, buttons: Boolean) {
        progressBar.isVisible = pb
        btnSave.isEnabled = buttons
        btnPilihGambar.isEnabled = buttons
    }


    private fun saveData() {
        if (allValidation()) {
            refreshState(pb = true, buttons = false)
            val id = UUID.randomUUID().toString()

            // Jika Gambar Tidak Kosong
            if (uriBuku != null) {

                val gambar = "${System.currentTimeMillis()}.${getExtension(uriBuku!!)}"
                val bukuModel = BukuModel(
                    id = id,
                    judul = judul,
                    penulis = penulis,
                    genre = genre,
                    deskripsi = desc,
                    gambar = gambar,
                    slug = getSlug(judul)
                )

                // Simpan Gambar ke Firebase Storage
                storageRoot.child(gambar).putFile(uriBuku!!)
                    .addOnSuccessListener {

                        // Simpan data ke Firestore
                        firestoreRoot.document("buku/$id").set(bukuModel)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Input Data Berhasil!", Toast.LENGTH_SHORT).show()
                                refreshState(pb = false, buttons = true)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Input Data Gagal!", Toast.LENGTH_SHORT).show()
                                refreshState(pb = false, buttons = true)
                            }

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Storage gagal!", Toast.LENGTH_SHORT).show()
                        refreshState(pb = false, buttons = true)
                    }

            }
            // Jika Gambar Kosong
            else {
                val bukuModel = BukuModel(
                    id = id,
                    judul = judul,
                    penulis = penulis,
                    genre = genre,
                    deskripsi = desc,
                    slug = getSlug(judul)
                )
                // Hanya simpan data ke Firestore
                firestoreRoot.document("buku/$id").set(bukuModel)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Input Data Berhasil!", Toast.LENGTH_SHORT).show()
                        refreshState(pb = false, buttons = true)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Input Data Gagal!", Toast.LENGTH_SHORT).show()
                        refreshState(pb = false, buttons = true)
                    }
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save -> {
                saveData()
            }

            R.id.btn_pilih_gambar_input -> {
                openFileChooser()
            }

            R.id.iv_back -> {
                finish()
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.iv_buku_input -> {
                val dialogItems = arrayOf("Hapus Gambar")
                MaterialAlertDialogBuilder(this).setItems(
                    dialogItems
                ) { _, p1 ->
                    when (p1) {
                        0 -> {
                            uriBuku = null
                            Glide.with(this).load(R.drawable.book).into(ivBuku)
                        }
                    }
                }.show()
            }
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}