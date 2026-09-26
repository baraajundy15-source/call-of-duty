package com.zerha.app

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Context.dataStore by preferencesDataStore("zerha_settings")

private val COUNT = intPreferencesKey("count")
private val BEST = intPreferencesKey("best")
private val SOUND = booleanPreferencesKey("sound")
private val VIBRATION = booleanPreferencesKey("vibration")

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZerhaApp() }
    }
}

@Composable
fun ZerhaApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var count by remember { mutableIntStateOf(0) }
    var best by remember { mutableIntStateOf(0) }
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var settings by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("اضغط الزر وشوف شو بيصير 👇") }
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val p = context.dataStore.data.first()
        count = p[COUNT] ?: 0
        best = p[BEST] ?: 0
        sound = p[SOUND] ?: true
        vibration = p[VIBRATION] ?: true
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = spring(),
        label = "buttonScale"
    )

    MaterialTheme(colorScheme = darkColorScheme(
        background = Color(0xFF0B0B10),
        surface = Color(0xFF15151D),
        primary = Color(0xFFE53935)
    )) {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF0B0B10)) {
            if (settings) {
                SettingsScreen(
                    sound, vibration,
                    onSound = { value ->
                        sound = value
                        scope.launch { context.dataStore.edit { it[SOUND] = value } }
                    },
                    onVibration = { value ->
                        vibration = value
                        scope.launch { context.dataStore.edit { it[VIBRATION] = value } }
                    },
                    onReset = {
                        count = 0
                        best = 0
                        scope.launch {
                            context.dataStore.edit {
                                it[COUNT] = 0
                                it[BEST] = 0
                            }
                        }
                    },
                    onBack = { settings = false }
                )
            } else {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(20.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { settings = true }) {
                                Text("⚙️ الإعدادات", color = Color.White)
                            }
                        }
                        Text("زِرّها", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("ZERHA", fontSize = 14.sp, letterSpacing = 5.sp, color = Color(0xFFE53935))
                        Spacer(Modifier.height(24.dp))
                        Text("عدد الضغطات", color = Color.LightGray, fontSize = 15.sp)
                        Text(count.toString(), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Text("🏆 أفضل رقم: $best", color = Color(0xFFFFD54F), fontSize = 15.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                count++
                                if (count > best) best = count
                                message = messages[Random.nextInt(messages.size)]
                                pressed = true
                                if (sound) ToneGenerator(AudioManager.STREAM_MUSIC, 80).startTone(ToneGenerator.TONE_PROP_BEEP, 60)
                                if (vibration) {
                                    val vibrator = if (android.os.Build.VERSION.SDK_INT >= 31) {
                                        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
                                    } else context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                                }
                                scope.launch {
                                    context.dataStore.edit {
                                        it[COUNT] = count
                                        it[BEST] = best
                                    }
                                }
                            },
                            Modifier.size(210.dp).scale(scale),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 14.dp, pressedElevation = 4.dp)
                        ) {
                            Text("اضغطني", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        LaunchedEffect(pressed) {
                            if (pressed) {
                                kotlinx.coroutines.delay(120)
                                pressed = false
                            }
                        }

                        Spacer(Modifier.height(28.dp))
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF15151D))
                        ) {
                            Text(message, Modifier.fillMaxWidth().padding(22.dp), textAlign = TextAlign.Center, fontSize = 17.sp)
                        }
                    }

                    Text("يعمل بدون إنترنت • إصدار 2.0", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    sound: Boolean,
    vibration: Boolean,
    onSound: (Boolean) -> Unit,
    onVibration: (Boolean) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← رجوع", color = Color.White) }
        Spacer(Modifier.height(20.dp))
        Text("الإعدادات", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("زِرّها — ZERHA", color = Color(0xFFE53935), fontSize = 16.sp)
        Spacer(Modifier.height(28.dp))

        SettingSwitch("🔊 أصوات الضغط", sound, onSound)
        SettingSwitch("📳 الاهتزاز", vibration, onVibration)

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(16.dp)
        ) { Text("🗑️ تصفير العداد وأفضل رقم") }

        Spacer(Modifier.height(24.dp))
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF15151D)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                "الإصدار 2.0\n\nالتطبيق يعمل بدون إنترنت ولا يحتاج إلى تسجيل دخول.",
                Modifier.padding(20.dp),
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun SettingSwitch(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.White, fontSize = 17.sp)
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
