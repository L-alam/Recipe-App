package com.example.recipeapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipeapp.Recipe
import com.example.recipeapp.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(viewModel: RecipeViewModel = viewModel()) {
    // State to track the currently selected recipe for detail view
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    // Show recipe detail screen if a recipe is selected
    selectedRecipe?.let { recipe ->
        RecipeDetailScreen(
            recipe = recipe,
            onBackClick = { selectedRecipe = null }
        )
        return
    }
    var searchQuery by remember { mutableStateOf("") }
    val recipeState by viewModel.recipeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Recipes") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    if (searchQuery.isNotEmpty()) {
                        viewModel.searchRecipes(searchQuery)
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search button
        Button(
            onClick = {
                if (searchQuery.isNotEmpty()) {
                    viewModel.searchRecipes(searchQuery)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on state
        when (recipeState) {
            RecipeViewModel.RecipeState.Initial -> {
                InitialStateMessage()
            }
            RecipeViewModel.RecipeState.Loading -> {
                LoadingIndicator()
            }
            is RecipeViewModel.RecipeState.Success -> {
                val recipes = (recipeState as RecipeViewModel.RecipeState.Success).recipes
                RecipeList(
                    recipes = recipes,
                    onRecipeClick = { recipe -> selectedRecipe = recipe }
                )
            }
            is RecipeViewModel.RecipeState.Error -> {
                val errorMessage = (recipeState as RecipeViewModel.RecipeState.Error).errorMessage
                ErrorMessage(message = errorMessage)
            }
        }
    }
}

@Composable
fun InitialStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Search for your favorite recipes",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (Recipe) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe) }
            )
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            // Recipe image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Recipe details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.getShortDescription(),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.getShortInstructions(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}