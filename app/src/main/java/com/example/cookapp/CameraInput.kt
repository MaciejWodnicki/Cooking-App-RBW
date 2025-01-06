package com.example.cookapp

import android.os.Bundle
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cookapp.databinding.FragmentCameraInputBinding
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.core.ImageProxy

class CameraInput : Fragment() {
    private val nextPageDetectionPercentage = 0.6
    private val nextPageTriggerTime = 3000 // in ms
    private val gestureDetectionInterval = 6 // frames

    private var _binding: FragmentCameraInputBinding? = null

    private var calibrationPixelStdDevValue: Double? = null
    private var lastGestureTime: Long = 0
    private var frameCount: Int = 0

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
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT) //CHANGE TO FRONT SO WE CAN SEE SCREEN
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setResolutionSelector(ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy(
                    Size(640, 480),
                    ResolutionStrategy.FALLBACK_RULE_NONE)).build())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this.requireContext())) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // insert your code here.
            val bitmap = imageProxy.toBitmap().rotate(rotationDegrees.toFloat())
            binding.CameraInputView.setImageBitmap(bitmap)

            if (isGestureDetected(imageProxy)) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Next Page", Toast.LENGTH_SHORT).show()
                }
            }

            // after done, release the ImageProxy object
            imageProxy.close()
        }

        cameraProvider.bindToLifecycle(this.viewLifecycleOwner, cameraSelector, imageAnalysis)
    }

    private fun isGestureDetected(imageProxy: ImageProxy): Boolean {
        if (frameCount < gestureDetectionInterval) {
            frameCount++
            return false
        }
        frameCount = 0

        val buffer = imageProxy.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        val pixelStdDev = calculatePixelStdDev(data)
        if (calibrationPixelStdDevValue == null) {
            calibrationPixelStdDevValue = pixelStdDev
            return false // No gesture on calibration
        }

        val currentTime = System.currentTimeMillis()
        if (pixelStdDev <= calibrationPixelStdDevValue!! * nextPageDetectionPercentage && currentTime - lastGestureTime >= nextPageTriggerTime) {
            lastGestureTime = currentTime
            return true
        }

        return false
    }

    private fun calculatePixelStdDev(data: ByteArray): Double {
        // Calculate mean
        val mean = data.map { it.toInt() and 0xFF }.average()

        // Calculate standard deviation
        val variance = data.map { (it.toInt() and 0xFF) - mean }
            .map { it * it }
            .average()

        return Math.sqrt(variance)
    }
}
