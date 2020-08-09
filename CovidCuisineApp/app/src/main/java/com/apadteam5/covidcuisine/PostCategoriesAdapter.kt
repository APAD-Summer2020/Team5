package com.apadteam5.covidcuisine


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*
import java.net.URI

private const val POST_TYPE_DESCRIPTION: Int = 0
private const val POST_TYPE_IMAGE: Int = 1

class PostCategoriesAdapter (var postCategoryItems: List<CategoryItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class DescViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(categoryItem: CategoryItem){
            itemView.category_textview_1.text = categoryItem.text1
            //itemView.desc_post_title.text = categoryItem.description1
        }
    }

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(categoryItem: CategoryItem){
            itemView.category_textview_1.text = categoryItem.text1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == POST_TYPE_DESCRIPTION){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
            return DescViewHolder(view) }
        else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
            return ImageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return postCategoryItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(postCategoryItems[position].category_type == 0L){
            POST_TYPE_DESCRIPTION
        } else{
            POST_TYPE_IMAGE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == POST_TYPE_DESCRIPTION){
            (holder as DescViewHolder).bind(postCategoryItems[position])
        } else{
            (holder as ImageViewHolder).bind(postCategoryItems[position])
        }
    }

}