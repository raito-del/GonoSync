package com.example.gonosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A) // Slate Dark
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GonoSync",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Text(
                    text = "Gono University • CSE Dept",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "• Live Routine",
                    color = Color(0xFF4ADE80),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Today's Schedule",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(3) { index ->
                ClassCard(
                    subject = if (index == 0) "Structured Programming" else if (index == 1) "Discrete Math" else "Physics Lab",
                    time = if (index == 0) "10:00 AM - 11:30 AM" else if (index == 1) "11:30 AM - 01:00 PM" else "02:00 PM - 04:00 PM",
                    room = if (index == 0) "Room 302" else if (index == 1) "Room 405" else "Lab 2 (CSE)",
                    teacher = if (index == 0) "Dept Teacher A" else if (index == 1) "Dept Teacher B" else "Lab Assistant"
                )
            }
        }
    }
}

@Composable
fun ClassCard(subject: String, time: String, room: String, teacher: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = subject,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = room,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA855F7)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "⏱ $time", fontSize = 14.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "👨‍🏫 $teacher", fontSize = 14.sp, color = Color(0xFF94A3B8))
        }
    }
}
