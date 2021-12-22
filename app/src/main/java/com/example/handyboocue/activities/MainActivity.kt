package com.example.handyboocue.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handyboocue.R
import com.example.handyboocue.adapter.BukuAdapter
import com.example.handyboocue.model.BukuModel
import com.example.handyboocue.session.SessionManager
import com.example.handyboocue.statics.SessionName
import com.example.handyboocue.wrapper.WrapLinearLayout
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity(), View.OnClickListener, BukuAdapter.OnItemClickCallback {

    private lateinit var tiSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var fab: FloatingActionButton
    private lateinit var rvBuku: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var firestoreRoot: FirebaseFirestore
    private lateinit var storageRoot: StorageReference
    private lateinit var sessionManager: SessionManager
    private lateinit var detailManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hooks
        tiSearch = findViewById(R.id.ti_search)
        etSearch = findViewById(R.id.et_search)
        fab = findViewById(R.id.fab)
        rvBuku = findViewById(R.id.rv_buku)
        progressBar = findViewById(R.id.progress_bar_main)

        // Set Firebase
        firestoreRoot = FirebaseFirestore.getInstance()
        storageRoot = FirebaseStorage.getInstance().getReference("buku")

        // Set Session
        sessionManager = SessionManager(this, SessionName.EDIT_SESSION)
        detailManager = SessionManager(this, SessionName.DETAIL_SESSION)

        setSearchBar()
        setReyclerView()

        // On Click
        fab.setOnClickListener(this)

    }

    private fun setReyclerView() {

        val query: Query = firestoreRoot.collection("buku")

        val options = FirestoreRecyclerOptions.Builder<BukuModel>()
            .setLifecycleOwner(this)
            .setQuery(query, BukuModel::class.java)
            .build()

        rvBuku.layoutManager = WrapLinearLayout(this)
        rvBuku.adapter = BukuAdapter(options, this)
    }

    private fun searching(hint: String) {
        val query: Query = firestoreRoot.collection("buku").whereArrayContains("slug", hint)

        val options = FirestoreRecyclerOptions.Builder<BukuModel>()
            .setLifecycleOwner(this)
            .setQuery(query, BukuModel::class.java)
            .build()

        rvBuku.layoutManager = WrapLinearLayout(this)
        rvBuku.adapter = BukuAdapter(options, this)
    }

    private fun setSearchBar() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    etSearch.hint = resources.getString(R.string.cari_buku)
                    setReyclerView()
                } else {
                    searching(p0.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun refreshState(pb: Boolean) {
        progressBar.isVisible = pb
    }

    private fun deleteData (bukuModel: BukuModel) {
        refreshState(pb = true)
        firestoreRoot.document("buku/${bukuModel.id}").delete()
            .addOnSuccessListener {

                // Jika ada gambar, maka hapus dari storage
                if (bukuModel.gambar.isNotEmpty()) {
                    storageRoot.child(bukuModel.gambar).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Hapus Data Berhasil!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Hapus Data Gagal!", Toast.LENGTH_SHORT).show()
                            refreshState(pb = false)
                        }
                }

                // Jika tidak ada gambar, tidak perlu hapus storage
                else {
                    Toast.makeText(this, "Hapus Data Berhasil!", Toast.LENGTH_SHORT).show()
                    refreshState(pb = false)
                }

            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                startActivity(Intent(this, InputActivity::class.java))
            }
        }
    }

    override fun onLongPressed(bukuModel: BukuModel) {
        val dialogItems = arrayOf("Edit", "Hapus")
        MaterialAlertDialogBuilder(this)
            .setItems(dialogItems) { _, p1 ->
                when(p1) {
                    0 -> {
                        sessionManager.createEditSession(bukuModel)
                        startActivity(Intent(this, EditActivity::class.java))
                    }

                    1 -> {
                        deleteData(bukuModel)
                    }
                }
            }.show()
    }

    override fun onNormalPressed(bukuModel: BukuModel) {
        detailManager.createEditSession(bukuModel)
        startActivity(Intent(this, DetailActivity::class.java))
    }
}