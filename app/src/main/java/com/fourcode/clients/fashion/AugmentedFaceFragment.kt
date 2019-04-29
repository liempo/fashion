package com.fourcode.clients.fashion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class AugmentedFaceFragment: ArFragment() {

    // Set AR mode to AugmentedFace
    override fun getSessionConfiguration(session: Session?): Config =
        Config(session).apply {
            augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        }

    // Set camera facing of ARCore
    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA)
    }

    // Turn off plane discovery
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = super.onCreateView(inflater,
            container, savedInstanceState) as FrameLayout

        planeDiscoveryController.apply {
            hide(); setInstructionView(null)
        }

        return layout
    }

}