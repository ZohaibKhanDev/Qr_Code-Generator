package com.example.qrcode_genrater

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PrivacyPolicy(navController: NavController) {
    val verticalScroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(verticalScroll)
    ) {

        val titleStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )


        val bodyStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )


        Text(
            text = "Privacy Policy",
            style = titleStyle,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Text(
            text = "1. Introduction\n\n" +
                    "Welcome to the QR Code Generator app (\"App\"), developed by Zohaib Khan (\"Developer\"). This Privacy Policy outlines how we collect, use, disclose, and protect your personal information when you use our App. By using the App, you agree to the collection and use of information in accordance with this policy.\n\n",
            style = titleStyle
        )

        Text(
            text = "2. Information We Collect\n\n" +
                    "Personal Information: We do not collect any personal information that identifies you directly. We only collect the data you provide voluntarily within the App, such as the information you enter to generate QR codes (e.g., name, age, bio).\n\n" +
                    "Usage Data: We may collect information about how you use the App, including your interaction with the QR code generation features, crash reports, and other usage statistics. This information is collected to improve the App’s performance and user experience.\n\n",
            style = bodyStyle
        )

        Text(
            text = "3. How We Use Your Information\n\n" +
                    "To Provide and Maintain the App: We use the data you provide to generate QR codes based on your input and to deliver the functionalities of the App.\n\n" +
                    "To Improve the App: Usage data helps us understand how users interact with the App, which allows us to make improvements and enhance the App’s performance and features.\n\n" +
                    "To Communicate with You: We may use your email or contact information, if provided, to respond to your inquiries, provide support, or notify you of updates related to the App.\n\n",
            style = bodyStyle
        )

        Text(
            text = "4. Data Sharing and Disclosure\n\n" +
                    "Third-Party Services: We do not share your personal information with third parties. The App does not integrate with third-party services that collect personal information.\n\n" +
                    "Legal Requirements: We may disclose your information if required by law or to protect our rights, privacy, safety, or property, or that of others.\n\n",
            style = bodyStyle
        )

        Text(
            text = "5. Data Security\n\n" +
                    "We take reasonable measures to protect your information from unauthorized access, alteration, disclosure, or destruction. However, please be aware that no method of transmission over the internet or method of electronic storage is 100% secure.\n\n",
            style = bodyStyle
        )

        Text(
            text = "6. Data Retention\n\n" +
                    "We retain the data you provide for as long as necessary to fulfill the purposes outlined in this Privacy Policy. We do not retain any personal data beyond what is required for generating QR codes.\n\n",
            style = bodyStyle
        )

        Text(
            text = "7. Your Choices\n\n" +
                    "Access and Update: You can access and update the data you have provided in the App. If you need assistance with this, please contact us using the information provided below.\n\n" +
                    "Opt-Out: You may choose not to provide certain information or to delete the information stored within the App. However, this may affect your ability to use certain features of the App.\n\n",
            style = bodyStyle
        )

        Text(
            text = "8. Children's Privacy\n\n" +
                    "The App is not intended for use by children under the age of 13. We do not knowingly collect personal information from children under 13. If we become aware that we have collected personal information from a child under 13, we will take steps to delete such information.\n\n",
            style = bodyStyle
        )

        Text(
            text = "9. Changes to This Privacy Policy\n\n" +
                    "We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page. You are advised to review this Privacy Policy periodically.\n\n",
            style = bodyStyle
        )
    }
}
