package com.clickaday

import android.app.Activity
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner


class CameraTools {
    companion object {
        /**
         * Return the camera
         * @author Mathieu Castera
         */
        fun returnCamera(lensFacing: CameraSelector): CameraSelector {
            var newLensFacing = lensFacing
            if (lensFacing == DEFAULT_FRONT_CAMERA)
                newLensFacing = DEFAULT_BACK_CAMERA
            else if (lensFacing == DEFAULT_BACK_CAMERA)
                newLensFacing = DEFAULT_FRONT_CAMERA

            return newLensFacing
        }

        /**
         * Start the camera
         * @author Internet
         */
        fun startCamera(
            act: Activity,
            cameraSelector: CameraSelector,
            viewFinder: PreviewView,
            lifecycleOwner: LifecycleOwner
        ): ImageCapture {

            var imageCapture: ImageCapture? = null
            imageCapture = ImageCapture.Builder().build()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(act)

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e(PictureActivity.toString(), "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(act))

            return imageCapture
        }

        fun takePhoto(
            act: Activity,
            imageCapture: ImageCapture,
            outputOptions: ImageCapture.OutputFileOptions,
            callback: (Boolean) -> Unit
        ) {
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(act),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        callback(false)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        callback(true)
                    }
                }
            )
        }


    }
}