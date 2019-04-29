package com.fourcode.clients.fashion

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.info

class ARActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var faceRenderable: ModelRenderable
    private lateinit var faceTexture: Texture
    private val faceNodeMap = hashMapOf<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Check if device is support before initializing fragment
        if (isDeviceSupport().not())
            alert("OpenGL version must be above $MIN_OPENGL_VERSION.") {
                positiveButton("OK") { finish() }
            }.show()

        // Get the instance of arFragment
        val fragment = supportFragmentManager.
            findFragmentById(R.id.face_fragment) as AugmentedFaceFragment

        // Render 3D model
        ModelRenderable.builder()
            .setSource(this, Uri.parse("fox_face.sfb"))
            .build()
            .thenAccept {
                faceRenderable = it.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
            }

        // Load the face mesh texture.
        Texture.builder()
            .setSource(this, R.drawable.fox_face_mesh_texture)
            .build()
            .thenAccept { faceTexture = it }

        val sceneView = fragment.arSceneView

        // This is important to make sure that the camera
        // stream renders first so that the face mesh occlusion works correctly.
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        val scene = sceneView.scene

        // Setup scene hahaha
        scene.addOnUpdateListener {

            // Return if model is initialized
            if (this::faceRenderable.isInitialized.not()) {
                info { "faceRenderable is not initialized. Skipping." }
                return@addOnUpdateListener
            }

            if (this::faceTexture.isInitialized.not()) {
                info { "faceTexture is not initialized. Skipping." }
                return@addOnUpdateListener
            }

            info { "isSessionNull ${ sceneView.session == null }" }
            val faces = sceneView.session!!.
                getAllTrackables(AugmentedFace::class.java)

            for (face in faces) {
                if (faceNodeMap.containsKey(face))
                    continue

                faceNodeMap[face] = AugmentedFaceNode(face).apply {
                    setParent(scene)
                    faceRegionsRenderable = faceRenderable
                    faceMeshTexture = faceTexture
                }
            }

            val iterator = faceNodeMap.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val face = entry.key

                if (face.trackingState == TrackingState.STOPPED) {
                    val node = entry.value
                    node.setParent(null)
                    iterator.remove()
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
