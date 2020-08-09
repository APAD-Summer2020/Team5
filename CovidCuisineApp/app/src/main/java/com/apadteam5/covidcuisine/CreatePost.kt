package com.apadteam5.covidcuisine

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.common.io.Files.append
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_post.*
import org.w3c.dom.Text
import java.util.*


class CreatePost : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var categorySelected:String = ""
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        //initializing spinner
        var spinner:Spinner? = null
        var arrayAdapter: ArrayAdapter<String>? = null

        //in order to display options on the spinner, it has to initialize with an empty string,Why!?
        val categories = arrayListOf("")

        db.collection("categories").get()
            .addOnSuccessListener { result->
                for (category in result){
                    //Log.d("tag", "${category.id} => $ ${category.data}") }
               categories.add(category.id)
            }
            println(categories)

                Log.d("post", "Post successfully uploaded") }
            .addOnFailureListener { e -> Log.w("post","Error uploading post")}

        spinner = findViewById(R.id.select_categories)
        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, categories)
        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = this



        //select Photo listener
        select_photo.setOnClickListener {
            Log.d("createPost", "Try to upload image")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //submit button listener
        val submitButton = findViewById<Button>(R.id.submit)
        submitButton.setOnClickListener {
            performSubmit(categorySelected)

        }

        //cancel button listener
        val cancelButton = findViewById<Button>(R.id.cancel)
        cancelButton.setOnClickListener{
            performCancel()
        }

    }

    //check for upload
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("createPost", "Photo was selected")
            Toast.makeText(this, "photo Selected", Toast.LENGTH_SHORT).show()

            selectedPhotoUri = data.data
            println(selectedPhotoUri)
        }
    }

    //submit function
    private fun performSubmit(categorySelected:String) {
        //initializing firebase


        val username = "test"
        val postTitle = findViewById<EditText>(R.id.post_title)
        val postContent = findViewById<EditText>(R.id.post_content)
        val title = postTitle.text
        val content = postContent.text

        uploadImageToFirebaseStorage(title.toString())

        val data = hashMapOf(
            "author" to username,
            "category" to categorySelected,
            "content" to content.toString(),
            "title" to title.toString()
        )

        db.collection("posts").document(title.toString()).set(data)

            .addOnSuccessListener {
                Toast.makeText(this,"Post posted successfully",Toast.LENGTH_LONG).show()
                post_title.text.clear()
                post_content.text.clear()
                Log.d("post", "Post successfully uploaded") }

            .addOnFailureListener { e -> Log.w("post","Error uploading post")}



    }

    //cancel function
    private fun performCancel(){
        post_title.text.clear()
        post_content.text.clear()

    }

    //upload image function
    private fun uploadImageToFirebaseStorage(title:String) {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val title = title
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Upload", "Successfully uploaded Image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Upload", "File Location: $it")
                    saveImagetoDatabase(title, it.toString())
                }

            }
    }

    private fun saveImagetoDatabase(title:String, imgURL:String){
        val db = Firebase.firestore

        db.collection("posts").document(title.toString()).update("imgURL",imgURL)
            .addOnSuccessListener { Log.d("post", "Post successfully uploaded") }
            .addOnFailureListener { e -> Log.w("post","Error uploading post")}

    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(applicationContext,"nothing selected",Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items:String = parent?.getItemAtPosition(position) as String
        println(items)
        Toast.makeText(applicationContext,"$items",Toast.LENGTH_LONG).show()
        categorySelected = items
    }
}
