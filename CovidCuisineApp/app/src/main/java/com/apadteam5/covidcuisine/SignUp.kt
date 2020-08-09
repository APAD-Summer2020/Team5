package com.apadteam5.covidcuisine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity(), AdapterView.OnItemSelectedListener{
    private var usertypeSelected:String = ""
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //initializing spinner
        var spinner: Spinner? = null
        var arrayAdapter: ArrayAdapter<String>? = null

        //in order to display options on the spinner, it has to initialize with an empty string,Why!?
        val usertype = arrayListOf("Personal","Commercial")

        spinner = findViewById(R.id.usertype)
        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, usertype)
        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = this

        val signupButton = findViewById<Button>(R.id.signup)
        signupButton.setOnClickListener {
            performSignup(usertypeSelected)
        }

    }
    private fun performSignup(usertypeSelected:String){
        val username = findViewById<EditText>(R.id.username).text.toString()
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val subscriptions = arrayListOf("")

        val data = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password,
            "subscriptions" to subscriptions,
            "usertype" to usertypeSelected
        )

        db.collection("users").document(username).set(data)

            .addOnSuccessListener {
                Toast.makeText(this, "user created successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this,CreatePost::class.java)
                intent.putExtra("username",username)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("post","Error uploading post")}
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(applicationContext,"nothing selected",Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items:String = parent?.getItemAtPosition(position) as String
        println(items)
        Toast.makeText(applicationContext,"$items",Toast.LENGTH_LONG).show()
        usertypeSelected = items
    }
}
