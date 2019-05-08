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
import org.jetbrains.anko.info

class ProductDetailsFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = (activity as MainActivity).firestore
        arguments?.let {
            documentId = it.getString(ARG_DOCUMENT_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore.collection(getString(R.string.collection_products))
            .document(documentId).get()
            .addOnSuccessListener {
                // It fucking sucks that the DocumentSnapshot is made like this
                // when DocumentQuerySnapshot return null. I wanna die.
                // Need a null check before accessing data attribute. Fuck



            }
    }

    companion object {

        private const val ARG_DOCUMENT_ID = "documentId"
        @JvmStatic fun newInstance(id: String) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DOCUMENT_ID, id)
                }
            }
    }
}
