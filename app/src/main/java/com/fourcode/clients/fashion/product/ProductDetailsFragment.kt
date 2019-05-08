package com.fourcode.clients.fashion.product


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.AnkoLogger

class ProductDetailsFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = (activity as MainActivity).firestore
        arguments?.let { productId = it.getString(ARG_PRODUCT_ID) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }



    companion object {

        private const val ARG_PRODUCT_ID = "productId"
        @JvmStatic fun newInstance(productId: String) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_ID, productId)
                }
            }
    }
}
