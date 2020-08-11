package com.apadteam5.covidcuisine

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
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


        //cancel button listener
        val cameraButton = findViewById<Button>(R.id.camera)
        cameraButton.setOnClickListener{
            takePicture()
        }



    }
    //take a photo with camera app
    private val REQUEST_IMAGE_CAPTURE =1
    private fun takePicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
            Log.d("camera", "Picture has been taken")
        }
    }

    //submit function
    private fun performSubmit(categorySelected:String) {
        //initializing firebase

        val username = intent.getStringExtra("username")
        val postTitle = findViewById<EditText>(R.id.post_title)
        val postContent = findViewById<EditText>(R.id.post_content)
        val title = postTitle.text.toString()
        val content = postContent.text.toString()

        if(title == ""){
            Toast.makeText(this,"Please enter a title",Toast.LENGTH_LONG).show()
        }
        else if(categorySelected == ""){
            Toast.makeText(this,"Please select a category",Toast.LENGTH_LONG).show()
        }
        else {
            uploadImageToFirebaseStorage(title.toString())

            val data = hashMapOf(
                "author" to username,
                "category" to categorySelected,
                "content" to content,
                "title" to title
            )

            db.collection("posts").document(title.toString()).set(data)

                .addOnSuccessListener {
                    Toast.makeText(this, "Post posted successfully", Toast.LENGTH_LONG).show()
                    post_title.text.clear()
                    post_content.text.clear()
                    Log.d("post", "Post successfully uploaded")
                }

                .addOnFailureListener { e -> Log.w("post", "Error uploading post") }

        }






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
        categorySelected = items
    }
}
