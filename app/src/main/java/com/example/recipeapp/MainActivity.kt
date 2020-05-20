package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recycleView_main

class MainActivity : AppCompatActivity() {

    //db helper
    lateinit var dbHelper: MyDbHelper

    //orderby sql queries
    private val NEWEST_FIRST = "${Constants.C_DATE_ADDED} DESC"
    lateinit var recipeTypes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //dbhelper
        dbHelper = MyDbHelper(this)

        loadRecords()

        button_addRecipe.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra("isEditMode", false)//new record, set it false
            // start your next activity
            startActivity(intent)
        }

        recipeTypes = resources.getStringArray(R.array.recipeTypes)

    }

    private fun loadRecords(){
        val adapterRecipe = AdapterRecipe(this, dbHelper.getAllRecipes(NEWEST_FIRST))

        recycleView_main.adapter = adapterRecipe
    }

    private fun loadFilterRecords(filter:String){
        val adapterRecipe = AdapterRecipe(this, dbHelper.getRecipesByType(filter))

        recycleView_main.adapter = adapterRecipe
    }

    public override fun onResume() {
        super.onResume()
        loadRecords()
    }


    private fun filterDialog(){
        val options= resources.getStringArray(R.array.recipeTypes)
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Pick a type")
            .setItems(options){_, which ->
                //handles items
                if(which==0){
                    loadRecords()
                }else{
                    loadFilterRecords(recipeTypes[which])
                }
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu items
        val id = item.itemId
        if(id==R.id.action_filter){
            filterDialog()
        }else{

        }

        return super.onOptionsItemSelected(item)
    }
}
