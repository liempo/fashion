package com.fourcode.clients.fashion.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fourcode.clients.fashion.R
import kotlinx.android.synthetic.main.item_product.view.*

class ProductListAdapter(private val activity: FragmentActivity, private val items: List<Product>):
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.image.context)
            .load(item.image)
            .into(holder.image)
        holder.name.text = item.name
        holder.price.text = holder.price.context.
            getString(R.string.format_price, item.price)

        holder.card.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    ProductDetailsFragment.newInstance(item.documentId)
                )
                .addToBackStack("")
                .commit()
        }

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val card: CardView = view.card
        val image: ImageView = view.image
        val name: TextView = view.name
        val price: TextView = view.price

    }

}