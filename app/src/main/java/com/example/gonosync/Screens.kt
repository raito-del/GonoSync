package com.example.gonosync

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoutineTab(routineList: List<RoutineItem>, isAdmin: Boolean, selectedDay: String, onDelete: (RoutineItem) -> Unit) {
    Column {
        Text(
            text = "$selectedDay Schedule",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (routineList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No classes scheduled", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(routineList, key = { it.id }) { item ->
                    GlassCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.subject, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                if (item.isLive) {
                                    Surface(
                                        color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "LIVE NOW",
                                            color = Color(0xFF22C55E),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("⏰ ${item.time}", fontSize = 14.sp, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📍 ${item.room}  •  👨‍🏫 ${item.teacher}", fontSize = 13.sp, color = Color.Gray)
                                if (isAdmin) {
                                    IconButton(onClick = { onDelete(item) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeTab(noticeList: List<NoticeItem>) {
    Column {
        Text("Announcements", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(noticeList, key = { it.id }) { notice ->
                GlassCard {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(notice.author, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            Text(notice.timestamp, fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.message, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DirectoryTab(studentList: List<StudentProfile>) {
    Column {
        Text("Batch Directory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(studentList) { student ->
                GlassCard {
                    Column {
                        Text(student.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ID: ${student.id}  •  Blood Group: ${student.bloodGroup}", fontSize = 13.sp, color = Color.LightGray)
                        Text("📞 ${student.phone}", fontSize = 13.sp, color = Color(0xFF38BDF8))
                    }
                }
            }
        }
    }
}

@Composable
fun GlassCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun AdminLoginDialog(onDismiss: () -> Unit, onSuccess: () -> Unit) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Super Admin Login", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                if (errorMsg.isNotEmpty()) Text(errorMsg, color = Color.Red, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (userId == "01743836672" && password == "nemesis00") onSuccess()
                else errorMsg = "Invalid User ID or Password"
            }) { Text("Login") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color(0xFF1E293B)
    )
}

@Composable
fun AddRoutineDialog(currentDay: String, currentDept: String, onDismiss: () -> Unit, onAdd: (RoutineItem) -> Unit) {
    var subject by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class ($currentDept - $currentDay)", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (subject.isNotBlank()) onAdd(RoutineItem(day = currentDay, department = currentDept, subject = subject, time = time, room = room, teacher = teacher))
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color(0xFF1E293B)
    )
}

@Composable
fun AddNoticeDialog(onDismiss: () -> Unit, onAdd: (NoticeItem) -> Unit) {
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Post Announcement", color = Color.White) },
        text = {
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Notice / Update") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(onClick = {
                if (message.isNotBlank()) onAdd(NoticeItem(author = "Super Admin", role = "CR", message = message, timestamp = "Just Now"))
            }) { Text("Post") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color(0xFF1E293B)
    )
}
