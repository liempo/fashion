package com.fourcode.clients.fashion

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_ar.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.info

class ARActivity : AppCompatActivity(), AnkoLogger {

    private val detected = hashMapOf<AugmentedImage, TransformableNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Check if device is support before initializing fragment
        if (isDeviceSupport().not())
            alert("OpenGL version must be above $MIN_OPENGL_VERSION.") {
                positiveButton("OK") { finish() }
            }.show()

        upload_button.hide()

        // Render 3D model
        var shirt: ModelRenderable? = null

        ChooserDialog(this)
            .withFilter(false, false, "gltf")
            .withChosenListener { _, file ->

                ModelRenderable.builder()
                    .setSource(this, RenderableSource.builder()
                        .setSource(this, Uri.fromFile(file),
                            RenderableSource.SourceType.GLTF2)
                        .setRecenterMode(RenderableSource.RecenterMode.CENTER)

                        .build())
                    .setRegistryId(Uri.fromFile(file))
                    .build()
                    .thenAccept {
                        shirt = it.apply {
                            isShadowCaster = false
                            isShadowReceiver = false
                        }

                        upload_button.show()
                    }
                    .exceptionally { error(it) }
            }
            .withOnCancelListener {
                ModelRenderable.builder()
                    .setSource(this, Uri.parse("casual.sfb"))
                    .build()
                    .thenAccept {
                        shirt = it.apply {
                            isShadowReceiver = false
                            isShadowCaster = false
                        }

                        upload_button.show()
                    }
                    .exceptionally { error(it) }
            }
            .build()
            .show()

        upload_button.setOnClickListener { recreate() }

        // Get the instance of arFragment
        val ux = supportFragmentManager.
            findFragmentById(R.id.ux_fragment) as AugmentedImageFragment

        ux.arSceneView.scene.addOnUpdateListener {

            if (shirt == null) return@addOnUpdateListener

            val frame = ux.arSceneView.arFrame!!
            val images = frame.getUpdatedTrackables(AugmentedImage::class.java)

            info ("Images detected: ${images.size}")
            for (image in images) {

                if (detected.containsKey(image).not() && image.trackingState == TrackingState.TRACKING) {

                    val anchor = image.createAnchor(image.centerPose)
                    val node = AnchorNode(anchor).apply {
                        setParent(ux.arSceneView.scene)
                    }

                    detected[image] = TransformableNode(ux.transformationSystem).apply {
                        renderable = shirt

                        scaleController.minScale = 0.05f
                        scaleController.maxScale = 0.85f

                        localPosition = localPosition.apply { z = 0.2f; y = -0.1f }
                        localScale = localScale.scaled(0.25f)

                        localRotation = Quaternion.multiply(
                            Quaternion.axisAngle(Vector3(1f, 0f, 0f), 270f),
                            Quaternion.axisAngle(Vector3(0f, 1f, 0f), 180f)
                        )

                        // The local scale of the TransformableNode
                        // must be set before calling node.setParent
                        setParent(node); select()
                    }
                }
            }
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