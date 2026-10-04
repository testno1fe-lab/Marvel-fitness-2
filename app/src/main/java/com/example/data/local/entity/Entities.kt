package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String = "",
    val gender: String = "Male",
    val joinDate: Long = System.currentTimeMillis(),
    val planId: Long,
    val planName: String,
    val expiryDate: Long,
    val isActive: Boolean = true,
    val emergencyContact: String = "",
    val notes: String = "",
    val avatarColorIndex: Int = 0,
    val lastCheckIn: Long = 0,
    val attendanceCount: Int = 0
)

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val price: Double,
    val durationMonths: Int,
    val features: String, // Comma or newline separated
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val badgeText: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val planName: String,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "UPI", // UPI, Cash, Card
    val transactionRef: String,
    val durationMonthsAdded: Int = 1,
    val notes: String = ""
)

@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val checkInTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val date: Long = System.currentTimeMillis(),
    val category: String = "Notice"
)
