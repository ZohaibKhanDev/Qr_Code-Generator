package com.example.qrcode_genrater

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.qrcode_genrater.ui.theme.QrCodeGenraterTheme
import com.lightspark.composeqr.QrCodeView

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
        onClick = { launcher.launch("image/*") },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White
        ),
        shape = ButtonDefaults.shape
    ) {
        Text(text = "Select Image")
    }
}

@Composable
fun ImageQrCode(imageUri: Uri?) {
    if (imageUri != null) {
        val painter = rememberImagePainter(
            data = imageUri.toString(),
            builder = {
                scale(Scale.FILL)
            }
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        SelectionContainer {
            Text(
                text = imageUri.toString(),
                modifier = Modifier
                    .background(Color(0xFFFAFAFA))
                    .padding(8.dp),
                color = Color.Black
            )
        }
    } else {
        Text(
            text = "No image selected",
            color = Color.Gray
        )
    }
}

@Composable
fun GalleryQrCodeApp() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        SelectImageFromGallery { uri ->
            selectedImageUri = uri
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImageUri != null) {
            QrCodeView(
                data = selectedImageUri.toString(), modifier = Modifier
                    .size(300.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .border(BorderStroke(2.dp, Color(0xFF6200EE)), shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        ImageQrCode(imageUri = selectedImageUri)
    }
}
