package com.apadteam5.covidcuisine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.category_item.view.*

class ResultsAdapter(private val resultsList: List<ResultsItem>) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        return ResultsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        val currentItem = resultsList[position]

        Glide.with(holder.image)
            .load(currentItem.image)
            .into(holder.image)
        holder.name.text = currentItem.name
        holder.description.text = currentItem.description
    }

    override fun getItemCount() = resultsList.size

    class ResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.image
        val name: TextView = itemView.name
        val description: TextView = itemView.description
    }
}