package com.fourcode.clients.fashion.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fourcode.clients.fashion.AuthActivity
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.fourcode.clients.fashion.product.Product
import com.fourcode.clients.fashion.product.ProductListAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class ProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var products: RecyclerView
    private lateinit var uid: String

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
            container, false
        )

        products = view.find(R.id.products_recycler_view)
        with(products) {
            layoutManager = GridLayoutManager(context, 3)
        }

        return view.also { setHasOptionsMenu(true) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.title_profile)

        if (uid.isEmpty()) return

        firestore.collection(getString(R.string.collection_profiles))
            .document(uid).get()
            .addOnSuccessListener {

                // Aah shit, here we go again

                // Looks weird, but this is a null check hahaha
                if (it.data?.containsKey("image") == true)
                    Glide.with(view)
                        .load(it.data?.get("image").toString())
                        .into(profile_image)

                if (it.data?.containsKey("name") == true)
                    name?.text = it.data?.get("name").toString()

                if (it.data?.containsKey("about") == true)
                    about?.text = it.data?.get("about").toString()

                if (it.data?.containsKey("shopName") == true)
                    shop_name?.text = it.data?.get("shopName").toString()

                if (it.data?.containsKey("phone") == true)
                    phone?.text = it.data?.get("phone").toString()

                if (it.data?.containsKey("address") == true)
                    address?.text = it.data?.get("address").toString()

            }

        firestore.collection(getString(R.string.collection_products)).get()
            .addOnSuccessListener { documents ->

                // Make activity local for smart casting
                val activity = activity ?: return@addOnSuccessListener

                val items = arrayListOf<Product>()

                for (document in documents) {

                    // Parse this first before anything else
                    val userId = document.data["userId"].toString()

                    // Skip if uid does not match
                    if (userId != (activity as MainActivity).uid)
                        continue

                    val name = document.data["name"].toString()
                    val brand = document.data["brand"].toString()
                    val description = document.data["description"].toString()
                    val price = document.data["price"].toString().toFloat()
                    val image = document.data["image"].toString()
                    val categ = document.data["category"].toString()
                    val material = document.data["material"].toString()
                    val stock = document.data["stock"].toString().toFloat()
                    val created = (document.data["createdOn"] as Timestamp)

                    items.add(
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

                products.adapter = ProductListAdapter(activity, items)
                progress_bar.visibility = View.INVISIBLE

            }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId) {
        R.id.logout_button -> {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
            activity?.startActivity<AuthActivity>()
            true
        }

        R.id.edit_button -> {
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    companion object {

        internal const val ARG_UID = "uid"

        @JvmStatic
        fun newInstance(uid: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }
}
