package com.example.recipeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ingredient_row.view.*
import kotlinx.android.synthetic.main.recipe_row.view.textView_name


class AdapterIngredient() : RecyclerView.Adapter<AdapterIngredient.HolderRecord>(){

    private var context: Context?=null
    private var ingredientList:ArrayList<ModelIngredient>?=null

    lateinit var dbHelper:MyDbHelper

    constructor(context: Context?, ingredientList: ArrayList<ModelIngredient>?):this(){
        this.context=context
        this.ingredientList = ingredientList

        dbHelper = MyDbHelper(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        //inflate layout
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.ingredient_row,parent,false)
        )
    }

    override fun getItemCount(): Int {
        //return item size
        return ingredientList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        //get data, set data

        //get data
        val model = ingredientList!!.get(position)
        val id = model.id
        val recipeId = model.recipeId
        val name = model.name

        //set data to views
        holder.name.text = name

        holder.updateBtn.setOnClickListener{
            //show edit or delete

            if(!model.recipeId.equals("") && model.id!=null){
                //delete in db
                dbHelper.deleteIngredient(model.id.toString())
            }
            ingredientList?.removeAt(position)
            notifyDataSetChanged()
        }

    }

    inner class HolderRecord(itemView: View): RecyclerView.ViewHolder(itemView){

        //views from ingredient_row.xml
        var name: TextView = itemView.textView_name
        var updateBtn: ImageButton = itemView.button_deleteIngredient
    }
}
