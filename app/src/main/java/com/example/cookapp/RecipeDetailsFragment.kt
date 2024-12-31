package com.example.cookapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookapp.databinding.RecipeDetailsScreenBinding

class RecipeDetailsFragment : Fragment() {
    private var _binding: RecipeDetailsScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecipeDetailsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Recipe Steps
        binding.startCookingButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipeDetails_to_recipeSteps)
        }

        // Navigate to Recipe Ingredients
        binding.ingredientsButton2.setOnClickListener {
            findNavController().navigate(R.id.action_recipeDetails_to_recipeIngredients)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
