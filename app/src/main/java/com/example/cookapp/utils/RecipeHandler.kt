package com.example.cookapp.utils

import com.example.cookapp.models.Recipe

fun GetInstructionArrayFromRecipe(recipe:Recipe): Array<String> {
    // Read the JSON file as a string
    val arr = recipe.strInstructions.split(Regex("(?<=\\.)\\s*|\r\n|\n"))  // This regex splits by period + space or newlines
        .map { it.trim() }  // Trim any leading/trailing spaces or newlines
        .filter { it.isNotEmpty() }
        .toTypedArray()

    // Parse JSON string into a List of Recipe objects
    return arr
}