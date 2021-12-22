package com.example.handyboocue.session

import android.content.Context
import com.example.handyboocue.model.BukuModel
import com.example.handyboocue.statics.SessionName

class SessionManager(val ctx: Context, val sessionType: String) {
    private var myPref = ctx.getSharedPreferences(sessionType, Context.MODE_PRIVATE)
    private var editor = myPref.edit()


    fun createEditSession(bukuModel: BukuModel) {
        editor.clear()
        editor.putString(SessionName.KEY_ID, bukuModel.id)
        editor.putString(SessionName.KEY_JUDUL, bukuModel.judul)
        editor.putString(SessionName.KEY_GAMBAR, bukuModel.gambar)
        editor.putString(SessionName.KEY_PENULIS, bukuModel.penulis)
        editor.putString(SessionName.KEY_GENRE, bukuModel.genre)
        editor.putString(SessionName.KEY_DESC, bukuModel.deskripsi)
        editor.apply()
    }

    fun getEditSession(): BukuModel {
        return BukuModel(
            id = myPref.getString(SessionName.KEY_ID, "").toString(),
            judul = myPref.getString(SessionName.KEY_JUDUL, "").toString(),
            gambar = myPref.getString(SessionName.KEY_GAMBAR, "").toString(),
            penulis = myPref.getString(SessionName.KEY_PENULIS, "").toString(),
            genre = myPref.getString(SessionName.KEY_GENRE, "").toString(),
            deskripsi = myPref.getString(SessionName.KEY_DESC, "").toString()
        )
    }


}