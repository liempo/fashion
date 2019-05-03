package com.fourcode.clients.fashion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.content_web.*
import org.jetbrains.anko.startActivity

class WebActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://ar-web-ecommerce.herokuapp.com/")

        fab.setOnClickListener {
            startActivity<ARActivity>()
        }
    }

}
