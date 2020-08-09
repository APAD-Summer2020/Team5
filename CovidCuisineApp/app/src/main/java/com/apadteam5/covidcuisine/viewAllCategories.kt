package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_all_categories_firestore.*

class viewAllCategories : AppCompatActivity() {

    private val firebaseRepo: FirebaseRepoCategories = FirebaseRepoCategories()
    private var categoriesList: List<CategoryItem> = ArrayList()
    private val categoriesListAdapter: PostCategoriesAdapter = PostCategoriesAdapter(categoriesList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_categories)

        loadCategoryData()

        firestore_list_categories.layoutManager = LinearLayoutManager(this)
        firestore_list_categories.adapter = categoriesListAdapter




    }

    private fun loadCategoryData(){
        firebaseRepo.getCategoriesList().addOnCompleteListener{
            //if(it.isSuccessful){
                categoriesList = it.result!!.toObjects(CategoryItem::class.java)
                categoriesListAdapter.postCategoryItems = categoriesList
                categoriesListAdapter.notifyDataSetChanged()
            //}
        }
    }
}