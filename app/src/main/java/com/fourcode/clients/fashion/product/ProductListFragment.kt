package com.fourcode.clients.fashion.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_product_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info

class ProductListFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var products: RecyclerView
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = (activity as MainActivity).firestore
        category = arguments?.getString(ARG_CATEGORY) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_product_list,
            container, false
        )

        products = view.find(R.id.cart_recycler_view)
        with(products) {
            layoutManager = GridLayoutManager(context, 2)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = category

        // Fetch categories from products
        firestore.collection(getString(R.string.collection_products)).get()
            .addOnSuccessListener { documents ->
                val featuredItems = arrayListOf<Product>()

                for (document in documents) {

                    val name = document.data["name"].toString()
                    val brand = document.data["brand"].toString()
                    val description = document.data["description"].toString()
                    val price = document.data["price"].toString().toFloat()
                    val image = document.data["image"].toString()
                    val categ = document.data["category"].toString()
                    val material = document.data["material"].toString()
                    val stock = document.data["stock"].toString().toFloat()
                    val userId = document.data["userId"].toString()
                    val created = (document.data["createdOn"] as Timestamp)

                    info("$category, $categ")
                    if (category.isNotEmpty() && category != categ)
                        continue

                    featuredItems.add(
                        Product(
                            documentId = document.id,
                            brand = brand,
                            category = categ,
                            dateCreated = created,
                            description = description,
                            image = image,
                            material = material,
                            name = name,
                            price = price,
                            stock = stock,
                            userId = userId
                        )
                    )

                }

                // Sort products by price
                featuredItems.sortWith(compareBy { it.price })

                // Need to set as local variable for smart casting reasons
                val activity = activity; if (activity != null) {
                    // hide progress_bar
                    cart_progress_bar?.visibility = View.INVISIBLE

                    // Show to UI
                    products.adapter = ProductListAdapter(activity, featuredItems)
                }

            }
    }

    companion object {

        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String = "") =
            ProductListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }

}