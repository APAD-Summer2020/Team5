package com.example.covidcuisine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class SpinnerActivity : AppCompatActivity() {

    lateinit var option : Spinner
    lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)

        option = findViewById(R.id.userType) as Spinner
        result = findViewById(R.id.spinnerTextview) as TextView

        val options = arrayOf("Personal", "Commercial")

        option.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)

        option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                result.text = "Please select your user type."
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                result.text = options.get(p2)
            }

        }
    }
}