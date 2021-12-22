package com.example.handyboocue.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.example.handyboocue.R
import com.example.handyboocue.statics.DummyUser
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var animation: Animation
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hooks
        tvTitle = findViewById(R.id.tv_title)
        animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim)
        tvTitle.animation = animation

        // Set Firebase
        mAuth = FirebaseAuth.getInstance()

        // to Main Activity
        goToMainActivity()


    }

    private fun goToMainActivity() {
        handler = Handler(Looper.getMainLooper())

        if (mAuth.uid == null) {
            mAuth.signInWithEmailAndPassword(DummyUser.EMAIL, DummyUser.PASSWORD)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        runnable = Runnable {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        handler.postDelayed(runnable, 4000)

                    }
                }
        } else {
            runnable = Runnable {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            handler.postDelayed(runnable, 4000)
        }

    }

}