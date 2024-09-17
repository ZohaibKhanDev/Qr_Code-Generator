package com.example.qrcode_genrater

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateQr(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showTextFields by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
                NavigationDrawerItem(
                    label = { Text("Create Image QR", fontSize = 17.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate(Screens.Home.route) },
                    icon = { Icon(imageVector = Icons.Default.ImageSearch, contentDescription = "") }
                )

                Divider()
                NavigationDrawerItem(
                    label = { Text("Scan", fontSize = 17.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate(Screens.Scan.route) },
                    icon = { Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "") }
                )

                Divider()
                NavigationDrawerItem(
                    label = { Text("Create QR", fontSize = 17.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate(Screens.Create_Qr.route) },
                    icon = { Icon(imageVector = Icons.Default.QrCode, contentDescription = "") }
                )

                Divider()
                NavigationDrawerItem(
                    label = { Text("Privacy Policy", fontSize = 17.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate(Screens.Privacy_Policy.route) },
                    icon = { Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = "") }
                )

                Divider()
                NavigationDrawerItem(
                    label = { Text("Setting", fontSize = 17.sp, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "") }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Create QR Code",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                    .background(Color(0xFFF5F5F5)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (showTextFields) {

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        onClick = {
                            val qrData = "Name: $name, Age: $age, Bio: $bio"
                            generatedBitmap = generateQrCode(qrData)
                            showTextFields = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Create QR Code")
                    }
                } else {
                    generatedBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier
                                .size(300.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    generatedBitmap?.let { saveQRCodeToGallery(context, it) }
                                },
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    generatedBitmap?.let { shareQRCode(context, it) }
                                },
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Share")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showTextFields = true
                            generatedBitmap = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Image",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Create Another QR Code")
                    }
                }
            }
        }
    }
}



private fun generateQrCode(data: String): Bitmap {
    val size = 512
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x, y, if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
            )
        }
    }

    return bitmap
}
