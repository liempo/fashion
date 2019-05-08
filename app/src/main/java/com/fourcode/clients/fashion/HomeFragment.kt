package com.fourcode.clients.fashion


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.AnkoLogger

import com.google.firebase.firestore.FirebaseFirestore

import com.glide.slider.library.SliderTypes.DefaultSliderView
import com.bumptech.glide.request.RequestOptions
import com.fourcode.clients.fashion.CategoryAdapter.*
import com.fourcode.clients.fashion.ProductListAdapter.*
import org.jetbrains.anko.find

class HomeFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var categories: RecyclerView
    private lateinit var featured: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = (activity as MainActivity).firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_home,
            container, false
        )

        categories = view.find(R.id.categories_recycler_view)
        with(categories) {
            layoutManager = GridLayoutManager(context, 3)
        }

        featured = view.find(R.id.featured_recycler_view)
        with(featured) {
            layoutManager = GridLayoutManager(context, 2)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = getString(R.string.title_home)

        // Get banners and add to UI
        firestore.collection(COLLECTION_BANNERS).get()
            .addOnSuccessListener {

                val requestOptions = RequestOptions().apply {
                    centerCrop()
                }

                for (document in it) {

                    banners_layout.addSlider(DefaultSliderView(context).apply {
                        image(document.data["image"].toString())
                        setRequestOption(requestOptions)
                        setProgressBarVisible(true)
                    })
                }
            }

        // Fetch categories from products
        firestore.collection(COLLECTION_PRODUCTS).get()
            .addOnSuccessListener { documents ->
                val categoryItems = hashMapOf<String, String>()
                val featuredItems = arrayListOf<Product>()

                for (document in documents) {

                    val name = document.data["name"].toString()
                    val brand = document.data["brand"].toString()
                    val description = document.data["description"].toString()
                    val price = document.data["price"].toString().toFloat()
                    val image = document.data["image"].toString()
                    val category = document.data["category"].toString()

                    if (category !in categoryItems)
                        categoryItems[category] = image

                    featuredItems.add( Product(
                        brand, description, category, image, name, price))
                }

                // Show categories to UI
                categories.adapter = CategoryAdapter(activity!!,
                    categoryItems.map { Category(it.key, it.value) }
                )

                // Sort products by price
                featuredItems.sortWith(compareBy { it.price })
                featured.adapter = ProductListAdapter(featuredItems)

            }

    }

    companion object {
        @JvmStatic fun newInstance() = HomeFragment()

        private const val COLLECTION_BANNERS = "banners"
        private const val COLLECTION_PRODUCTS = "products"
    }
}
