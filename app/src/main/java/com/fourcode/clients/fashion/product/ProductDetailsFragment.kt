package com.fourcode.clients.fashion.product


import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_product_details.*
import org.jetbrains.anko.AnkoLogger
import java.text.SimpleDateFormat
import java.util.*

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
    ): View? = inflater.inflate(
        R.layout.fragment_product_details,
        container, false).also {
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firestore.collection(getString(R.string.collection_products))
            .document(documentId).get()
            .addOnSuccessListener { product ->

                if (image != null)
                    Glide.with(view)
                        .load(product.data?.get("image").toString())
                        .into(image)

                // It fucking sucks that the DocumentSnapshot is made like this
                // when DocumentQuerySnapshot return null. I wanna die.
                // Need a null check before accessing data attribute. Fuck
                name?.text = product.data?.get("name").toString()
                brand?.text = product.data?.get("brand").toString()

                activity?.title = ""

                description.text = Html.fromHtml(
                    product.data?.get("description").toString(),
                    Html.FROM_HTML_MODE_COMPACT
                )

                price?.text = getString(
                    R.string.format_price,
                    product.data?.get("price").toString().toFloat()
                )

                stock?.text = getString(R.string.format_stock, product.data?.get("stock"))
                material?.text = getString(R.string.format_material, product.data?.get("material"))

                created_on?.text = getString(
                    R.string.format_created_on,
                    SimpleDateFormat(getString(R.string.format_date), Locale.getDefault())
                        .format((product.data?.get("createdOn") as Timestamp).toDate())
                )

                val userId = product.data?.get("userId").toString()

                // Need to fetch userId details to adapt firebase (uhh)
                firestore.collection(getString(R.string.collection_profiles))
                    .document(userId).get()
                    .addOnSuccessListener { profile ->

                        // Fetch and bind data at the same time (added safe calls)
                        shop_name?.text = profile.data?.get("shopName").toString()
                        shop_owner?.text = profile.data?.get("name").toString()
                        phone?.text = profile.data?.get("phone").toString()
                        address?.text = profile.data?.get("address").toString()

                    }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_product_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {

        private const val ARG_DOCUMENT_ID = "documentId"
        @JvmStatic
        fun newInstance(id: String) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DOCUMENT_ID, id)
                }
            }
    }
}
