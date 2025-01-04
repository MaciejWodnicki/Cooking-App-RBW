package com.example.cookapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookapp.R
import com.example.cookapp.databinding.RecipeGuideActivityBinding

class RecipeStepsFragment : Fragment() {
    private var _binding: RecipeGuideActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecipeGuideActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Recipe Details
        binding.ingredientsButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipeStepsFragment_to_recipeIngredientsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
