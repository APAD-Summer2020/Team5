package com.apadteam5.covidcuisine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter(
    private val categoryList: List<CategoryItem>
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
            return CategoryViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val currentItem = categoryList.get(position)

            Glide.with(holder.imageView)
                .load(currentItem.imgObj1)
                .into(holder.imageView)
            holder.categoryName.text = currentItem.categoryName
            holder.categoryDesc.text = currentItem.categoryDesc
        }

        override fun getItemCount() = categoryList.size

        class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.image
            val categoryName: TextView = view.name
            val categoryDesc: TextView = view.description

            init {
                view.setOnClickListener {
                    val intent = Intent(view.context, Results::class.java)
                    val catName = view.name.getText().toString()

                    intent.putExtra("catName", catName)

                    view.context.startActivity(intent)
                }
            } //End of init block
        } //End of CategoryViewHolder class
} //End of CategoryAdapter class
