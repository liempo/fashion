package com.fourcode.clients.fashion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val activity: FragmentActivity, private val items: List<Category>):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.image.context)
            .load(item.image)
            .into(holder.image)

        holder.name.text = item.name
        holder.card.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProductListFragment.newInstance(item.name))
                .addToBackStack("")
                .commit()
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val card: CardView = view.card
        val image: ImageView = view.image
        val name: TextView = view.name
    }

    data class Category(
        val name: String,
        val image: String
    )

}