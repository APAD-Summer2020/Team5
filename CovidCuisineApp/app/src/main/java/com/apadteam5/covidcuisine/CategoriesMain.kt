package com.apadteam5.covidcuisine

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*
import java.io.InputStream


class CategoriesMain : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)

        //val categoryList = generateCategoriesList()



        val categories_names = arrayListOf<String>()
        val categories_descriptions = arrayListOf<String>()
        val categories_images = arrayListOf<String>()

        val db = Firebase.firestore
        //val db = FirebaseFirestore.getInstance()
        db.collection("categories").get()
            .addOnSuccessListener { result ->
                for (cat in result) {
                    categories_names.add(cat.get("name") as String)
                    categories_descriptions.add(cat.get("description") as String)
                    categories_images.add(cat.get("imgURL") as String)
                }

                val categoryList = generateCategoriesList(categories_names, categories_descriptions, categories_images)

                recycler_view_categories.adapter = CategoryAdapter(categoryList)
                recycler_view_categories.layoutManager = LinearLayoutManager(this)
                recycler_view_categories.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }


    }

    private fun generateCategoriesList(categories_names : ArrayList<String>, categories_descriptions : ArrayList<String>, categories_images : ArrayList<String>): List<CategoryItem> {
        //val categories_names = arrayListOf("")




        /*
        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                for (cat in result) {
                    cat.getString("name")?.let { categories_names.add(it) }
                }
            }

         */

        //categories_names.add("Number One")
        //categories_names.add("Number two")
        //categories_names.add("Number Three")

        val list = ArrayList<CategoryItem>()
        for (cat in 0 until categories_names.size) {
            val drawable = R.drawable.ic_android
            //val textObj1 = "Hello"
            val textObj1 = categories_names[cat]
            val textObj2 = categories_descriptions[cat]
            //val textObj1: String = cat.get("name") as String
            //val textObj2: String = cat.get("description") as String
            //val imageObj1: String = cat.get("imgURL") as String
            val drawable2 = categories_images[cat]




            val item = CategoryItem(drawable, textObj1, textObj2)
            list += item

        }
        return list
    }
}


/*

val list = ArrayList<CategoryItem>()
val size = 5
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

}*/