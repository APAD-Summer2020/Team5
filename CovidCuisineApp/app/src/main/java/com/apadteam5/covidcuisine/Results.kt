package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_main)

        val arguments = requireNotNull(intent?.extras){"Meaningful message"}
        val type: String?
        val position: String?

        with(arguments) {
            type = getString("type")
            position = getString("position")
        }
        Toast.makeText(this, "$type $position", Toast.LENGTH_SHORT).show()

        //GET FIRESTORE DATA
        val db = Firebase.firestore

        if (type == "tags") {

        }
        else {
            val posts = db.collection("posts")
                .whereEqualTo("category", "category")
                .get()
                .addOnSuccessListener { posts ->
                    for (post in posts) {

                    }
                }
        }


        /*
        val db = Firebase.firestore
        db.collection("posts").get()
            .addOnSuccessListener { posts ->
                for (post in posts) {
                    categories_names.add(cat.get("name") as String)
                    categories_descriptions.add(cat.get("description") as String)
                    categories_images.add(cat.get("imgURL") as String)
                }

                categoryItemList_global.add(
                    generateCategoriesList(
                        categories_names,
                        categories_descriptions,
                        categories_images
                    )
                )
                val categoryList = categoryItemList_global[0]
                adapterList_global.add(CategoryAdapter(categoryList, this))
                recycler_view_categories.adapter = adapterList_global[0]
                //recycler_view_categories.adapter = CategoryAdapter(categoryList)

                recycler_view_categories.layoutManager = LinearLayoutManager(this)
                recycler_view_categories.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
        */



    }
}