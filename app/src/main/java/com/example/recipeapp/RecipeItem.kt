package com.example.recipeapp

import java.io.Serializable

data class RecipeItem (
    var imageResource: Int,
    var name: String,
    var steps: String,
    var type: String,
    var ingredients: ArrayList<String>):Serializable{

}

