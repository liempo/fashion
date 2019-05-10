package com.fourcode.clients.fashion.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.fourcode.clients.fashion.R

class ProfileFragment : Fragment() {

    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { uid = it.getString(ARG_UID) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
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
