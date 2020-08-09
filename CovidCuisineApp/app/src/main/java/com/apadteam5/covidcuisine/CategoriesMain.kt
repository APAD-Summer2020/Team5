package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_categories_main.*

class CategoriesMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)

        val categoryList = generateDummyList(500)

        recycler_view_categories.adapter = CategoryAdapter(categoryList)
        recycler_view_categories.layoutManager = LinearLayoutManager(this)
        recycler_view_categories.setHasFixedSize(true)
    }

    private fun generateDummyList(size: Int): List<CategoryItem> {
        val list = ArrayList<CategoryItem>()
        for (i in 0 until size) {
            val drawable = when (i % 3) {
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