package de.koenidv.ablaufdaten

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import de.koenidv.ablaufdaten.BottomSheets.BottomSheet

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnsafeOptInUsageError")
class ScannerBottomSheet(
    private val context: Context,
) {
    private val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    private val camera = ProcessCameraProvider.getInstance(context)

    @Composable
    fun Scan(onScanned: (String) -> Unit) {
        HandleCameraPermission()
        BottomSheet(sheetState) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                BarcodeScanner(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    context = context,
                    callback = onScanned
                )
            }
        }
    }


    suspend fun show() {
        sheetState.show()
    }

    suspend fun hide() {
        sheetState.hide()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun HandleCameraPermission() {
        val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
        LaunchedEffect(cameraPermissionState) {
            if (cameraPermissionState.status is PermissionStatus.Denied) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    // Camera Composable
    @Composable
    @androidx.camera.core.ExperimentalGetImage
    private fun BarcodeScanner(
        modifier: Modifier,
        context: Context,
        callback: (String) -> Unit
    ) {
        val preview = remember { PreviewView(context) }
        val cameraFuture = remember { ProcessCameraProvider.getInstance(context) }
        val camera = remember(cameraFuture) { cameraFuture.get() }
        val lifecycleOwner = LocalLifecycleOwner.current
        val executor = remember(context) { ContextCompat.getMainExecutor(context) }
        // ProcessCameraProvider#isBound doesn't work, so we use a flag to track if the camera is bound
        val cameraBound = remember { mutableStateOf(false) }

        // Get the scanning client for specified code formats
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A
            ).build()
        val scanner = BarcodeScanning.getClient(options)

        // Analyze each frame using ML Kit
        val frameAnalysis = ImageAnalysis.Builder().build().also {
            it.setAnalyzer(executor) { imageProxy ->
                processImageProxy(scanner, imageProxy) { result ->
                    if (result != null) {
                        camera.unbindAll()
                        cameraBound.value = false
                        callback(result)
                    }
                }
            }
        }

        // Create a preview on the preview view
        val prev = Preview.Builder().build().also {
            it.setSurfaceProvider(preview.surfaceProvider)
        }

        LaunchedEffect(sheetState.currentValue) {
            if (sheetState.isVisible && !cameraBound.value) {
                cameraBound.value = true
                // Bind the preview surface to the camera
                camera.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    prev
                )

                // Bind the barcode analysis to the camera
                camera.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    frameAnalysis
                )
            } else if (!sheetState.isVisible) {
                camera.unbindAll()
                cameraBound.value = false
            }
        }

        AndroidView(
            modifier = modifier,
            factory = {
                preview
            }
        )
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy,
        callback: (String?) -> Unit
    ) {
        // Process the image and callback with the result
        // Close the proxy after completion to allow for the next image
        barcodeScanner.process(
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        )
            .addOnSuccessListener { barcodes -> callback(barcodes.firstOrNull()?.rawValue) }
            .addOnFailureListener { callback(null) }
            .addOnCompleteListener { imageProxy.close() }
    }
}