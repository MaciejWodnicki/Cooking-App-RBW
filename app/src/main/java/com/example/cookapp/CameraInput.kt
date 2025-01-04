package com.example.cookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cookapp.databinding.FragmentCameraInputBinding
import com.google.common.util.concurrent.ListenableFuture

class CameraInput : Fragment() {
    private var _binding: FragmentCameraInputBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraInputBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun prepareCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this.requireContext()))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK) //CHANGE TO FRONT SO WE CAN SEE SCREEN
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setResolutionSelector(ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this.requireContext())) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // insert your code here.
            val bitmap = imageProxy.toBitmap().rotate(rotationDegrees.toFloat())
            val grayBitmap = toGrayscale(bitmap)
            binding.CameraInputView.setImageBitmap(grayBitmap)
            // after done, release the ImageProxy object
            imageProxy.close()
        }

        cameraProvider.bindToLifecycle(this.viewLifecycleOwner, cameraSelector, imageAnalysis)
    }
}