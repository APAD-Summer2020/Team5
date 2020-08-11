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
        val posts: HashMap<String, String>
        val query = db.collection("posts")
            .whereEqualTo(type, filterValue)
            .get()
            .addOnSuccessListener { posts ->
                for (post in posts) {
                    var image = post.get("imgURL") as String
                    var title = post.get("title") as String
                    var description = post.get("content") as String

                    // ADD TO THE HASHMAP
                    //posts.add(image, title, description)
                }
            }
        return ""
    }
}