package com.zerha.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZerhaApp() }
    }
}

private val messages = listOf(
    "😂 مبروك! ضيّعت ضغطة من حياتك.",
    "🧠 معلومة: الأخطبوط لديه ثلاثة قلوب.",
    "🎯 تحدي: اضغط 20 مرة بدون توقف!",
    "😈 كان لازم ما تضغط… هلق تورطت 😂",
    "🍕 سؤال مهم: بيتزا أم برغر؟",
    "🎲 الحظ يقول: جرّب ضغطة ثانية!",
    "🤔 هل كنت تتوقع شيئًا مختلفًا؟",
    "🚀 ضغطة أخرى ونصل إلى المليون!",
    "😎 أنت رسميًا من جماعة زرّها.",
    "✨ أحيانًا أفضل قرار هو… الضغط."
)

@Composable
fun ZerhaApp() {
    var count by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("اضغط الزر وشوف شو بيصير 👇") }
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = spring(),
        label = "buttonScale"
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0B0B10),
            surface = Color(0xFF15151D),
            primary = Color(0xFFE53935)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0B0B10)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "زِرّها",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        "ZERHA",
                        fontSize = 14.sp,
                        letterSpacing = 5.sp,
                        color = Color(0xFFE53935)
                    )
                    Spacer(Modifier.height(28.dp))
                    Text(
                        "عدد الضغطات",
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )
                    Text(
                        count.toString(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            count++
                            message = messages[Random.nextInt(messages.size)]
                            pressed = true
                        },
                        modifier = Modifier
                            .size(210.dp)
                            .scale(scale),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 14.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            "اضغطني",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    LaunchedEffect(pressed) {
                        if (pressed) {
                            kotlinx.coroutines.delay(120)
                            pressed = false
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF15151D)
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }

                Text(
                    "يعمل بدون إنترنت • إصدار 1.0",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
