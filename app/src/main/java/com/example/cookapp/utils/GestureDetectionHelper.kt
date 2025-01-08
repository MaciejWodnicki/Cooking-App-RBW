package com.example.cookapp.utils

import android.content.Context
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture

class GestureDetectionHelper(
    private val context: Context,
    private val onGestureDetected: () -> Unit
) {
    private var calibrationPixelStdDevValue: Double? = null
    private var lastGestureTime: Long = 0
    private val nextPageDetectionPercentage = 0.6
    private val nextPageTriggerTime = 3000 // in ms
    private val gestureDetectionInterval = 6 // frames
    private var frameCount = 0

    fun prepareCamera(): ListenableFuture<ProcessCameraProvider> {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            runCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(context))

        return cameraProviderFuture
    }

    private fun runCamera(cameraProvider: ProcessCameraProvider) {
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(
                    Size(640, 480),
                    ResolutionStrategy.FALLBACK_RULE_NONE)
                ).build())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            if (isGestureDetected(imageProxy)) {
                onGestureDetected()
            }
            imageProxy.close()
        }

        cameraProvider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, cameraSelector, imageAnalysis)
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
        if (pixelStdDev <= calibrationPixelStdDevValue!! * nextPageDetectionPercentage &&
            currentTime - lastGestureTime >= nextPageTriggerTime) {
            lastGestureTime = currentTime
            return true
        }

        return false
    }

    private fun calculatePixelStdDev(data: ByteArray): Double {
        val mean = data.map { it.toInt() and 0xFF }.average()
        val variance = data.map { (it.toInt() and 0xFF) - mean }
            .map { it * it }
            .average()
        return Math.sqrt(variance)
    }
}
