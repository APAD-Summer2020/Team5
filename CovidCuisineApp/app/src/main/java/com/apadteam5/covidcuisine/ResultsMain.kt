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

val adapterResultsList_global = ArrayList<ResultsAdapter>()
val resultsItemList_global = ArrayList<List<ResultsItem>>()

class ResultsMain : AppCompatActivity(), ResultsAdapter.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)

        val categories_names = arrayListOf<String>()
        val categories_descriptions = arrayListOf<String>()
        val categories_images = arrayListOf<String>()

        val db = Firebase.firestore
        db.collection("categories").get()
            .addOnSuccessListener { result ->
                for (cat in result) {
                    categories_names.add(cat.get("name") as String)
                    categories_descriptions.add(cat.get("description") as String)
                    categories_images.add(cat.get("imgURL") as String)
                }

                resultsItemList_global.add(
                    generateCategoriesList(
                        categories_names,
                        categories_descriptions,
                        categories_images
                    )
                )
                val categoryList = resultsItemList_global[0]
                adapterResultsList_global.add(ResultsAdapter(categoryList, this))
                recycler_view_categories.adapter = adapterResultsList_global[0]
                recycler_view_categories.layoutManager = LinearLayoutManager(this)
                recycler_view_categories.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }
    }

    private fun generateCategoriesList(
        categories_names: ArrayList<String>,
        categories_descriptions: ArrayList<String>,
        categories_images: ArrayList<String>
    ): List<ResultsItem> {
        val list = ArrayList<ResultsItem>()
        for (cat in 0 until categories_names.size) {
            val drawable = R.drawable.ic_android
            //val textObj1 = "Hello"
            val textObj1 = categories_names[cat]
            val textObj2 = categories_descriptions[cat]
            //val textObj1: String = cat.get("name") as String
            //val textObj2: String = cat.get("description") as String
            //val imageObj1: String = cat.get("imgURL") as String
            val imgObj1 = categories_images[cat]

            val item = ResultsItem(imgObj1, textObj1, textObj2)
            list += item
        }
        return list
    }

    override fun onItemClick(position: Int) {
        val adapter = adapterResultsList_global[0]
        adapter.notifyItemChanged(position)

        val type = "category"

        val intent = Intent(this, ResultsMain::class.java)
        intent.putExtra("type", type)
        intent.putExtra("position", position.toString())
        Toast.makeText(this, "$type $position", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}