package com.apadteam5.covidcuisine


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.File
import java.util.*
import java.util.jar.Manifest

private const val REQUEST_CODE = 42
private const val PERMISSION_REQUEST = 10


class CreatePost : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //initialize variables---------------------------------------------------------------------------------------------
    private var categorySelected:String = ""
    val db = Firebase.firestore
    private val REQUEST_IMAGE_CAPTURE =1
    private val REQUEST_GALLERY_CODE = 0
    val categories = arrayListOf("")
    var selectedPhotoUri: Uri? = null
    private lateinit var photoFile: File
    val fileName = UUID.randomUUID().toString()
    //private var PERMISSION_ID = 1000
    //END OF INITALIZATION---------------------------------------------------------------------------------------------

    //start of the function.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        //initializing spinner---------------------------------------------------------------------------------------------
        var spinner:Spinner? = null
        var arrayAdapter: ArrayAdapter<String>? = null

        db.collection("categories").get()
            .addOnSuccessListener { result->
                for (category in result){
                    //Log.d("tag", "${category.id} => $ ${category.data}") }
                    categories.add(category.id)
                }
                Log.d("post", "Post successfully uploaded") }

            .addOnFailureListener { e -> Log.w("post","Error uploading post")}

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

    //BUTTONS ONCLICK CALLS-----------------------------------------------------------------------------------------

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
        val longtitude = 1.2032
        val latitude = 2.4123
        val location = arrayListOf("Longitude: $longtitude","Latitude: $latitude")



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
                "title" to title,
                "location" to location
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

    //END OF BUTTON ONCLICK CALLS-----------------------------------------------------------------------------------------



    //DATA SELECTIONS-----------------------------------------------------------------------------------------

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

    //listener for location:
    /*
    private fun CheckPermission(): Boolean{
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ){
            return true
        }
        return false
    }
*/
    //get user permission
    /*
    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

     */

    //END OF DATA SELECTIONS-----------------------------------------------------------------------------------------
}