package com.fourcode.clients.fashion.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.fourcode.clients.fashion.product.Product
import com.fourcode.clients.fashion.product.ProductListAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cart.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find

class CartFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var cart: HashMap<String, Int>
    private lateinit var products: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = (activity as MainActivity).firestore
        cart = (activity as MainActivity).cart
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_cart,
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
        activity?.title = getString(R.string.title_cart)

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

                    if (document.id !in cart.keys)
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
                            name = "$name x${cart[document.id]}",
                            price = price * cart[document.id]!!,
                            stock = stock,
                            userId = userId
                        )
                    )

                }

                var total = 0f
                val breakdown = featuredItems.joinToString("\n") {
                    val quantity = cart[it.documentId]!!
                    val subtotal = it.price * quantity.toFloat()
                    total += subtotal

                    "${it.name}  x$quantity  - ${getString(
                        R.string.format_price, subtotal)}"
                }

                // Need to set as local variable for smart casting reasons
                val activity = activity; if (activity != null) {
                    // hide progress_bar
                    cart_progress_bar?.visibility = View.INVISIBLE
                    subtotal_price?.text = breakdown
                    total_price?.text = getString(R.string.format_price, total)

                    // Show to UI
                    products.adapter = ProductListAdapter(activity, featuredItems)
                }

            }
    }

    companion object {
        fun newInstance() = CartFragment()
    }

}