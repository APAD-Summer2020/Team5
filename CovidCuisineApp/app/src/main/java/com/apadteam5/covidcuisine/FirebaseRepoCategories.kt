package com.apadteam5.covidcuisine

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class FirebaseRepoCategories {

    //private val firebaseAuth: FirebaseAuth =  FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCategoriesList(): Task<QuerySnapshot> {
        return db.collection("Categories").get()
    }

}