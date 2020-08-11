package com.apadteam5.covidcuisine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter(
    private val categoryList: List<CategoryItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]

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

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
        val imageView: ImageView = itemView.image
        val categoryName: TextView = itemView.name
        val categoryDesc: TextView = itemView.description

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}