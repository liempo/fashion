package com.fourcode.clients.fashion.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.*
import android.widget.TextView.BufferType.EDITABLE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fourcode.clients.fashion.AuthActivity
import com.fourcode.clients.fashion.MainActivity
import com.fourcode.clients.fashion.R
import com.fourcode.clients.fashion.cart.CartFragment
import com.fourcode.clients.fashion.product.Product
import com.fourcode.clients.fashion.product.ProductListAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.*

class ProfileFragment : Fragment(), AnkoLogger {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: StorageReference

    private lateinit var products: RecyclerView
    private var imageToUpload: Uri? = null
    private lateinit var uid: String

    private var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID, "")
            edit = it.getBoolean(ARG_EDIT, false)
        }

        firestore = (activity as MainActivity).firestore
        storage = (activity as MainActivity).storage

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

        products = view.find(R.id.cart_recycler_view)

        with(products) {
            layoutManager = GridLayoutManager(context, 3)
        }

        return view.also { setHasOptionsMenu(true) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.title_profile)

        if (uid.isEmpty()) return

        // Check if edit will be enabled
        if (edit) {

            // Hide some UIs
            products.visibility = View.INVISIBLE
            product_list_label?.visibility = View.INVISIBLE
            cart_progress_bar.visibility = View.INVISIBLE

            // Show upload image button
            upload_button?.apply {

                visibility = View.VISIBLE
                setOnClickListener {
                    startActivityForResult(
                        Intent(ACTION_PICK).apply { type = "image/*" }, REQUEST_IMAGE)
                }
            }

            // Enable inputs
            name?.isEnabled = true
            about?.isEnabled = true
            shop_name?.isEnabled = true
            phone?.isEnabled = true
            address?.isEnabled = true

        } else {

            name?.setBackgroundResource(android.R.color.transparent)
            about?.setBackgroundResource(android.R.color.transparent)
            shop_name?.setBackgroundResource(android.R.color.transparent)
            phone?.setBackgroundResource(android.R.color.transparent)
            address?.setBackgroundResource(android.R.color.transparent)

            // Leave inputs disabled

            // Fetch user products
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
                }

            cart_progress_bar.visibility = View.INVISIBLE
        }

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
                    name?.setText(it.data?.get("name").toString(), EDITABLE)

                if (it.data?.containsKey("about") == true)
                    about?.setText(it.data?.get("about").toString(), EDITABLE)

                if (it.data?.containsKey("shopName") == true)
                    shop_name?.setText(it.data?.get("shopName").toString(), EDITABLE)

                if (it.data?.containsKey("phone") == true)
                    phone?.setText(it.data?.get("phone").toString(), EDITABLE)

                if (it.data?.containsKey("address") == true)
                    address?.setText(it.data?.get("address").toString(), EDITABLE)

                profile_progress_bar.visibility = View.INVISIBLE
            }
    }

    /* https://stackoverflow.com/questions/16928727/open-gallery-app-from-android-intent */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE &&
            resultCode == RESULT_OK &&
            data != null && data.data != null) {

            // Smart casting issues but won't crash anyway
            imageToUpload = data.data!!

            // Preview to bitmap
            profile_image.setImageBitmap(
                getBitmap(context?.contentResolver, imageToUpload))

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {

        // Show save icon instead of edit
        if (edit) menu?.findItem(R.id.edit_button)?.
            setIcon(R.drawable.ic_check_black_24dp)

        super.onPrepareOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {

        R.id.logout_button -> {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
            activity?.startActivity<AuthActivity>()
            true
        }

        R.id.view_cart_button -> {

            activity?.supportFragmentManager?.beginTransaction()?.
                replace(R.id.container, CartFragment.newInstance())?.
                addToBackStack("Cart")?.
                commit()
            true
        }

        R.id.edit_button -> {

            // Edit button will act as save button if edit mode is true
            if (edit) {

                // Update UI first
                name?.isEnabled = true
                about?.isEnabled = true
                shop_name?.isEnabled = true
                phone?.isEnabled = true
                address?.isEnabled = true
                profile_progress_bar.visibility = View.VISIBLE


                val profile = hashMapOf<String, Any>(
                    "name" to name.text.toString(),
                    "address" to address.text.toString(),
                    "phone" to phone.text.toString(),
                    "shopName" to shop_name.text.toString(),
                    "about" to about.text.toString()
                )

                // Image upload section section
                if (imageToUpload != null) {

                    // Convert to stream
                    // Tangina galit na galit hahaha
                    val stream = context!!.
                        contentResolver!!.
                        openInputStream(imageToUpload!!)!!

                    storage.child("profiles/$uid")
                        .putStream(stream)
                        .addOnSuccessListener { upload ->

                            upload.storage.downloadUrl.addOnSuccessListener {

                                info(it)
                                profile["image"] = it.toString()

                                firestore.collection(getString(R.string.collection_profiles))
                                    .document(uid).set(profile, SetOptions.merge())
                                    .addOnSuccessListener {
                                        context?.toast("Successfully " +
                                                "updated your profile.")?.show()
                                        profile_progress_bar.visibility = View.INVISIBLE
                                        activity?.supportFragmentManager?.popBackStack()
                                    }
                                    .addOnFailureListener {
                                        context?.toast("Failed " +
                                                "updating your profile.")?.show()
                                        profile_progress_bar.visibility = View.INVISIBLE
                                        activity?.supportFragmentManager?.popBackStack()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            profile_progress_bar.visibility = View.INVISIBLE
                            activity?.toast("Failed uploading image.")?.show()
                        }

                } else {
                    firestore.collection(getString(R.string.collection_profiles))
                        .document(uid).set(profile, SetOptions.merge())
                        .addOnSuccessListener {
                            context?.toast("Successfully " +
                                    "updated your profile.")?.show()
                            profile_progress_bar.visibility = View.INVISIBLE
                            activity?.supportFragmentManager?.popBackStack()
                        }
                        .addOnFailureListener {
                            context?.toast("Failed " +
                                    "updating your profile.")?.show()
                            profile_progress_bar.visibility = View.INVISIBLE
                            activity?.supportFragmentManager?.popBackStack()
                        }
                }
            } else {

                activity?.supportFragmentManager?.beginTransaction()?.
                    replace(R.id.container, newInstance(uid, true))?.
                    addToBackStack("Edit Profile")?.
                    commit()
            }

            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    companion object {

        internal const val ARG_UID = "uid"
        internal const val ARG_EDIT = "edit"
        private const val REQUEST_IMAGE = 1242

        @JvmStatic
        fun newInstance(uid: String, edit: Boolean = false) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                    putBoolean(ARG_EDIT, edit)
                }
            }
    }
}
