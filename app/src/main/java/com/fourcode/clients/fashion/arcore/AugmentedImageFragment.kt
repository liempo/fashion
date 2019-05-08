package com.fourcode.clients.fashion.arcore

import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class AugmentedImageFragment: ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {

        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)

        val augmentedImageDb = AugmentedImageDatabase.deserialize(
            session, context!!.assets.open("database.imgdb"))

        return Config(session).apply {
            updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            augmentedImageDatabase = augmentedImageDb
        }

    }
}