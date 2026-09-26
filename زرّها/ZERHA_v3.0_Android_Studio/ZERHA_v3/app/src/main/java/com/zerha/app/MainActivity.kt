package com.zerha.app

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Context.dataStore by preferencesDataStore("zerha_settings")
private val COUNT = intPreferencesKey("count")
private val BEST = intPreferencesKey("best")
private val SOUND = booleanPreferencesKey("sound")
private val VIBRATE = booleanPreferencesKey("vibrate")
private val DARK = booleanPreferencesKey("dark")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZerhaApp(this) }
    }
}

@Composable
fun ZerhaApp(context: Context) {
    val scope = rememberCoroutineScope()
    var count by remember { mutableIntStateOf(0) }
    var best by remember { mutableIntStateOf(0) }
    var sound by remember { mutableStateOf(true) }
    var vibrate by remember { mutableStateOf(true) }
    var dark by remember { mutableStateOf(true) }
    var settings by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("اضغط الزر… وشوف شو بصير 🔥") }

    LaunchedEffect(Unit) {
        val p = context.dataStore.data.first()
        count = p[COUNT] ?: 0
        best = p[BEST] ?: 0
        sound = p[SOUND] ?: true
        vibrate = p[VIBRATE] ?: true
        dark = p[DARK] ?: true
    }

    val messages = listOf(
        "هيك الشغل! 🔥", "كمل… لا توقف 😈", "ضغطة أسطورية! 🚀",
        "زرّها يا بطل! 🔴", "مين رح يكسر الرقم؟ 🏆", "ZERHA! ✨"
    )
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "press")

    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        if (settings) {
            SettingsScreen(
                dark, sound, vibrate,
                onDark = { dark = it; scope.launch { context.dataStore.edit { this[DARK] = it } } },
                onSound = { sound = it; scope.launch { context.dataStore.edit { this[SOUND] = it } } },
                onVibrate = { vibrate = it; scope.launch { context.dataStore.edit { this[VIBRATE] = it } } },
                onReset = { count = 0; best = 0; scope.launch { context.dataStore.edit { this[COUNT] = 0; this[BEST] = 0 } } },
                onBack = { settings = false }
            )
        } else {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFF0B0B0F), Color(0xFF17171D)))
                )
            ) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("زِرّها", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("ZERHA", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B4B),
                            modifier = Modifier.padding(top = 8.dp))
                    }
                    Spacer(Modifier.height(42.dp))
                    Text("$count", fontSize = 64.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("عدد الضغطات", color = Color.LightGray)
                    Spacer(Modifier.height(34.dp))

                    Box(
                        Modifier.size(230.dp).scale(scale).background(
                            Brush.radialGradient(listOf(Color(0xFFFF4B4B), Color(0xFFB91C1C))),
                            CircleShape
                        ).clickable {
                            pressed = true
                            count++
                            if (count > best) best = count
                            message = messages[Random.nextInt(messages.size)]
                            scope.launch { context.dataStore.edit { this[COUNT] = count; this[BEST] = best } }
                            if (vibrate) {
                                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                v.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                            scope.launch {
                                kotlinx.coroutines.delay(90)
                                pressed = false
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("اضغطني", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(28.dp))
                    Text(message, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(22.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White.copy(alpha = .08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏆", fontSize = 22.sp); Text("$best", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("أفضل رقم", color = Color.Gray, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔊", fontSize = 22.sp); Text(if (sound) "ON" else "OFF", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("الصوت", color = Color.Gray, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📳", fontSize = 22.sp); Text(if (vibrate) "ON" else "OFF", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("الاهتزاز", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { settings = true }) {
                        Text("⚙️ الإعدادات", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    dark: Boolean, sound: Boolean, vibrate: Boolean,
    onDark: (Boolean) -> Unit, onSound: (Boolean) -> Unit,
    onVibrate: (Boolean) -> Unit, onReset: () -> Unit, onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(if (dark) Color(0xFF0B0B0F) else Color(0xFFF7F7F7)).padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← رجوع", color = Color.White) }
            Spacer(Modifier.weight(1f))
            Text("الإعدادات", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(30.dp))
        SettingRow("🌙 الوضع الداكن", dark, onDark)
        SettingRow("🔊 صوت الضغط", sound, onSound)
        SettingRow("📳 الاهتزاز", vibrate, onVibrate)
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
            modifier = Modifier.fillMaxWidth()
        ) { Text("🗑️ تصفير العداد وأفضل رقم") }
        Spacer(Modifier.weight(1f))
        Text("زِرّها — ZERHA", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("الإصدار 3.0 • يعمل بدون إنترنت", color = Color.Gray, fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SettingRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
