package com.example.gonosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RoutineItem(
    val id: Long = System.currentTimeMillis(),
    val day: String,
    val department: String,
    val subject: String,
    val time: String,
    val room: String,
    val teacher: String,
    val isLive: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GonoSyncUltimateApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GonoSyncUltimateApp() {
    val days = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu")
    var selectedDay by remember { mutableStateOf("Sat") }
    var selectedDept by remember { mutableStateOf("CSE") }
    
    var isAdminLoggedIn by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val routineList = remember {
        mutableStateListOf(
            RoutineItem(1, "Sat", "CSE", "Structured Programming", "10:00 AM - 11:30 AM", "Room 302", "Dept Teacher A", isLive = true),
            RoutineItem(2, "Sat", "CSE", "Discrete Math", "11:30 AM - 01:00 PM", "Room 405", "Dept Teacher B"),
            RoutineItem(3, "Sat", "CSE", "Physics Lab", "02:00 PM - 04:00 PM", "Lab 2 (CSE)", "Lab Assistant"),
            RoutineItem(4, "Sun", "CSE", "Data Structure", "09:00 AM - 10:30 AM", "Room 301", "Dept Teacher C")
        )
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F172A), Color(0xFF1E1035), Color(0xFF0F172A))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GonoSync",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Gono University • $selectedDept Dept",
                                fontSize = 13.sp,
                                color = Color.LightGray
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { 
                                    if (isAdminLoggedIn) isAdminLoggedIn = false 
                                    else showLoginDialog = true 
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAdminLoggedIn) Icons.Default.Lock else Icons.Default.Person,
                                    contentDescription = "Admin Login",
                                    tint = if (isAdminLoggedIn) Color(0xFF22C55E) else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Day Selector Tabs
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(days) { day ->
                            val isSelected = day == selectedDay
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B).copy(alpha = 0.6f)
                                    )
                                    .clickable { selectedDay = day }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = day,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (isAdminLoggedIn) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = Color(0xFF38BDF8)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    }
                }
            }
        ) { padding ->
            val filteredList = routineList.filter { it.day == selectedDay && it.department == selectedDept }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedDay Schedule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isAdminLoggedIn) {
                        Text(
                            text = "● Admin Mode Active",
                            fontSize = 12.sp,
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No classes scheduled for $selectedDay", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredList, key = { it.id }) { item ->
                            GlassmorphicCard(
                                item = item,
                                isAdmin = isAdminLoggedIn,
                                onDelete = { routineList.remove(item) }
                            )
                        }
                    }
                }
            }
        }

        // Login Dialog
        if (showLoginDialog) {
            AdminLoginDialog(
                onDismiss = { showLoginDialog = false },
                onSuccess = {
                    isAdminLoggedIn = true
                    showLoginDialog = false
                }
            )
        }

        // Add Routine Dialog
        if (showAddDialog) {
            AddRoutineDialog(
                currentDay = selectedDay,
                currentDept = selectedDept,
                onDismiss = { showAddDialog = false },
                onAdd = { newItem ->
                    routineList.add(newItem)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun GlassmorphicCard(item: RoutineItem, isAdmin: Boolean, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.subject,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (item.isLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("LIVE NOW", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "⏰ ${item.time}", fontSize = 14.sp, color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📍 ${item.room}  •  👨‍🏫 ${item.teacher}", fontSize = 13.sp, color = Color.Gray)
                if (isAdmin) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
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
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID / Phone") }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userId == "01743836672" && password == "nemesis00") {
                        onSuccess()
                    } else {
                        errorMsg = "ভুল ID অথবা Password!"
                    }
                }
            ) {
                Text("Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
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
        title = { Text("নতুন ক্লাস অ্যাড করুন ($currentDay)", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject Name") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:00 AM)") })
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room No") })
                OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher Name") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onAdd(
                            RoutineItem(
                                day = currentDay,
                                department = currentDept,
                                subject = subject,
                                time = time,
                                room = room,
                                teacher = teacher
                            )
                        )
                    }
                }
            ) {
                Text("Add Class")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = Color(0xFF1E293B)
    )
}
