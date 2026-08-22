package com.example.gonosync

data class RoutineItem(
    val id: Int = (0..1000).random(),
    val day: String,
    val department: String,
    val subject: String,
    val time: String,
    val room: String,
    val teacher: String,
    val isLive: Boolean = false
)

data class NoticeItem(
    val id: Int = (0..1000).random(),
    val author: String,
    val role: String,
    val message: String,
    val timestamp: String
)

data class StudentProfile(
    val id: String,
    val name: String,
    val department: String,
    val bloodGroup: String,
    val phone: String
)
