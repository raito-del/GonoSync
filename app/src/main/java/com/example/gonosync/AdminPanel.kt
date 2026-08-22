package com.example.gonosync

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CR / Admin Login") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Admin Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Checking..." else "Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ThemeControlDialog(
    onDismiss: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change App Theme for All") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { updateTheme(db, "dark", context, onDismiss) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Dark Theme (Default)") }

                Button(
                    onClick = { updateTheme(db, "light", context, onDismiss) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Light Theme") }

                Button(
                    onClick = { updateTheme(db, "purple", context, onDismiss) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Purple Aesthetic Theme") }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun updateTheme(db: FirebaseFirestore, themeMode: String, context: android.content.Context, onDismiss: () -> Unit) {
    val data = hashMapOf("theme_mode" to themeMode)
    db.collection("settings").document("app_theme")
        .set(data)
        .addOnSuccessListener {
            Toast.makeText(context, "Theme updated for all users!", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
}
