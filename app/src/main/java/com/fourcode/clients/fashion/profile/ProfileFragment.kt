package com.fourcode.clients.fashion.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var uid: String

    private lateinit var scaleDown: Animation
    private lateinit var scaleUp: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { uid = it.getString(ARG_UID, "") }
        firestore = (activity as MainActivity).firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_profile,
            container, false)

        // Load animations here cuz idk anywhere else to put it.
        // If i put it in onCreate will the context even be ready? Idk man.
        // I got a bad feeling about this.
        scaleDown = AnimationUtils.loadAnimation(
            context, R.anim.profile_image_scale_down)
        scaleUp = AnimationUtils.loadAnimation(
            context, R.anim.profile_image_scale_up)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = getString(R.string.title_profile)

        if (uid.isNotEmpty())
            firestore.collection(getString(R.string.collection_profiles))
                .document(uid).get()
                .addOnSuccessListener {

                    Glide.with(view)
                        .load(it.data?.get("image").toString())
                        .into(profile_image)

                    // Aah shit, here we go again

                    // Looks weird, but this is a null check hahaha
                    if (it.data?.containsKey("name") == true)
                        name?.text = it.data?.get("name").toString()

                    if (it.data?.containsKey("about") == true)
                        about?.text = it.data?.get("about").toString()

                    if (it.data?.containsKey("shopName") == true)
                        shop_name?.text = it.data?.get("shopName").toString()

                    if (it.data?.containsKey("phone") == true )
                        phone?.text = it.data?.get("phone").toString()

                    if (it.data?.containsKey("address") == true)
                        address?.text = it.data?.get("address").toString()

                }

    }

    companion object {

        internal const val ARG_UID = "uid"

        @JvmStatic fun newInstance(uid: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }
}
