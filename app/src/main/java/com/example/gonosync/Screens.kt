package com.example.gonosync

import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore

// --- Routine UI ---
@Composable
fun RoutineTab(
    routineList: List<RoutineItem>,
    isAdmin: Boolean,
    selectedDay: String,
    onDelete: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (routineList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No classes scheduled for $selectedDay", color = Color.White)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(routineList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    text = item.subject,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Teacher: ${item.teacher}", color = MaterialTheme.colorScheme.onSurface)
                                Text("Room: ${item.room} | Time: ${item.time}", color = MaterialTheme.colorScheme.onSurface)
                            }

                            if (isAdmin) {
                                IconButton(onClick = { onDelete(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Routine", tint = Color.Black)
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(selectedDay = selectedDay, onDismiss = { showAddDialog = false })
    }
}

@Composable
fun AddRoutineDialog(selectedDay: String, onDismiss: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var subject by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class for $selectedDay") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher Name/Initial") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room No.") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:00 AM)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (subject.isNotEmpty() && room.isNotEmpty()) {
                    val data = hashMapOf(
                        "day" to selectedDay,
                        "department" to "CSE",
                        "subject" to subject,
                        "teacher" to teacher,
                        "room" to room,
                        "time" to time,
                        "isLive" to false
                    )
                    db.collection("routines").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Class Added!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// --- Notice UI ---
@Composable
fun NoticeTab(noticeList: List<NoticeItem>) {
    if (noticeList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No notices posted yet", color = Color.White)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(noticeList) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = notice.author.ifEmpty { "Notice" },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (notice.role.isNotEmpty()) {
                            Text(text = notice.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.message, color = MaterialTheme.colorScheme.onSurface)
                        if (notice.timestamp.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notice.timestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// --- Directory UI ---
@Composable
fun DirectoryTab(
    studentList: List<StudentProfile>,
    isAdmin: Boolean,
    onDelete: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (studentList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No student info found", color = Color.White)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(studentList) { student ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("ID: ${student.id} | Dept: ${student.department}", color = MaterialTheme.colorScheme.onSurface)
                                Text("Blood Group: ${student.bloodGroup} | Phone: ${student.phone}", color = MaterialTheme.colorScheme.onSurface)
                            }

                            if (isAdmin) {
                                IconButton(onClick = { onDelete(student.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Student", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student", tint = Color.Black)
            }
        }
    }

    if (showAddDialog) {
        AddStudentDialog(onDismiss = { showAddDialog = false })
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("CSE") }
    var bloodGroup by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student Info") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") })
                OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Department") })
                OutlinedTextField(value = bloodGroup, onValueChange = { bloodGroup = it }, label = { Text("Blood Group") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotEmpty() && studentId.isNotEmpty()) {
                    val data = hashMapOf(
                        "name" to name,
                        "department" to department,
                        "bloodGroup" to bloodGroup,
                        "phone" to phone
                    )
                    db.collection("students").document(studentId).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Student Added!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
