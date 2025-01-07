package com.example.cookapp.fragments

import LoadRecipesFromAssets
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookapp.MainActivity
import com.example.cookapp.R
import com.example.cookapp.adapters.InstructionsAdapter
import com.example.cookapp.adapters.ItemAdapter
import com.example.cookapp.databinding.RecipeGuideActivityBinding
import com.example.cookapp.utils.GetInstructionArrayFromRecipe
import java.util.Locale

class RecipeStepsFragment : Fragment() {
    private var _binding: RecipeGuideActivityBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InstructionsAdapter
    private lateinit var tts: TextToSpeech


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecipeGuideActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipes = LoadRecipesFromAssets(this.requireContext())

        val instructions = GetInstructionArrayFromRecipe(recipes[0])


        // Initialize Text-to-Speech
        tts = TextToSpeech(this.context) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US)
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = InstructionsAdapter(instructions) { step ->
            speakOut(step)
        }
        // Navigate to Recipe Details
        binding.ingredientsButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipeStepsFragment_to_recipeIngredientsFragment)
        }
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroyView()
        _binding = null
    }

    private fun speakOut(text: String) {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
