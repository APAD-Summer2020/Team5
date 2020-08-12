package com.apadteam5.covidcuisine

import android.os.Bundle

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*
import kotlinx.android.synthetic.main.activity_results_main.*
import kotlinx.android.synthetic.main.category_item.*

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.apadteam5.covidcuisine.ui.main.SectionsPagerAdapter

class Results : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)
        recycler_view_categories.layoutManager = LinearLayoutManager(this)
        recycler_view_categories.setHasFixedSize(true)

        val db = Firebase.firestore
        val catName = intent.getStringExtra("catName")
        getData(db, catName)
    }

    private fun getData(db: FirebaseFirestore, catName: String?): Any {

        println(catName + " from getData()")

        val images = arrayListOf<String>()
        val names = arrayListOf<String>()
        val descriptions = arrayListOf<String>()


        db.collection("posts")
            .whereEqualTo("category", catName)
            .get()
            .addOnSuccessListener { documents ->
                for (post in documents) {
                    images.add(post.get("imgURL") as kotlin.String)
                    names.add(post.get("title") as kotlin.String)
                    descriptions.add(post.get("content") as kotlin.String)
                }
                // ADD TO THE HASHMAP




                val posts = generatedResultsList(catName, images, names, descriptions)
                recycler_view_categories.adapter = ResultsAdapter(posts)


            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
        return ""
    } // END FUNCTION getData

    private fun generatedResultsList(
        catName: String?, images: ArrayList<String>,
        names: ArrayList<String>,
        descriptions: ArrayList<String>
    ) : List<ResultsItem> {
        val list = ArrayList<ResultsItem>()
        println(catName + " from generated ResultsList()")

        for (post in 0 until names.size) {
            val drawable = R.drawable.ic_android
            val image = images[post]
            val name = names[post]
            val description = descriptions[post]
            println(name)
            println(description)

            val item = ResultsItem(image, name, description)
            list += item
        }
        return list
    }
}