package com.fourcode.clients.fashion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fourcode.clients.fashion.helpers.CameraPermissionHelper
import org.jetbrains.anko.toast

class ARActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
    }

    override fun onResume() {
        super.onResume()

        if (CameraPermissionHelper.hasCameraPermission(this).not())
            CameraPermissionHelper.requestCameraPermission(this)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {

        if (CameraPermissionHelper.hasCameraPermission(this).not())
            toast("Camera permission is needed to run this application")
        if (CameraPermissionHelper.shouldShowRequestPermissionRationale(this))
            // Permission denied with checking "Do not ask again".
            CameraPermissionHelper.launchPermissionSettings(this)

    }
}
