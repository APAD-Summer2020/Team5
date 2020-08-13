package com.apadteam5.covidcuisine

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*

val adapterList_global = ArrayList<CategoryAdapter>()
val categoryItemList_global = ArrayList<List<CategoryItem>>()

class CategoriesMain : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)

        //Initialize Lists
        val categories_names = arrayListOf<String>()
        val categories_descriptions = arrayListOf<String>()
        val categories_images = arrayListOf<String>()

        //Get categories from the database
        val db = Firebase.firestore
        db.collection("categories").get()
            .addOnSuccessListener { result ->
                for (cat in result) {
                    categories_names.add(cat.get("name") as String)
                    categories_descriptions.add(cat.get("description") as String)
                    categories_images.add(cat.get("imgURL") as String)
                }

                //Add them to the global lists
                categoryItemList_global.add(
                    generateCategoriesList(
                        categories_names,
                        categories_descriptions,
                        categories_images
                    )
                )

                val categoryList = categoryItemList_global[0]
                adapterList_global.add(CategoryAdapter(categoryList))
                recycler_view_categories.adapter = adapterList_global[0]
                recycler_view_categories.layoutManager = LinearLayoutManager(this)
                recycler_view_categories.setHasFixedSize(true)
            } //End .addOnSuccessListener
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
    } //End onCreate function

    private fun generateCategoriesList(
        categories_names: ArrayList<String>,
        categories_descriptions: ArrayList<String>,
        categories_images: ArrayList<String>
    ): List<CategoryItem> {
        val list = ArrayList<CategoryItem>()
        for (cat in 0 until categories_names.size) {
            val drawable = R.drawable.ic_android
            val textObj1 = categories_names[cat]
            val textObj2 = categories_descriptions[cat]
            val imgObj1 = categories_images[cat]

            val item = CategoryItem(imgObj1, textObj1, textObj2)
            list += item
        }
        return list
    } //End generateCategoriesList function
}