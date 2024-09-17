package com.example.qrcode_genrater

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var scannedText1 by remember { mutableStateOf("Scan a QR Code") }
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionGranted = isGranted
            if (isGranted) {
                Log.d("ScanScreen", "Camera permission granted")
            } else {
                Log.d("ScanScreen", "Camera permission denied")
            }
        }
    )


    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        }
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
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(
                    text = "Create",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }, actions = {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "",
                    tint = Color.Black
                )
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())) {
                if (permissionGranted) {
                    CameraPreview(onQrCodeScanned = { qrCode ->
                        scannedText1 = qrCode ?: "No QR Code detected"
                    })

                    Text(
                        text = scannedText1,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(8.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Camera permission is required to use this feature.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
                try {
                    val cameraProvider = cameraProviderFuture.get()


                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }


                    val options = BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                    val barcodeScanner = BarcodeScanning.getClient(options)


                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysisUseCase ->
                            analysisUseCase.setAnalyzer(cameraExecutor, { imageProxy ->
                                processImageProxy(barcodeScanner, imageProxy, onQrCodeScanned)
                            })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

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
    val mediaImage = imageProxy.image ?: run {
        Log.w("CameraPreview", "No image available")
        imageProxy.close()
        return
    }
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                Log.d("CameraPreview", "Barcodes detected: ${barcodes.size}")
                barcodes.forEach { barcode ->
                    barcode.rawValue?.let { qrCode ->
                        Log.d("CameraPreview", "QR Code detected: $qrCode")
                        onQrCodeScanned(qrCode)
                    }
                }
            } else {
                Log.d("CameraPreview", "No barcodes detected")
            }
        }
        .addOnFailureListener { e ->
            Log.e("CameraPreview", "Barcode scanning failed", e)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
