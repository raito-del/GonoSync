package com.example.gonosync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore

data class AppThemeColors(
    val backgroundColor: Color,
    val primaryColor: Color,
    val cardColor: Color,
    val textColor: Color
)

@Composable
fun rememberLiveTheme(): AppThemeColors {
    val db = FirebaseFirestore.getInstance()

    val currentTheme = remember {
        mutableStateOf(
            AppThemeColors(
                backgroundColor = Color(0xFF0F172A),
                primaryColor = Color(0xFF38BDF8),
                cardColor = Color(0xFF1E293B),
                textColor = Color.White
            )
        )
    }

    DisposableEffect(Unit) {
        val listener = db.collection("settings").document("app_theme")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val mode = snapshot.getString("theme_mode") ?: "dark"
                    currentTheme.value = when (mode) {
                        "light" -> AppThemeColors(
                            backgroundColor = Color(0xFFF8FAFC),
                            primaryColor = Color(0xFF0284C7),
                            cardColor = Color(0xFFFFFFFF),
                            textColor = Color(0xFF0F172A)
                        )
                        "purple" -> AppThemeColors(
                            backgroundColor = Color(0xFF1A0B2E),
                            primaryColor = Color(0xFFA855F7),
                            cardColor = Color(0xFF2D124D),
                            textColor = Color.White
                        )
                        else -> AppThemeColors(
                            backgroundColor = Color(0xFF0F172A),
                            primaryColor = Color(0xFF38BDF8),
                            cardColor = Color(0xFF1E293B),
                            textColor = Color.White
                        )
                    }
                }
            }

        onDispose { listener.remove() }
    }

    return currentTheme.value
}
