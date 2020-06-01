package com.example.recipeapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.imageView_recipe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recipe_row.*
import java.util.jar.Manifest

class CreateActivity : AppCompatActivity() {

    //action bar
    private var actionBar: ActionBar?=null

    //permissions
    private val CAMERA_REQUEST_CODE = 100;
    private val STORAGE_REQUEST_CODE = 101;
    private val IMAGE_PICK_CAMERA_CODE = 102;
    private val IMAGE_PICK_GALLERY_CODE = 103;

    //arrays of permission
    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    //var save in database
    private var id:String=""
    private var imageUri:Uri? = null
    private var name:String?=""
    private var steps:String?=""
    private var type:String?=""
    private var dateAdded:String?=""
    private var dateUpdated:String?=""

    private var isEditMode = false

    lateinit var dbHelper:MyDbHelper
    lateinit var adapterIngredient:AdapterIngredient

    var ingredientList = ArrayList<ModelIngredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        //init dbhelper
        dbHelper = MyDbHelper(this)


        val recipeTypes = resources.getStringArray(R.array.recipeTypes)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recipeTypes)
        spinner_type.adapter = spinnerAdapter
        spinner_type.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                type = recipeTypes[p2]
            }

        }

        //setting up actionbar
        actionBar = supportActionBar
        actionBar!!.title = "Create Recipe"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        //get data from intent
        val intent = intent
        isEditMode = intent.getBooleanExtra("isEditMode", false)
        if(isEditMode){
            //edit data, values from adapter
            actionBar!!.title = "Update Recipe"

            id = intent.getStringExtra("ID")
            name = intent.getStringExtra("NAME")
            type = intent.getStringExtra("TYPE")
            steps = intent.getStringExtra("STEPS")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))
            dateAdded = intent.getStringExtra("DATE_ADDED")
            dateUpdated = intent.getStringExtra("DATE_UPDATED")

            //set data into the views
            if(imageUri.toString()=="null"){
                //no image
                imageView_recipe.setImageResource(R.drawable.ic_add_camera)
            }else{
                //have image
                imageView_recipe.setImageURI(imageUri)
            }
            editText_recipeName.setText(name)
            editText_steps.setText(steps)
            var spinnerPos = spinnerAdapter.getPosition(type)
            spinner_type.setSelection(spinnerPos)
            ingredientList.clear()
            ingredientList = dbHelper.getIngredientsByRecipeId(id)
            adapterIngredient = AdapterIngredient(this, ingredientList)
            recycleView_ingredient.adapter = adapterIngredient
        }else{
            //new data, values from MainActivity

        }

        //init permission arrays
        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        //pick recipe image
        imageView_recipe.setOnClickListener{
            imagePickDialog();

        }
        //save button
        button_save.setOnClickListener{
            inputData()
        }

        //ingredient button
        imageButton_addIngredient.setOnClickListener{
            inputIngredient()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun inputIngredient(){
        var ingredient = editText_ingredient.text.toString().trim()
        adapterIngredient = AdapterIngredient(this, ingredientList)
        recycleView_ingredient.adapter = adapterIngredient
        if(!ingredient.equals("")){
            val timeStamp = "${System.currentTimeMillis()}"
            val modelIngredient = ModelIngredient("","$id",ingredient,timeStamp,timeStamp)
            ingredientList.add(modelIngredient)
        }else{
            Toast.makeText(this,"Please save recipe first...",Toast.LENGTH_SHORT)
        }
    }

    private fun inputData(){
        name = editText_recipeName.text.toString().trim()
        type = spinner_type.selectedItem.toString()
        steps = editText_steps.text.toString().trim()

        if(isEditMode){
            //editing
            val timeStamp = "${System.currentTimeMillis()}"
            val recipeId= dbHelper.updateRecord(
                "$id",
                "$name",
                "$imageUri",
                "$type",
                "$steps",
                "$dateAdded",
                "$timeStamp"
            )
            //dbHelper.deleteIngredientByRecipeId("$id")
            if(recipeId>0){
                Toast.makeText(this, "Recipe updated...", Toast.LENGTH_SHORT).show()
                dbHelper.deleteIngredientByRecipeId("$id")
                dbHelper.insertIngredients(ingredientList,"$id".toLong())
            }

        }else{
            //adding new
            //save data to db
            val timestamp = System.currentTimeMillis()
            val insert = dbHelper.insertRecord(
                ""+name,
                ""+imageUri,
                ""+type,
                ""+steps,
                "$timestamp",
                "$timestamp")
            if(insert>0){
                val ingId=dbHelper.insertIngredients(ingredientList,insert)
                Toast.makeText(this, "Recipe added...", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun imagePickDialog(){
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose image from")
        builder.setItems(options){dialog, which->
            if (which==0){
                if(!checkCameraPermissions()){
                    requestCameraPermission()
                }
                else{
                    pickFromCamera()
                }
            }
            else{
                if(!checkStoragePermission()){
                    requestStoragePermission()
                }
                else{
                    pickFromGallery()
                }
            }
        }
        //show dialog
        builder.show()
    }

    private fun pickFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE)
    }

    private fun checkStoragePermission():Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions():Boolean{
        val results = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return results && results1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty()) {
                        //if alllowed returns trues else false
                        val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                        val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                        if (cameraAccepted && storageAccepted) {
                            pickFromCamera()
                        } else {
                            Toast.makeText(this, "Camera and Storage permissions are required", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                STORAGE_REQUEST_CODE->{
                    if(grantResults.isNotEmpty()){
                        val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                        if(storageAccepted){
                            pickFromGallery()
                        }else{
                            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked will be received here
        if(resultCode == Activity.RESULT_OK){
            //image is picked
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //picked from gallery
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //picked camera
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //cropped image received
                val result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri = resultUri
                    //set image
                    imageView_recipe.setImageURI(resultUri)
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    //error
                    val error = result.error
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
