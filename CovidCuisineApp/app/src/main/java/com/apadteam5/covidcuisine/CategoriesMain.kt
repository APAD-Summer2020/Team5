package com.apadteam5.covidcuisine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_categories_main.*

const val EXTRA_MESSAGE = "com.apadteam5.covidcuisine.MESSAGE"

class CategoriesMain : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private val categoryList = generateDummyList(5)
    private val adapter = CategoryAdapter(categoryList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)

        recycler_view_categories.adapter = adapter
        recycler_view_categories.layoutManager = LinearLayoutManager(this)
        recycler_view_categories.setHasFixedSize(true)
    }

    /*FUNCTION onItemClick POTENTIALLY NOT BEING USED*/
    override fun onItemClick(position: Int) {
        //Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val clickedItem = categoryList[position]
        adapter.notifyItemChanged(position)

        Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
    }

    private fun generateDummyList(size: Int): List<CategoryItem> {
        val list = ArrayList<CategoryItem>()
        for (i in 0 until size) {
            val drawable = when (i % 1) {
                0 -> R.drawable.ic_android
                1 -> R.drawable.ic_baseline_map
                else -> R.drawable.ic_baseline_category
            }
            val item = CategoryItem(drawable, "Item $i", "Line 2")
            list += item
        }
        return list
    }
}