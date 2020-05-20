package com.example.recipeapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recipe_row.view.*

class AdapterRecipe() : RecyclerView.Adapter<AdapterRecipe.HolderRecord>(){

    private var context:Context?=null
    private var recipeList:ArrayList<ModelRecipe>?=null

    lateinit var dbHelper:MyDbHelper

    constructor(context: Context?, recipeList: ArrayList<ModelRecipe>?):this(){
        this.context=context
        this.recipeList = recipeList

        dbHelper = MyDbHelper(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        //inflate layout
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.recipe_row,parent,false)
        )
    }

    override fun getItemCount(): Int {
        //return item size
        return recipeList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        //get data, set data

        //get data
        val model = recipeList!!.get(position)
        val id = model.id
        val name = model.name
        val image = model.image
        val type = model.type
        val steps = model.steps
        val dateAdded = model.dateAdded
        val dateUpdated = model.dateUpdated

        //set data to views
        holder.name.text = name
        holder.type.text = type

        if(image=="null"){
            //no image in record
            holder.image.setImageResource(R.drawable.ic_add_camera)
        }else{
            //image in record
            holder.image.setImageURI(Uri.parse(image))
        }

        //show record in new activity on clicking record
        holder.itemView.setOnClickListener{
            //parse id

            val intent = Intent(context,RecipeDetailActivity::class.java)
            intent.putExtra("RECORD ID", id)
            context!!.startActivity(intent)
        }

        holder.updateBtn.setOnClickListener{
            //show edit or delete
            showMoreOptions(
                position,
                id,
                name,
                type,
                steps,
                image,
                dateAdded,
                dateUpdated
            )
        }

    }

    private fun showMoreOptions(
        position: Int,
        id: String,
        name: String,
        type:String,
        steps:String,
        image: String,
        dateAdded: String,
        dateUpdated: String
    ){
        val options = arrayOf("Edit","Delete")
        val dialog:AlertDialog.Builder = AlertDialog.Builder(context)
        dialog.setItems(options) {dialog, which->
            //handle item clicks
            if(which==0){
                //edit clicked
                val intent = Intent(context, CreateActivity::class.java)
                intent.putExtra("ID", id)
                intent.putExtra("NAME", name)
                intent.putExtra("TYPE", type)
                intent.putExtra("STEPS", steps)
                intent.putExtra("IMAGE", image)
                intent.putExtra("DATE_ADDED", dateAdded)
                intent.putExtra("DATE_UPDATED", dateUpdated)
                intent.putExtra("isEditMode", true)
                context!!.startActivity(intent)
            }else{
                //delete clicked
                dbHelper.deleteRecord(id)
                //refresh page
                (context as MainActivity)!!.onResume()
            }
        }
        dialog.show()

    }

    inner class HolderRecord(itemView:View):RecyclerView.ViewHolder(itemView){

        //views from recipe_row.xml
        var image:ImageView = itemView.findViewById(R.id.imageView_recipe)
        var name:TextView = itemView.textView_name
        var type:TextView = itemView.textView_type
        var updateBtn:ImageButton = itemView.button_updateRecipe
    }
}