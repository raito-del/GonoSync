package com.example.gonosync

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

data class NoticeItem(
    val id: Long = System.currentTimeMillis(),
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
