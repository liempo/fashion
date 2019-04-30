package com.fourcode.clients.fashion

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert

class ARActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Check if device is support before initializing fragment
        if (isDeviceSupport().not())
            alert("OpenGL version must be above $MIN_OPENGL_VERSION.") {
                positiveButton("OK") { finish() }
            }.show()

        // Get the instance of arFragment
        val ux = supportFragmentManager.
            findFragmentById(R.id.ux_fragment) as ArFragment

        // Render 3D model
        var shirt: ModelRenderable? = null
        ModelRenderable.builder()
            .setSource(this, Uri.parse("casual.sfb"))
            .build()
            .thenAccept {
                shirt = it.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
            }

        var transformable: TransformableNode? = null
        ux.setOnTapArPlaneListener { hit, _, _ ->
            if (shirt == null) return@setOnTapArPlaneListener

            // Create anchor node from hit point
            val node = AnchorNode(hit.createAnchor()).apply {
                setParent(ux.arSceneView.scene)
            }

            // Create transformable (scalable) node from hit point
            transformable = TransformableNode(ux.transformationSystem).apply {
                renderable = shirt

                scaleController.minScale = 0.05f
                scaleController.maxScale = 0.85f
                localScale = localScale.scaled(0.15f)
                localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 180f)

                // The local scale of the TransformableNode
                // must be set before calling node.setParent
                setParent(node); select()
            }
        }

        ux.arSceneView.scene.addOnUpdateListener {
            // TODO get posenet data here and
            //  move transformable based on posenet data


        }
    }

    private fun getOpenGLVersion(): Double =
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo.glEsVersion.toDouble()

    private fun isDeviceSupport(): Boolean =
        (ArCoreApk.getInstance().checkAvailability(this)
                != UNSUPPORTED_DEVICE_NOT_CAPABLE) &&
                (getOpenGLVersion() > MIN_OPENGL_VERSION)

    companion object {
        private const val MIN_OPENGL_VERSION = 3.0
    }

}
