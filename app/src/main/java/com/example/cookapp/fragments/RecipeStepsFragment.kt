package com.example.cookapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookapp.R
import com.example.cookapp.adapters.InstructionsAdapter
import com.example.cookapp.databinding.RecipeGuideActivityBinding
import com.example.cookapp.utils.GetInstructionArrayFromRecipe
import com.example.cookapp.utils.LoadRecipesFromAssets
import java.util.Locale

class RecipeStepsFragment : Fragment() {
    private var _binding: RecipeGuideActivityBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InstructionsAdapter
    private lateinit var tts: TextToSpeech

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener
    private var speechRecognitionEnabled: Boolean = false

    var currentStep: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecipeGuideActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Text-to-Speech
        tts = TextToSpeech(this.context) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US)
            }
        }
        setupRecycler()

        val handler = Handler(Looper.getMainLooper())
        val continuousTask = object : Runnable {
            override fun run() {
                startSpeechRecognition() // Call your function
                handler.postDelayed(this, 1000) // Repeat every 1000ms (1 second)
            }
        }
        setupSpeechRecognition()
        binding.toggleSpeechRecognition.setOnClickListener{
            speechRecognitionEnabled = !speechRecognitionEnabled
            if(speechRecognitionEnabled){
                handler.post(continuousTask)
            }
            else{
                handler.removeCallbacks(continuousTask)
            }

        }




        // Navigate to Recipe Ingredients
        binding.ingredientsButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipeStepsFragment_to_recipeIngredientsFragment)
        }
        // advance steps
        binding.nextStepButton.setOnClickListener {
            goToNextStep()
            }

    }

    private fun setupRecycler(){
        val recipes = LoadRecipesFromAssets(this.requireContext())

        val instructions = GetInstructionArrayFromRecipe(recipes[0])

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = InstructionsAdapter(instructions,
            isSpecial = { position -> position == currentStep }) { step ->
            speakOut(step)
        }
        recyclerView.adapter = adapter
    }

    private fun speakOut(text: String) {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun goToNextStep(){
        currentStep++
        adapter.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(currentStep+6)

        //read out the new step
        recyclerView.post {
            recyclerView.findViewHolderForAdapterPosition(currentStep)?.itemView?.performClick()
        }
    }

    fun goToPreviousStep(){
        currentStep--
        adapter.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(currentStep+6)

        //read out the new step
        recyclerView.post {
            recyclerView.findViewHolderForAdapterPosition(currentStep)?.itemView?.performClick()
        }
    }

    private fun setupSpeechRecognition(){
        // Initialize SpeechRecognizer and RecognitionListener

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context)

        recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
                //Toast.makeText(context, "Error occurred: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                // Get the recognized speech
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (matches != null && matches.isNotEmpty()) {
                    val recognizedText = matches[0].lowercase(Locale.getDefault())

                    // Handle navigation based on recognized speech
                    when {
                        recognizedText.contains("next") -> {
                            goToNextStep()
                        }
                        recognizedText.contains("previous") -> {
                            goToPreviousStep()
                        }

                        else -> {
                            Toast.makeText(context, "No command recognized", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(eventType: Int, p0: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    // Start speech recognition when button is clicked
    fun startSpeechRecognition() {
        if(!speechRecognitionEnabled){
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

        // Start listening for speech input
        speechRecognizer.startListening(intent)
    }

    override fun onDestroyView() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroyView()
        _binding = null
    }
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()  // Always clean up the SpeechRecognizer
    }
}
