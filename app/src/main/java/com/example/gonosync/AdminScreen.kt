package com.example.gonosync // আপনার প্রজেক্টের প্যাকেজ নাম অনুযায়ী এটি অটো থাকবে বা বসিয়ে নিবেন

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

// Backend Function to Assign CR Role in Firebase
fun assignCRToUser(userEmail: String, departmentName: String, onResult: (Boolean, String) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users")
        .whereEqualTo("email", userEmail)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                
                val updates = mapOf(
                    "role" to "cr",
                    "department" to departmentName
                )

                firestore.collection("users").document(documentId)
                    .update(updates)
                    .addOnSuccessListener {
                        onResult(true, "CR assigned successfully!")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed: ${e.message}")
                    }
            } else {
                onResult(false, "User not found with this email!")
            }
        }
        .addOnFailureListener { e ->
            onResult(false, "Error: ${e.message}")
        }
}

// Super Admin Panel Screen UI
@Composable
fun AddCRScreen() {
    var userEmail by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("CSE") }
    var statusMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val departments = listOf("CSE", "EEE", "BBA", "Civil")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Super Admin: Assign CR",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Student Email Field
        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Student's Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Department Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Department: $selectedDept")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                departments.forEach { dept ->
                    DropdownMenuItem(
                        text = { Text(dept) },
                        onClick = {
                            selectedDept = dept
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = {
                if (userEmail.isNotBlank()) {
                    assignCRToUser(userEmail, selectedDept) { success, message ->
                        statusMessage = message
                    }
                } else {
                    statusMessage = "Please enter a valid email."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Give CR Permission")
        }

        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
