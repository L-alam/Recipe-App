package com.example.recipeapp

import com.squareup.moshi.Json

// Main response from TheMealDB API
data class RecipeResponse(
    @Json(name = "meals") val meals: List<Recipe>?
)

// Individual recipe model
data class Recipe(
    @Json(name = "idMeal") val id: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strCategory") val category: String,
    @Json(name = "strArea") val area: String,
    @Json(name = "strInstructions") val instructions: String,
    @Json(name = "strMealThumb") val imageUrl: String,
    @Json(name = "strTags") val tags: String?,
    @Json(name = "strYoutube") val youtubeUrl: String?
) {
    // Method to get a shorter description of the recipe
    fun getShortDescription(): String {
        return "Category: $category • Cuisine: $area"
    }

    // Method to get a shortened version of instructions (for preview)
    fun getShortInstructions(): String {
        return if (instructions.length > 100) {
            instructions.substring(0, 100) + "..."
        } else {
            instructions
        }
    }
}