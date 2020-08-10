package com.apadteam5.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_main)

        val arguments = requireNotNull(intent?.extras){"Meaningful message"}
        val type: String?
        val position: String?

        with(arguments) {
            type = getString("type")
            position = getString("position")
        }
        Toast.makeText(this, "$type $position", Toast.LENGTH_SHORT).show()

    }
}