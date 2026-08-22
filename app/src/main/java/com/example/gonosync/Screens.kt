package com.example.gonosync

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// --- Routine Screen ---
@Composable
fun RoutineScreen(
    themeColors: AppThemeColors,
    selectedDay: String,
    onDaySelected: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val days = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu")
    var routineList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showAddRoutineDialog by remember { mutableStateOf(false) }

    val isAdmin = auth.currentUser != null

    // Load Routine Data based on Selected Day
    DisposableEffect(selectedDay) {
        val listener = db.collection("routines")
            .whereEqualTo("day", selectedDay)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = mutableListOf<Map<String, Any>>()
                    for (doc in snapshot.documents) {
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["id"] = doc.id
                        list.add(data)
                    }
                    routineList = list
                }
            }
        onDispose { listener.remove() }
    }

    Scaffold(
        containerColor = themeColors.backgroundColor,
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddRoutineDialog = true },
                    containerColor = themeColors.primaryColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Class", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Day Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { onDaySelected(day) },
                        label = { Text(day, color = if (selectedDay == day) Color.White else themeColors.textColor) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = themeColors.primaryColor,
                            containerColor = themeColors.cardColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Routine List
            if (routineList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No classes scheduled for $selectedDay", color = themeColors.textColor)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(routineList) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = themeColors.cardColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item["subject"].toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = themeColors.primaryColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Teacher: ${item["teacher"]}", color = themeColors.textColor)
                                    Text("Room: ${item["room"]}", color = themeColors.textColor)
                                }

                                if (isAdmin) {
                                    IconButton(onClick = {
                                        val docId = item["id"].toString()
                                        db.collection("routines").document(docId).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Class Removed", Toast.LENGTH_SHORT).show()
                                            }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddRoutineDialog) {
        AddRoutineDialog(
            selectedDay = selectedDay,
            onDismiss = { showAddRoutineDialog = false }
        )
    }
}

@Composable
fun AddRoutineDialog(selectedDay: String, onDismiss: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var subject by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class for $selectedDay") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher Initial/Name") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room No.") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (subject.isNotEmpty() && room.isNotEmpty()) {
                    val classData = hashMapOf(
                        "day" to selectedDay,
                        "subject" to subject,
                        "teacher" to teacher,
                        "room" to room,
                        "department" to "CSE"
                    )
                    db.collection("routines").add(classData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// --- Directory Screen ---
@Composable
fun DirectoryScreen(themeColors: AppThemeColors) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var studentList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    val isAdmin = auth.currentUser != null

    DisposableEffect(Unit) {
        val listener = db.collection("students")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = mutableListOf<Map<String, Any>>()
                    for (doc in snapshot.documents) {
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["id"] = doc.id
                        list.add(data)
                    }
                    studentList = list
                }
            }
        onDispose { listener.remove() }
    }

    Scaffold(
        containerColor = themeColors.backgroundColor,
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddStudentDialog = true },
                    containerColor = themeColors.primaryColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Student", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Student & Teacher Directory", style = MaterialTheme.typography.titleLarge, color = themeColors.textColor)
            Spacer(modifier = Modifier.height(12.dp))

            if (studentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Directory Information Found", color = themeColors.textColor)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(studentList) { student ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = themeColors.cardColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student["name"].toString(), style = MaterialTheme.typography.titleMedium, color = themeColors.primaryColor)
                                    Text("ID: ${student["student_id"]}", color = themeColors.textColor)
                                    Text("Phone: ${student["phone"]}", color = themeColors.textColor)
                                }
                                if (isAdmin) {
                                    IconButton(onClick = {
                                        val id = student["id"].toString()
                                        db.collection("students").document(id).delete()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddStudentDialog) {
        AddStudentDialog(onDismiss = { showAddStudentDialog = false })
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Directory Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("ID / Designation") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotEmpty()) {
                    val data = hashMapOf(
                        "name" to name,
                        "student_id" to studentId,
                        "phone" to phone
                    )
                    db.collection("students").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Added to Directory!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
