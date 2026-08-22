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

// --- Data Models ---
data class RoutineItem(
    val id: String = "",
    val day: String = "",
    val department: String = "",
    val subject: String = "",
    val teacher: String = "",
    val room: String = ""
)

data class NoticeItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = ""
)

data class StudentProfile(
    val studentId: String = "",
    val name: String = "",
    val dept: String = "",
    val bloodGroup: String = "",
    val phone: String = ""
)

// --- Routine Tab ---
@Composable
fun RoutineTab(
    routineList: List<RoutineItem>,
    isAdmin: Boolean,
    selectedDay: String,
    onDelete: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
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
                                Text("Room: ${item.room}", color = MaterialTheme.colorScheme.onSurface)
                            }

                            if (isAdmin) {
                                IconButton(onClick = {
                                    db.collection("routines").document(item.id).delete()
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class for $selectedDay") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher Name/Initial") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room No.") })
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
                        "room" to room
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

// --- Notice Tab ---
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
                        Text(notice.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.description, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

// --- Directory Tab ---
@Composable
fun DirectoryTab(studentList: List<StudentProfile>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(studentList) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(student.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text("ID: ${student.studentId} | Dept: ${student.dept}", color = MaterialTheme.colorScheme.onSurface)
                    Text("Blood Group: ${student.bloodGroup} | Phone: ${student.phone}", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
