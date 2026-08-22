package com.example.gonosync

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

// Data Models
data class RoutineItem(
    val id: String = "",
    val day: String = "",
    val department: String = "",
    val subject: String = "",
    val time: String = "",
    val room: String = "",
    val teacher: String = "",
    val isLive: Boolean = false
)

data class NoticeItem(
    val id: String = "",
    val author: String = "",
    val tag: String = "",
    val content: String = "",
    val time: String = ""
)

data class StudentProfile(
    val id: String = "",
    val name: String = "",
    val dept: String = "",
    val bloodGroup: String = "",
    val phone: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GonoSyncMainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GonoSyncMainScreen() {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableStateOf("Sat") }
    var selectedDept by remember { mutableStateOf("CSE") }

    var isAdminLoggedIn by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }

    val days = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu")

    // Firebase Dynamic Lists
    val routineList = remember { mutableStateListOf<RoutineItem>() }
    val noticeList = remember { mutableStateListOf<NoticeItem>() }

    // Fetch Routine from Firebase Firestore
    LaunchedEffect(selectedDay, selectedDept) {
        db.collection("routines")
            .whereEqualTo("day", selectedDay)
            .whereEqualTo("department", selectedDept)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    routineList.clear()
                    for (doc in snapshot.documents) {
                        val item = doc.toObject(RoutineItem::class.java)?.copy(id = doc.id)
                        if (item != null) routineList.add(item)
                    }
                }
            }
    }

    // Fetch Notices from Firebase Firestore
    LaunchedEffect(Unit) {
        db.collection("notices")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    noticeList.clear()
                    for (doc in snapshot.documents) {
                        val item = doc.toObject(NoticeItem::class.java)?.copy(id = doc.id)
                        if (item != null) noticeList.add(item)
                    }
                }
            }
    }

    val studentList = remember {
        listOf(
            StudentProfile("231-15-001", "Sadnan Ahmed", "CSE", "O+", "01743836672"),
            StudentProfile("231-15-002", "Tanvir Hasan", "CSE", "A+", "01800000000"),
            StudentProfile("231-15-003", "Rahat Hossain", "CSE", "B+", "01900000000")
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
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Gono University • $selectedDept Dept",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isAdminLoggedIn) {
                                    isAdminLoggedIn = false
                                    Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                                } else {
                                    showLoginDialog = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isAdminLoggedIn) Icons.Default.Lock else Icons.Default.Person,
                                contentDescription = "Admin Login",
                                tint = if (isAdminLoggedIn) Color(0xFF22C55E) else Color.White
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(12.dp))
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
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
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
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Routine") },
                        label = { Text("Routine") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "Notices") },
                        label = { Text("Notices") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Directory") },
                        label = { Text("Directory") }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> RoutineTab(routineList = routineList)
                    1 -> NoticeTab(noticeList = noticeList)
                    2 -> DirectoryTab(studentList = studentList)
                }
            }
        }
    }
}

@Composable
fun RoutineTab(routineList: List<RoutineItem>) {
    if (routineList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("কোনো রুটিন পাওয়া যায়নি", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(routineList) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.subject, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Time: ${item.time}", color = Color(0xFF38BDF8))
                        Text(text = "Room: ${item.room} | Teacher: ${item.teacher}", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeTab(noticeList: List<NoticeItem>) {
    if (noticeList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("কোনো নোটিশ নেই", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(noticeList) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = item.author, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            Text(text = item.time, fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = item.content, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DirectoryTab(studentList: List<StudentProfile>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(studentList) { student ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = student.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(text = "ID: ${student.id} | Blood: ${student.bloodGroup}", color = Color.LightGray, fontSize = 12.sp)
                        Text(text = "Phone: ${student.phone}", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
