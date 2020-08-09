package com.apadteam5.covidcuisine

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.annotation.GlideModule
import kotlinx.android.synthetic.main.category_item.view.*



class CategoryAdapter(private val categoryList: List<CategoryItem>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem_cat = categoryList[position]

        //holder.imageView.setImageResource(currentItem_cat.imgObj1)
        //holder.imageView.setImageBitmap()
        holder.textView1.text = currentItem_cat.text1
        holder.textView2.text = currentItem_cat.text2
        //GlideApp.with(this).load(imgObj1).into(holder.imageView)
    }

    override fun getItemCount() = categoryList.size



    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view_cat
        val textView1: TextView = itemView.text_view_1_cat
        val textView2: TextView = itemView.text_view_2_cat

    }

}