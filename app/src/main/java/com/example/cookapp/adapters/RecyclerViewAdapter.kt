package com.example.cookapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookapp.R
import com.example.cookapp.models.Recipe
import com.example.cookapp.utils.GetInstructionArrayFromRecipe

class InstructionsAdapter(
    private val items: Array<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<InstructionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recipeStep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_steps_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text = items[position]

        // Set click listener
        holder.textView.setOnClickListener {
            onClick(items[position]) // Pass the item text to the callback
        }
    }

    override fun getItemCount(): Int = items.size
}

class ItemAdapter(
    private val items: Array<Recipe>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recipeStep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_steps_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[0]

        val instructions: Array<String> = GetInstructionArrayFromRecipe(item)

        holder.textView.text = instructions[position]

        // Set click listener
        holder.textView.setOnClickListener {
            onClick(item.strMeal) // Pass the item text to the callback
        }
    }

    override fun getItemCount(): Int = items.size
}


