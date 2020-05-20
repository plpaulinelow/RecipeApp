package com.example.recipeapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import java.util.*

class RecipeDetailActivity : AppCompatActivity() {

    //action bar
    private var actionBar:ActionBar?=null

    //db helper
    private var dbHelper:MyDbHelper?=null
    private var recordId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        //setting up actionbar
        actionBar = supportActionBar
        actionBar!!.title = "Record Details"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        //init db helper
        dbHelper = MyDbHelper(this)

        //get record id from intent
        val intent = intent
        recordId = intent.getStringExtra("RECORD ID")

        showRecordDetails()
        showIngredients()
    }

    private fun showRecordDetails(){
        //get record

        val selectQuery = "SELECT * FROM ${Constants.RECIPE_TABLE} WHERE ${Constants.C_ID} =\"$recordId\""
        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToFirst()){
            do{
                val id = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID))
                val name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val image = ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val type = ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE))
                val steps = ""+cursor.getString(cursor.getColumnIndex(Constants.C_STEPS))
                val dateAdded =""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_ADDED))
                val dateUpdated = ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_UPDATED))

                //convert time
                val  calendar1 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = dateAdded.toLong()
                val timeAdded = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar1)

                val  calendar2 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = dateUpdated.toLong()
                val timeUpdated = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar2)

                //set data
                textView_nameDetail.text = name
                textView_typeDetail.text = type
                textView_stepDetail.text = steps
                textView_dateAddedDetail.text = timeAdded
                textView_dateUpdatedDetail.text = timeUpdated

                if(image=="null"){
                    //no image in record
                    imageView_recipeDetail.setImageResource(R.drawable.ic_android)
                }else{
                    //image in record
                    imageView_recipeDetail.setImageURI(Uri.parse(image))
                }
            }while(cursor.moveToNext())
        }
        db.close()
    }

    private fun showIngredients(){
        val selectQuery = "SELECT * FROM ${Constants.INGREDIENTS_TABLE} WHERE ${Constants.C_RECIPE_ID} =\"$recordId\""
        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var allIngredients= ArrayList<String>()
        var all:String = ""
        var count=0
        if(cursor.moveToFirst()){
            do{
                val id = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID))
                val recipeId = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_RECIPE_ID))
                val name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val dateAdded =""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_ADDED))
                val dateUpdated = ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_UPDATED))

                //set data
                allIngredients.add(name)
            }while(cursor.moveToNext())

            for(item:String in allIngredients){
                if(count==0){
                    all=item
                }else{
                    all+=", " + item
                }
                count++
            }
            textView_ingredientDetail.text= all
        }
        db.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
