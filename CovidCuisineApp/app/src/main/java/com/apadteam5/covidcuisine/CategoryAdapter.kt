package com.apadteam5.covidcuisine

import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
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

            if (currentItem.imgObj1 !== null) {
                Glide.with(holder.imageView)
                    .load(currentItem.imgObj1)
                    .into(holder.imageView)
            }
            holder.categoryName.text = currentItem.categoryName
            holder.categoryDesc.text = currentItem.categoryDesc
            //GlideApp.with(this).load(imgObj1).into(holder.imageView)
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

                    intent.putExtra("catName", "$catName")

                    view.context.startActivity(intent)
                }
            }
    /*
            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }
        }

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }*/
        }
}
