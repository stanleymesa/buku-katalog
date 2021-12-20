package com.example.handyboocue.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.handyboocue.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tiSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hooks
        tiSearch = findViewById(R.id.ti_search)
        etSearch = findViewById(R.id.et_search)
        fab = findViewById(R.id.fab)

        setSearchBar()

        // On Click
        fab.setOnClickListener(this)

    }

    private fun setSearchBar() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    etSearch.hint = resources.getString(R.string.cari_buku)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                startActivity(Intent(this, InputActivity::class.java))
            }
        }
    }
}