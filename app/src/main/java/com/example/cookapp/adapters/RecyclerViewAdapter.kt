package com.example.cookapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookapp.R
import com.example.cookapp.models.Recipe
import com.example.cookapp.utils.GetInstructionArrayFromRecipe

class InstructionsAdapter(
    private val items: Array<String>,
    private val isSpecial: (Int) -> Boolean, // Determines if the item at a given position is special
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_REGULAR = 1
        private const val VIEW_TYPE_SPECIAL = 2
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.recipeStep)
    }

    // ViewHolder for the special item layout
    class SpecialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.currentStepText)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSpecial(position)) VIEW_TYPE_SPECIAL else VIEW_TYPE_REGULAR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SPECIAL -> {
                val view = inflater.inflate(R.layout.recipe_current_step, parent, false)
                SpecialViewHolder(view)
            }

            else -> {
                val view = inflater.inflate(R.layout.recipe_steps_item, parent, false)
                ViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ViewHolder -> {
                holder.textView.text = item
                holder.itemView.setOnClickListener { onClick(item) }
            }
            is SpecialViewHolder -> {
                holder.textView.text = item
                holder.itemView.setOnClickListener { onClick(item) }
            }
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


