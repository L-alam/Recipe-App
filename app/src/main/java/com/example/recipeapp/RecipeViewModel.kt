package com.example.recipeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeState>(RecipeState.Initial)
    val recipeState: StateFlow<RecipeState> = _recipeState

    // Function to search for recipes
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipeState.value = RecipeState.Loading
            try {
                val recipeResponse = ApiClient.apiService.searchRecipes(query)

                // Check if we received valid recipes
                if (recipeResponse.meals != null) {
                    _recipeState.value = RecipeState.Success(recipeResponse.meals)
                } else {
                    _recipeState.value = RecipeState.Error("No recipes found. Try another search term.")
                }
            } catch (e: Exception) {
                _recipeState.value = RecipeState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Sealed class to handle different states of the recipe search
    sealed class RecipeState {
        object Initial : RecipeState()
        object Loading : RecipeState()
        data class Success(val recipes: List<Recipe>) : RecipeState()
        data class Error(val errorMessage: String) : RecipeState()
    }
}