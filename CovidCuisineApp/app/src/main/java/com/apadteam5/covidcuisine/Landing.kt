package com.apadteam5.covidcuisine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Landing : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val username = intent.getStringExtra("username")
        val createPostButton = findViewById<Button>(R.id.create_post)

        createPostButton.setOnClickListener {
            val intent = Intent(this,CreatePost::class.java)
            intent.putExtra("username",username)
            startActivity(intent)
        }

        val AllCategoriesButton = findViewById<Button>(R.id.all_categories)
        AllCategoriesButton.setOnClickListener {
            val intent = Intent(this,CategoriesMain::class.java)
            startActivity(intent)
        }
    }
}