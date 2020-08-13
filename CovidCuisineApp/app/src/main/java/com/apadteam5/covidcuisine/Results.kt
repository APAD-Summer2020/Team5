package com.apadteam5.covidcuisine

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*

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

        val images = arrayListOf<String>()
        val names = arrayListOf<String>()
        val descriptions = arrayListOf<String>()

        db.collection("posts")
            .whereEqualTo("category", catName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents == null || documents.isEmpty) {
                    Toast.makeText(this, "There are no posts available!", Toast.LENGTH_SHORT).show()
                }
                else {
                    for (post in documents) {
                        images.add(post.get("imgURL") as String)
                        names.add(post.get("title") as String)
                        descriptions.add(post.get("content") as String)
                    }
                    val posts = generatedResultsList(catName, images, names, descriptions)
                    recycler_view_categories.adapter = ResultsAdapter(posts)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
        return ""
    } //End getData function

    private fun generatedResultsList(
        catName: String?, images: ArrayList<String>,
        names: ArrayList<String>,
        descriptions: ArrayList<String>
    ) : List<ResultsItem> {
        val list = ArrayList<ResultsItem>()

        for (post in 0 until names.size) {
            val image = images[post]
            val name = names[post]
            val description = descriptions[post]

            val item = ResultsItem(image, name, description)
            list += item
        } //End for loop
        return list
    } //End generatedResultsList function
} //End Results class