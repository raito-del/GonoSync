package com.example.gonosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableStateOf("Sat") }
    var selectedDept by remember { mutableStateOf("CSE") }

    var isAdminLoggedIn by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showAddRoutineDialog by remember { mutableStateOf(false) }
    var showAddNoticeDialog by remember { mutableStateOf(false) }

    val days = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu")

    val routineList = remember {
        mutableStateListOf(
            RoutineItem(1, "Sat", "CSE", "Structured Programming", "10:00 AM - 11:30 AM", "Room 302", "Dept Teacher A", isLive = true),
            RoutineItem(2, "Sat", "CSE", "Discrete Math", "11:30 AM - 01:00 PM", "Room 405", "Dept Teacher B"),
            RoutineItem(3, "Sat", "CSE", "Physics Lab", "02:00 PM - 04:00 PM", "Lab 2 (CSE)", "Lab Assistant")
        )
    }

    val noticeList = remember {
        mutableStateListOf(
            NoticeItem(1, "Super Admin", "CR / Lead", "Class 302 shifted to Room 405 for today's lecture.", "10:15 AM"),
            NoticeItem(2, "Dept Office", "Notice", "Midterm exam schedule will be published next week.", "Yesterday")
        )
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
            },
            floatingActionButton = {
                if (isAdminLoggedIn) {
                    FloatingActionButton(
                        onClick = {
                            if (selectedTab == 0) showAddRoutineDialog = true
                            else if (selectedTab == 1) showAddNoticeDialog = true
                        },
                        containerColor = Color(0xFF38BDF8)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    }
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
                    0 -> RoutineTab(
                        routineList = routineList.filter { it.day == selectedDay && it.department == selectedDept },
                        isAdmin = isAdminLoggedIn,
                        selectedDay = selectedDay,
                        onDelete = { item -> routineList.remove(item) }
                    )
                    1 -> NoticeTab(noticeList = noticeList)
                    2 -> DirectoryTab(studentList = studentList)
                }
            }
        }

        if (showLoginDialog) {
            AdminLoginDialog(
                onDismiss = { showLoginDialog = false },
                onSuccess = {
                    isAdminLoggedIn = true
                    showLoginDialog = false
                }
            )
        }

        if (showAddRoutineDialog) {
            AddRoutineDialog(
                currentDay = selectedDay,
                currentDept = selectedDept,
                onDismiss = { showAddRoutineDialog = false },
                onAdd = { newItem ->
                    routineList.add(newItem)
                    showAddRoutineDialog = false
                }
            )
        }

        if (showAddNoticeDialog) {
            AddNoticeDialog(
                onDismiss = { showAddNoticeDialog = false },
                onAdd = { newNotice ->
                    noticeList.add(0, newNotice)
                    showAddNoticeDialog = false
                }
            )
        }
    }
}
