package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase

class all_categories_firestore : AppCompatActivity() {

    private val firebaseRepo: FirebaseRepoCategories = FirebaseRepoCategories()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_categories_firestore)
    }
}