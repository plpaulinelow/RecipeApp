package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.ColorSpace
import java.util.function.LongPredicate

class MyDbHelper (context: Context?):SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Constants.CREATE_TABLE_RECIPE)
        db.execSQL(Constants.CREATE_TABLE_INGREDIENTS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.RECIPE_TABLE)
        onCreate(db)
    }

    //insert record to db
    fun insertRecord(
        name:String?,
        image:String?,
        type:String?,
        steps:String?,
        dateAdded:String?,
        dateUpdated:String?
    ):Long {
        val db = this.writableDatabase
        val values = ContentValues()
        //insert
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_STEPS, steps)
        values.put(Constants.C_DATE_ADDED, dateAdded)
        values.put(Constants.C_DATE_UPDATED, dateUpdated)

        //return id when insert row
        val id = db.insert(Constants.RECIPE_TABLE, null, values)
        db.close()
        return id
    }

    //insert ingredient to db
    fun insertIngredient(
        item:ModelIngredient,
        recipeId: Long
    ):Long {
        val db = this.writableDatabase
        val values = ContentValues()
        //insert
        values.put(Constants.C_RECIPE_ID,recipeId)
        values.put(Constants.C_NAME, item.name)
        values.put(Constants.C_DATE_ADDED, item.dateAdded)
        values.put(Constants.C_DATE_UPDATED, item.dateUpdated)

        //return id when insert row
        val id = db.insert(Constants.INGREDIENTS_TABLE, null, values)
        db.close()
        return id
    }

    //insert ingredients to db
    fun insertIngredients(
        inglist:ArrayList<ModelIngredient>,
        recipeId: Long
    )   {
        for(item: ModelIngredient in inglist){
            var id=insertIngredient(item,recipeId)
        }
    }

    //update record in db
    fun updateRecord(
        id:String,
        name:String?,
        image:String?,
        type:String?,
        steps: String?,
        dateAdded: String?,
        dateUpdated: String?
    ):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Constants.C_NAME, name)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_STEPS, steps)
        values.put(Constants.C_DATE_ADDED, dateAdded)
        values.put(Constants.C_DATE_UPDATED, dateUpdated)

        return db.update(Constants.RECIPE_TABLE,
            values,
            "${Constants.C_ID}=?",
            arrayOf(id)).toLong()
    }

    //get all data
    fun getAllRecipes(orderBy:String):ArrayList<ModelRecipe>{
        val recipeList = ArrayList<ModelRecipe>()
        val selectQuery = "SELECT * FROM ${Constants.RECIPE_TABLE} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToFirst()){
            do{
                val modelRecipe = ModelRecipe(
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_STEPS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_ADDED)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_UPDATED))
                )
                //add recipe to list
                recipeList.add(modelRecipe)
            }while (cursor.moveToNext())
        }
        db.close()
        return recipeList
    }

    //get recipe by type
    fun getRecipesByType(filter:String):ArrayList<ModelRecipe>{
        val recipeList = ArrayList<ModelRecipe>()
        val selectQuery = "SELECT * FROM ${Constants.RECIPE_TABLE} WHERE ${Constants.C_TYPE} = '$filter'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToFirst()){
            do{
                val modelRecipe = ModelRecipe(
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_STEPS)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_ADDED)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_UPDATED))
                )
                //add recipe to list
                recipeList.add(modelRecipe)
            }while (cursor.moveToNext())
        }
        db.close()
        return recipeList
    }

    //get ingredients by recipe id
    fun getIngredientsByRecipeId(recipeId:String):ArrayList<ModelIngredient>{
        val ingList = ArrayList<ModelIngredient>()
        val selectQuery = "SELECT * FROM ${Constants.INGREDIENTS_TABLE} WHERE ${Constants.C_RECIPE_ID} = '$recipeId'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if(cursor.moveToFirst()){
            do{
                val modelIngredient = ModelIngredient(
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    ""+cursor.getInt(cursor.getColumnIndex(Constants.C_RECIPE_ID)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_ADDED)),
                    ""+cursor.getString(cursor.getColumnIndex(Constants.C_DATE_UPDATED))
                )
                //add recipe to list
                ingList.add(modelIngredient)
            }while (cursor.moveToNext())
        }
        db.close()
        return ingList
    }

    //delete single record
    fun deleteRecord(id:String){
        val db = writableDatabase
        db.delete(
            Constants.RECIPE_TABLE,
            "${Constants.C_ID} = ?",
            arrayOf(id)
        )
        db.close()
    }

    //delete single ingredient
    fun deleteIngredient(id:String){
        val db = writableDatabase
        db.delete(
            Constants.INGREDIENTS_TABLE,
            "${Constants.C_ID} = ?",
            arrayOf(id)
        )
        db.close()
    }

    //delete ingredient by recipe id
    fun deleteIngredientByRecipeId(id:String){
        val db = writableDatabase
        db.delete(
            Constants.INGREDIENTS_TABLE,
            "${Constants.C_RECIPE_ID} = ?",
            arrayOf(id)
        )
        db.close()
    }

}