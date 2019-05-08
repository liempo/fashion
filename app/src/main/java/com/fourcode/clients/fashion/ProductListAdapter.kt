package com.fourcode.clients.fashion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category.view.image
import kotlinx.android.synthetic.main.item_category.view.name
import kotlinx.android.synthetic.main.item_product.view.*

class ProductListAdapter(private val items: List<Product>):
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

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val image: ImageView = view.image
        val name: TextView = view.name
        val price: TextView = view.price

    }

    data class Product(

        val brand: String,
        val description: String,
        val category: String,
        val image: String,
        val name: String,
        val price: Float
    )

}