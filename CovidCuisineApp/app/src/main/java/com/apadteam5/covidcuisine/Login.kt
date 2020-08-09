package com.apadteam5.covidcuisine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val loginButton = findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            performLogin()
        }

        val signupButton = findViewById<Button>(R.id.signup)
        signupButton.setOnClickListener {
            performSignup()
        }



    }

    private fun performLogin(){

        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        db.collection("users").document(username).get()
            .addOnSuccessListener { result ->
                val correct_pass = result.getString("password")
                println(correct_pass)

                if(result == null){
                    Toast.makeText(this,"Wrong Username or Password",Toast.LENGTH_LONG).show()
                }
                else if(correct_pass != password){
                    Toast.makeText(this,"Wrong Username or Password",Toast.LENGTH_LONG).show()
                }
                else{
                    val intent = Intent(this,CreatePost::class.java)
                    intent.putExtra("username",username)
                    startActivity(intent)
                    Toast.makeText(this,"logged in!",Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener { e -> Log.w("error","Error logging in")}

    }
    private fun performSignup(){
        val intent = Intent(this,SignUp::class.java)
        startActivity(intent)
    }


}



