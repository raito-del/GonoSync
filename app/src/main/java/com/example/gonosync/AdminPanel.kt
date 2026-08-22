package com.example.gonosync

import android.content.Context
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
fun CustomAdminLoginDialog(
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
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var showAddAdminDialog by remember { mutableStateOf(false) }

    if (showAddAdminDialog) {
        AddNewAdminDialog(onDismiss = { showAddAdminDialog = false })
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Admin Panel & Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Theme Controls", style = MaterialTheme.typography.titleMedium)
                    
                    Button(
                        onClick = { updateTheme(db, "dark", null, context, onDismiss) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Dark Theme (Default)") }

                    Button(
                        onClick = { updateTheme(db, "purple", null, context, onDismiss) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Purple Aesthetic Theme") }

                    Button(
                        onClick = { updateTheme(db, "light", null, context, onDismiss) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Light Theme") }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("CR Management", style = MaterialTheme.typography.titleMedium)

                    Button(
                        onClick = { showAddAdminDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("➕ Add New CR / Admin") }

                    OutlinedButton(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Logout Admin") }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        )
    }
}

@Composable
fun AddNewAdminDialog(
    onDismiss: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Admin Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("New CR Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Set Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newEmail.isNotEmpty() && newPassword.isNotEmpty()) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(newEmail, newPassword)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "New CR Account Created Successfully!", Toast.LENGTH_LONG).show()
                                onDismiss()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Creating..." else "Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Back") }
        }
    )
}

private fun updateTheme(
    db: FirebaseFirestore, 
    themeMode: String, 
    imageUrl: String?, 
    context: Context, 
    onDismiss: () -> Unit
) {
    val data = hashMapOf(
        "theme_mode" to themeMode,
        "bg_image_url" to (imageUrl ?: "")
    )
    db.collection("settings").document("app_theme")
        .set(data)
        .addOnSuccessListener {
            Toast.makeText(context, "Theme updated for all users!", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
}
