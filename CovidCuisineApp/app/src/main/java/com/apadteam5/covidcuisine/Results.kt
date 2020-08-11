package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
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

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_main)

        val arguments = requireNotNull(intent?.extras){"Meaningful message"}
        val type: String
        val position: String

        // GET PASSED IN VARIABLES
        with(arguments) {
            type = getString("type").toString()
            position = getString("position").toString()
        }

        Toast.makeText(this, "$type $position", Toast.LENGTH_SHORT).show()

        // GET FIRESTORE DATA
        val db = Firebase.firestore

        if (type == "tags") {
            var data = getData(db, "tags","american")
        }
        else {
            var data = getData(db, "category", "American")
        }
    }

    private fun getData(db: FirebaseFirestore, type: String, filterValue: String): Any {

        val images = arrayListOf<String>()
        val names = arrayListOf<String>()
        val descriptions = arrayListOf<String>()



        db.collection("posts")
            .whereEqualTo(type, filterValue)
            .get()
            .addOnSuccessListener { documents ->
                for (post in documents) {
                    images.add(post.get("imgURL") as String)
                    names.add(post.get("name") as String)
                    descriptions.add(post.get("description") as String)
                }
                // ADD TO THE HASHMAP
                val posts = List<ResultsItem>()

                posts.add(
                    generatedResultsList(images, names, descriptions)
                )

                recycler_view_results.adapter = ResultsAdapter(posts)
                recycler_view_results.layoutManager = LinearLayoutManager(this)
                recycler_view_results.setHasFixedSize(true)

            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
        return ""
        } // END FUNCTION getData
    } // END FUNCTION onCreate

    private fun generatedResultsList(
        images: ArrayList<String>,
        names: ArrayList<String>,
        descriptions: ArrayList<String>
    ) : List<ResultsItem> {
        val list = ArrayList<ResultsItem>()

        for (post in 0 until names.size) {
            val drawable = R.drawable.ic_android
            val image = images[post]
            val name = names[post]
            val description = descriptions[post]

            val item = ResultsItem(image, name, description)
            list += item
        }
        return list
    }
}