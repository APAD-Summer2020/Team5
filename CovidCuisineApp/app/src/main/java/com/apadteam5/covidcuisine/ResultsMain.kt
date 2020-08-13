package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*

//val adapterList_global2 = ArrayList<CategoryAdapter>()
//val categoryItemList_global2 = ArrayList<List<CategoryItem>>()

class ResultsMain : AppCompatActivity() {
    var positionInt = "8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_main)
        positionInt = intent.getStringExtra("numero").toString()
        println("received position" + positionInt)

        //val categoryList = generateCategoriesList()



        val pozt_names = arrayListOf<kotlin.String>()
        val pozt_descriptions = arrayListOf<kotlin.String>()
        val pozt_images = arrayListOf<kotlin.String>()
        var categoryName = ""

        val db = Firebase.firestore
        //val db = FirebaseFirestore.getInstance()
        var categoryIdx = 0
        db.collection("categories").get()
            .addOnSuccessListener { result ->
                for (cat in result) {
                    //categories_names.add(cat.get("name") as String)
                    //categories_descriptions.add(cat.get("description") as String)
                    //categories_images.add(cat.get("imgURL") as String)
                    if (categoryIdx == positionInt.toInt()){
                        categoryName = cat.get("name") as String
                    }

                    categoryIdx++
                }
            }
            .addOnFailureListener { exception ->
                Log.w("catError", "Error getting documents: ", exception)
            }


        db.collection("posts").get()
            .addOnSuccessListener { result ->
                for (pozt in result) {
                    //categories_names.add(cat.get("name") as String)
                    //categories_descriptions.add(cat.get("description") as String)
                    //categories_images.add(cat.get("imgURL") as String)
                    val categoryName2 = pozt.get("category") as kotlin.String
                    if (categoryName2 == categoryName){
                        pozt_names.add(pozt.get("title") as kotlin.String)
                        pozt_images.add(pozt.get("imgURL") as kotlin.String)
                        pozt_descriptions.add(pozt.get("content") as kotlin.String)

                    }

                    categoryIdx++
                }

                //categoryItemList_global2.add(generateCategoriesList(categories_names, categories_descriptions, categories_images))
                //val categoryList = categoryItemList_global2[0]
                val poztList = generateCategoriesList(pozt_names, pozt_images, pozt_descriptions)
                //adapterList_global2.add(CategoryAdapter(categoryList))
                //recycler_view_categories.adapter = adapterList_global2[0]
                recycler_view_categories.adapter = CategoryAdapter(poztList)

                recycler_view_categories.layoutManager = LinearLayoutManager(this)
                recycler_view_categories.setHasFixedSize(true)
            }
            .addOnFailureListener { exception ->
                Log.w("poztError", "Error getting documents: ", exception)
            }

    }

    private fun generateCategoriesList(pozt_names : ArrayList<String>, pozt_images : ArrayList<String>, pozt_descriptions : ArrayList<String>): List<CategoryItem> {


        val list = ArrayList<CategoryItem>()
        for (cat in 0 until pozt_names.size) {
            val drawable = R.drawable.ic_android
            //val textObj1 = "Hello"

            //val textObj1 = ("You Clicked " + positionInt)
            val textObj1 = pozt_names[cat]
            val textObj2 = pozt_descriptions[cat]
            //val textObj1: String = cat.get("name") as String
            //val textObj2: String = cat.get("description") as String
            //val imageObj1: String = cat.get("imgURL") as String
            val imgObj1 = pozt_images[cat]




            val item = CategoryItem(imgObj1, textObj1, textObj2)
            list += item

        }
        /*
        val drawable = R.drawable.ic_android
        //val textObj1 = "Hello"

        val textObj1 = (categoryName)
        ////val textObj1 = categories_names[cat]
        val textObj2 = (categoryName)
        //val textObj1: String = cat.get("name") as String
        //val textObj2: String = cat.get("description") as String
        //val imageObj1: String = cat.get("imgURL") as String
        val imgObj1 = (categoryName)




        val item = CategoryItem(imgObj1, textObj1, textObj2)
        list += item */



        return list
    }
}
