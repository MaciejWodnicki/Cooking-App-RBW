import android.content.Context
import com.example.cookapp.models.Recipe
import kotlinx.serialization.json.Json

fun LoadRecipesFromAssets(context: Context): List<Recipe> {
    // Read the JSON file as a string
    val json = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
    val jsonLight = Json{
        //ignoreUnknownKeys = true
    }

    // Parse JSON string into a List of Recipe objects
    return jsonLight.decodeFromString(json)
}