package com.example.handyboocue.activities

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.handyboocue.R
import com.example.handyboocue.model.BukuModel
import com.example.handyboocue.session.SessionManager
import com.example.handyboocue.statics.SessionName
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class EditActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var ivBuku: ImageView
    private lateinit var tiJudul: TextInputLayout
    private lateinit var tiPenulis: TextInputLayout
    private lateinit var tiGenre: TextInputLayout
    private lateinit var tiDesc: TextInputLayout
    private lateinit var btnPilih: MaterialButton
    private lateinit var btnEdit: MaterialButton
    private lateinit var ivBack: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager
    private lateinit var firestoreRoot: FirebaseFirestore
    private lateinit var storageRoot: StorageReference
    private lateinit var judul: String
    private lateinit var penulis: String
    private lateinit var genre: String
    private lateinit var desc: String
    private var uriBuku: Uri? = null
    private var isImageChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Hooks
        ivBuku = findViewById(R.id.iv_buku_edit)
        tiJudul = findViewById(R.id.ti_judul_edit)
        tiPenulis = findViewById(R.id.ti_penulis_edit)
        tiGenre = findViewById(R.id.ti_genre_edit)
        tiDesc = findViewById(R.id.ti_deskripsi_edit)
        btnPilih = findViewById(R.id.btn_pilih_gambar_edit)
        btnEdit = findViewById(R.id.btn_edit)
        ivBack = findViewById(R.id.iv_back_edit)
        progressBar = findViewById(R.id.progress_bar_edit)

        // Set Session
        sessionManager = SessionManager(this, SessionName.EDIT_SESSION)

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance()
        storageRoot = FirebaseStorage.getInstance().getReference("buku")

        setTampilan()

        // on Click
        ivBack.setOnClickListener(this)
        btnPilih.setOnClickListener(this)
        btnEdit.setOnClickListener(this)
        ivBuku.setOnLongClickListener(this)

    }

    private fun setTampilan() {
        val bukuModel = sessionManager.getEditSession()
        tiJudul.editText!!.setText(bukuModel.judul)
        tiPenulis.editText!!.setText(bukuModel.penulis)
        tiGenre.editText!!.setText(bukuModel.genre)
        tiDesc.editText!!.setText(bukuModel.deskripsi)

        // Jika Gambar tidak kosong
        if (bukuModel.gambar.isNotEmpty()) {
            refreshState(pb = true, buttons = false)
            storageRoot.child(bukuModel.gambar).downloadUrl
                .addOnSuccessListener {
                    uriBuku = it
                    refreshState(pb = false, buttons = true)
                    Glide.with(this).load(it).into(ivBuku)
                }
                .addOnFailureListener {
                    refreshState(pb = false, buttons = true)
                    Glide.with(this).load(R.drawable.book).into(ivBuku)
                    Toast.makeText(this, "Failed Retrieve Image!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Glide.with(this).load(R.drawable.book).into(ivBuku)
        }
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
        startActivityForResult(intent, InputActivity.StaticData.PICK_IMAGE_REQUEST)
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
        if (requestCode == InputActivity.StaticData.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            if (getImageSize(data.data!!) > 1000000) {
                Toast.makeText(this, "Gambar melebihi 1 MB!", Toast.LENGTH_SHORT).show()
                return
            }
            isImageChanged = true
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
                slug.add(text.substring(i, j + 1))
            }
        }
        return slug
    }

    private fun refreshState(pb: Boolean, buttons: Boolean) {
        progressBar.isVisible = pb
        btnPilih.isEnabled = buttons
        btnEdit.isEnabled = buttons
    }

    private fun editData() {
        if (allValidation()) {
            refreshState(pb = true, buttons = false)
            val id = sessionManager.getEditSession().id

            // Jika Gambar Tidak Kosong
            if (uriBuku != null) {
                // Jika Uri tidak sama dengan Uri yang sudah ada di storage,
                // perlu save ke storage lagi
                if (isImageChanged) {
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

                                    // Jika ada gambar di storage sebelumnya
                                    if (sessionManager.getEditSession().gambar.isNotEmpty()) {

                                        // Delete Image di Storage yang terdahulu
                                        storageRoot.child(sessionManager.getEditSession().gambar)
                                            .delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Edit Data Berhasil!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                refreshState(pb = false, buttons = true)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    "Edit Data Gagal!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                refreshState(pb = false, buttons = true)
                                            }
                                    }
                                    // Jika tidak ada gambar di storage sebelumnya, tidak perlu delete image
                                    else {
                                        Toast.makeText(
                                            this,
                                            "Edit Data Berhasil!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        refreshState(pb = false, buttons = true)
                                    }


                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Edit Data Gagal!", Toast.LENGTH_SHORT)
                                        .show()
                                    refreshState(pb = false, buttons = true)
                                }

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Storage gagal!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false, buttons = true)
                        }

                }
                // Jika Uri Sama dengan Uri Storage, maka tidak perlu save to storage
                else {
                    val bukuModel = BukuModel(
                        id = id,
                        judul = judul,
                        penulis = penulis,
                        genre = genre,
                        deskripsi = desc,
                        gambar = sessionManager.getEditSession().gambar,
                        slug = getSlug(judul)
                    )

                    // Simpan data ke Firestore
                    firestoreRoot.document("buku/$id").set(bukuModel)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Edit Data Berhasil!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false, buttons = true)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Edit Data Gagal!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false, buttons = true)
                        }
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
                    gambar = "",
                    slug = getSlug(judul)
                )
                // Simpan data ke Firestore
                firestoreRoot.document("buku/$id").set(bukuModel)
                    .addOnSuccessListener {

                        // Jika ada image sebelumnya, hapus dari storage
                        if (sessionManager.getEditSession().gambar.isNotEmpty()) {
                            storageRoot.child(sessionManager.getEditSession().gambar)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Edit Data Berhasil!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    refreshState(pb = false, buttons = true)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Edit Data Gagal!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    refreshState(pb = false, buttons = true)
                                }
                        }

                        // Jika tidak ada image sebelumnya, tidak perlu hapus storage
                        else {
                            Toast.makeText(this, "Edit Data Berhasil!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false, buttons = true)
                        }

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Edit Data Gagal!", Toast.LENGTH_SHORT).show()
                        refreshState(pb = false, buttons = true)
                    }
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_pilih_gambar_edit -> {
                openFileChooser()
            }

            R.id.btn_edit -> {
                editData()
                finish()
            }

            R.id.iv_back_edit -> {
                finish()
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.iv_buku_edit -> {
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