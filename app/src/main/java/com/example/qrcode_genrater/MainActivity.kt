package com.example.qrcode_genrater

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BlurLinear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.qrcode_genrater.ui.theme.QrCodeGenraterTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.lightspark.composeqr.QrCodeView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrCodeGenraterTheme {
                GalleryQrCodeApp()
            }
        }
    }
}

@Composable
fun SelectImageFromGallery(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onImageSelected(it) } }

    Button(
        onClick = { launcher.launch("image/*") }, colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE), contentColor = Color.White
        ), shape = RoundedCornerShape(1.dp), modifier = Modifier.fillMaxSize()
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Select Image")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Select Image")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GalleryQrCodeApp() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val density = LocalDensity.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Create",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }, navigationIcon = {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "", tint = Color.White)
        }, actions = {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "",
                tint = Color.White
            )
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue)
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scroll)
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (selectedImageUri == null) {
                SelectImageFromGallery { uri ->
                    selectedImageUri = uri
                }
            } else {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                        .height(54.dp)
                        .background(Color.LightGray.copy(0.20f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "")
                    Spacer(modifier = Modifier.width(9.dp))

                    Text(text = "My QR", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.BlurLinear, contentDescription = "")
                    Spacer(modifier = Modifier.width(9.dp))
                    Icon(imageVector = Icons.Outlined.Star, contentDescription = "")

                }

                Spacer(modifier = Modifier.height(97.dp))

                val qrCodeModifier = Modifier
                    .size(300.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        qrCodeBitmap = generateQrCodeBitmap(layoutCoordinates.size, selectedImageUri.toString(), density)
                    }

                Spacer(modifier = Modifier.height(16.dp))

                QrCodeView(
                    data = selectedImageUri.toString(),
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .height(55.dp)
                        .background(Color.LightGray.copy(alpha = 0.20f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(22.dp))

                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                qrCodeBitmap?.let { it1 -> shareQrCode(context, it1) }
                            }
                    )
                }


                Spacer(modifier = Modifier.height(22.dp))

                SelectionContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)
                            )
                            .padding(start = 16.dp, end = 16.dp)
                            .background(Color(0xFFFAFAFA))
                    ) {
                        Text(
                            text = "Image URL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedImageUri.toString(),
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = { selectedImageUri = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, contentColor = Color.White
                    ),
                    modifier = Modifier.size(180.dp, 50.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Image",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Remove Image")
                }
            }
        }
    }
}

fun generateQrCodeBitmap(size: IntSize, data: String, density: Density): Bitmap {
    return Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
}

fun saveQrCodeToGallery(context: Context, bitmap: Bitmap) {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "qr_code_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QrCodes")
    }
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        val outputStream: OutputStream? = contentResolver.openOutputStream(it)
        outputStream.use { stream ->
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            Toast.makeText(context, "QR Code saved", Toast.LENGTH_SHORT).show()
        }
    }
}

fun shareQrCode(context: Context, bitmap: Bitmap) {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "qr_code.png")
    val fileOutputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    fileOutputStream.close()

    val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share QR Code"))
}

/*
fun generateQrCodeBitmap(size: IntSize, data: String): Bitmap {
    val writer = QRCodeWriter()
    return try {
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size.width, size.height)
        val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until size.width) {
            for (y in 0 until size.height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.Black else Color.White)
            }
        }
        bitmap
    } catch (e: WriterException) {
        Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    }
}*/
