package com.apadteam5.covidcuisine

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.File
import java.util.*

private const val REQUEST_CODE = 42
private const val PERMISSION_REQUEST = 10

class CreatePost : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //initialize variables---------------------------------------------------------------------------------------------
    private var categorySelected:String = ""
    val db = Firebase.firestore
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_GALLERY_CODE = 0
    val categories = arrayListOf("")
    var selectedPhotoUri: Uri? = null
    private lateinit var photoFile: File
    val fileName = UUID.randomUUID().toString()
    //END OF INITALIZATION---------------------------------------------------------------------------------------------

    //start of the function.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        initLocatoinproviderClient()
        getUserLocation()
        //initializing spinner---------------------------------------------------------------------------------------------
        var spinner: Spinner?
        val arrayAdapter: ArrayAdapter<String>?

        db.collection("categories").get()
            .addOnSuccessListener { result->
                for (category in result){
                    categories.add(category.id)
                }
                Log.d("post", "Post successfully uploaded") }
            .addOnFailureListener { Log.w("post","Error uploading post")}

        spinner = findViewById(R.id.select_categories)
        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, categories)
        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = this
        //END OF SPINNER---------------------------------------------------------------------------------------------

        //onClickListens---------------------------------------------------------------------------------------------
        //select Photo listener
        select_photo.setOnClickListener {
            Log.d("createPost", "Try to upload image")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GALLERY_CODE)
        }

        //camera button listener
        val cameraButton = findViewById<Button>(R.id.camera)
        cameraButton.setOnClickListener{
            takePicture()
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
        //END OF LISTENERS---------------------------------------------------------------------------------------------
    }

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null
    private fun initLocatoinproviderClient(){
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun getUserLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else {
        fusedLocationProvider.lastLocation.addOnSuccessListener { location: Location? ->
            userLatitude = location?.latitude
            userLongitude = location?.longitude
            Log.d("Latitude", "$userLatitude")
            Log.d("Longitude", "$userLongitude")
            }
        }
    }

    //BUTTONS ONCLICK CALLS----------------------------------------------------------------------------------------------

    //take a photo with camera app
    private fun takePicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(fileName)
        val fileProvider = FileProvider.getUriForFile(this, "com.apadteam5.covidcuisine.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun getPhotoFile(fileName: String): File{
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName,".jpg",storageDirectory)
    }

    //submit function
    private fun performSubmit(categorySelected:String) {
        //initializing firebase
        val username = intent.getStringExtra("username")
        val postTitle = findViewById<EditText>(R.id.post_title)
        val postContent = findViewById<EditText>(R.id.post_content)
        val title = postTitle.text.toString()
        val content = postContent.text.toString()
        val longtitude = userLongitude
        val latitude = userLatitude
        val location = arrayListOf(longtitude,latitude)

        if (title == "") {
            Toast.makeText(this,"Please enter a title",Toast.LENGTH_LONG).show()
        }
        else if (categorySelected == "") {
            Toast.makeText(this,"Please select a category",Toast.LENGTH_LONG).show()
        }
        else if( selectedPhotoUri == null){
            Toast.makeText(this,"Please choose a photo",Toast.LENGTH_LONG).show()
        }
        else {
            uploadImageToFirebaseStorage(title)

            val data = hashMapOf(
                "author" to username,
                "category" to categorySelected,
                "content" to content,
                "title" to title,
                "location" to location
            )

            db.collection("posts").document(title).set(data)
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

    //END OF BUTTON ONCLICK CALLS-----------------------------------------------------------------------------------------

    //DATA SELECTIONS-----------------------------------------------------------------------------------------------------

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
                    println(it.toString())
                    saveImagetoDatabase(title, it.toString())
                }
            }
    }

    //save image url to database
    private fun saveImagetoDatabase(title:String, imgURL:String){
        val db = Firebase.firestore

        db.collection("posts").document(title).update("imgURL",imgURL)
            .addOnSuccessListener { Log.d("post", "Post successfully uploaded") }
            .addOnFailureListener { Log.w("post","Error uploading post")}
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(applicationContext,"nothing selected",Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val items:String = parent?.getItemAtPosition(position) as String
        categorySelected = items
    }

    //different activity functions
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("createPost", "Photo was selected")
            Toast.makeText(this, "photo Selected", Toast.LENGTH_SHORT).show()
            selectedPhotoUri = data.data
            imageView.setImageURI(selectedPhotoUri)
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
            Log.d("camera", "Picture has been taken")
            val takenImage= BitmapFactory.decodeFile(photoFile.absolutePath)
            //imageView.setImageBitmap(takenImage)
            val title = fileName

            //saving to camera
            MediaStore.Images.Media.insertImage(
                contentResolver,
                takenImage,
                title,
                "Image of $title"
            )
        }
    }

    //END OF DATA SELECTIONS-----------------------------------------------------------------------------------------
}
