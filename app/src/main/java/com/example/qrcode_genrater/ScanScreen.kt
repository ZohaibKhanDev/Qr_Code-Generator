package com.example.qrcode_genrater

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var scannedText1 = "Scan a QR Code"


    val cameraPermission = Manifest.permission.CAMERA
    val permissionGranted = ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(cameraPermission),
            100
        )
    }


    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(
            modifier = Modifier
                .requiredWidth(300.dp)
                .fillMaxHeight()
        ) {
            Text(
                "Scanner",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            Divider()
            NavigationDrawerItem(label = {
                Text(
                    text = "Create Image Qr",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
                selected = false,
                onClick = {
                    navController.navigate(Screens.Home.route)
                }, icon = {
                    Icon(imageVector = Icons.Default.ImageSearch, contentDescription = "")
                }
            )

            Divider()
            NavigationDrawerItem(label = {
                Text(
                    text = "Scan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
                selected = false,
                onClick = {
                    navController.navigate(Screens.Scan.route)
                }, icon = {
                    Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "")
                }
            )
            Divider()
            NavigationDrawerItem(label = {
                Text(
                    text = "Create Qr",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
                selected = false,
                onClick = {
                    navController.navigate(Screens.Create_Qr.route)
                }, icon = {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = "")
                }
            )

            Divider()
            NavigationDrawerItem(label = {
                Text(
                    text = "Privacy Policy",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
                selected = false,
                onClick = {
                    navController.navigate(Screens.Privacy_Policy.route)
                }, icon = {
                    Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = "")
                }
            )

            Divider()
            NavigationDrawerItem(label = {
                Text(
                    text = "Setting",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
                selected = false,
                onClick = { /*TODO*/ }, icon = {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                }
            )
        }
    }

    ) {


        Box(modifier = Modifier.fillMaxSize()) {
            if (permissionGranted){
                CameraPreview(onQrCodeScanned = { qrCode ->
                    scannedText1 = qrCode ?: "No QR Code detected"
                })


                Text(
                    text = scannedText1,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            } else {
                Text("Camera permission is required to use this feature.")
            }

        }

    }
}


@Composable
fun CameraPreview(onQrCodeScanned: (String?) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraExecutor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val barcodeScanner = BarcodeScanning.getClient()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also { analysisUseCase ->
                        analysisUseCase.setAnalyzer(cameraExecutor, { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy, onQrCodeScanned)
                        })
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onQrCodeScanned: (String?) -> Unit
) {
    val mediaImage = imageProxy.image ?: return
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                barcode.rawValue?.let { qrCode ->
                    onQrCodeScanned(qrCode)
                }
            }
        }
        .addOnFailureListener {
            Log.e("CameraPreview", "Barcode scanning failed", it)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}