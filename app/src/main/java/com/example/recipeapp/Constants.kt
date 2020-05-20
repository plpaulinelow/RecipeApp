package com.example.recipeapp

object Constants {
    //db name
    const val DB_NAME = "MY_RECIPE_DB"
    //db version
    const val DB_VERSION = 1
    //table name
    const val RECIPE_TABLE = "RECIPE_TABLE"
    const val INGREDIENTS_TABLE = "INGREDIENTS_TABLE"
    //col and fields of recipe table
    const val C_ID = "ID"
    const val C_NAME = "NAME"
    const val C_IMAGE = "IMAGE"
    const val C_TYPE = "TYPE"
    const val C_STEPS = "STEPS"
    const val C_DATE_ADDED = "DATE_ADDED"
    const val C_DATE_UPDATED = "DATE_UPDATED"
    //col and fields of ingredients table
    const val C_RECIPE_ID = "RECIPE_ID"

    //create recipe table
    const val CREATE_TABLE_RECIPE = (
            "CREATE TABLE " + RECIPE_TABLE + "("
                    + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + C_NAME + " TEXT,"
                    + C_IMAGE + " TEXT,"
                    + C_TYPE + " TEXT,"
                    + C_STEPS + " TEXT,"
                    + C_DATE_ADDED + " TEXT,"
                    + C_DATE_UPDATED + " TEXT"
                    + ")"
            )

    //create ingredients table
    const val CREATE_TABLE_INGREDIENTS = (
            "CREATE TABLE " + INGREDIENTS_TABLE + "("
                    + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + C_RECIPE_ID + " INTEGER,"
                    + C_NAME + " TEXT,"
                    + C_DATE_ADDED + " TEXT,"
                    + C_DATE_UPDATED + " TEXT"
                    + ")"
            )
}