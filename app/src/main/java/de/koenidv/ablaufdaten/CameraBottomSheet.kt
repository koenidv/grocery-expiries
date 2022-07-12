package de.koenidv.ablaufdaten

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
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

@Composable
@androidx.camera.core.ExperimentalGetImage
fun CameraBottomSheet(context: Context, onScanned: (String) -> Unit) {

    HandleCameraPermission()
    Box(
        modifier = Modifier
            .height(500.dp)
            .fillMaxWidth()
            .background(Color.White)
    ) {
        BarcodeScanner(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            context = context,
            callback = onScanned
        )
    }
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
fun BarcodeScanner(
    modifier: Modifier,
    context: Context,
    callback: (String) -> Unit
) {
    val preview = remember { PreviewView(context) }
    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }
    val cameraProvider = remember(cameraProviderFuture) { cameraProviderFuture.get() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }

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
                    cameraProvider.unbindAll()
                    callback(result)
                }
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            cameraProviderFuture.addListener(
                {
                    cameraProvider.unbindAll()

                    // Create a preview on the preview view
                    val prev = Preview.Builder().build().also {
                        it.setSurfaceProvider(preview.surfaceProvider)
                    }

                    // Bind the preview surface to the camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        prev
                    )

                    // Bind the barcode analysis to the camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        frameAnalysis
                    )
                },
                executor
            )
            preview
        }
    )
}

@androidx.camera.core.ExperimentalGetImage
fun processImageProxy(
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