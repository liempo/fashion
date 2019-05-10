package com.fourcode.clients.fashion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fourcode.clients.fashion.auth.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class AuthActivity : AppCompatActivity() {

    internal lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginFragment.newInstance())
            .commit()

    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null && DEBUG.not()) {
            finish(); startActivity<MainActivity>()
        }
    }

    companion object {
        private const val DEBUG = true
    }
}
