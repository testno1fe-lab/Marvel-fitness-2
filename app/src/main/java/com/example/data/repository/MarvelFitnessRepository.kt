package com.example.data.repository

import com.example.data.local.dao.AnnouncementDao
import com.example.data.local.dao.CheckInDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.dao.PaymentDao
import com.example.data.local.dao.PlanDao
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PlanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

class MarvelFitnessRepository(
    private val memberDao: MemberDao,
    private val planDao: PlanDao,
    private val paymentDao: PaymentDao,
    private val checkInDao: CheckInDao,
    private val announcementDao: AnnouncementDao
) {
    // Members Flow
    val allMembers: Flow<List<MemberEntity>> = memberDao.getAllMembers()
    val activeMembers: Flow<List<MemberEntity>> = memberDao.getActiveMembers()
    val activeCount: Flow<Int> = memberDao.getActiveCount()
    val totalCount: Flow<Int> = memberDao.getTotalCount()

    // Plans Flow
    val allPlans: Flow<List<PlanEntity>> = planDao.getAllPlans()
    val activePlans: Flow<List<PlanEntity>> = planDao.getActivePlans()

    // Payments & Revenue Flow
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val totalRevenue: Flow<Double?> = paymentDao.getTotalRevenue()

    // Check-ins Flow
    fun getTodayCheckIns(): Flow<List<CheckInEntity>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return checkInDao.getTodayCheckIns(calendar.timeInMillis)
    }

    fun getTodayCheckInCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return checkInDao.getTodayCheckInCount(calendar.timeInMillis)
    }

    // Announcements Flow
    val allAnnouncements: Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    // Member Operations
    suspend fun addMemberWithPayment(
        member: MemberEntity,
        paymentMethod: String,
        amountPaid: Double,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val memberId = memberDao.insertMember(member)
        val txnRef = "TXN_MF_" + UUID.randomUUID().toString().take(6).uppercase()
        val payment = PaymentEntity(
            memberId = memberId,
            memberName = member.fullName,
            planName = member.planName,
            amount = amountPaid,
            paymentDate = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            transactionRef = txnRef,
            durationMonthsAdded = 1,
            notes = if (notes.isNotBlank()) notes else "Initial membership fee"
        )
        paymentDao.insertPayment(payment)
        memberId
    }

    suspend fun updateMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        memberDao.updateMember(member)
    }

    suspend fun deleteMember(memberId: Long) = withContext(Dispatchers.IO) {
        memberDao.deleteMemberById(memberId)
    }

    suspend fun renewMembership(
        member: MemberEntity,
        plan: PlanEntity,
        paymentMethod: String,
        amountPaid: Double,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val baseDate = if (member.expiryDate > now) member.expiryDate else now
        val cal = Calendar.getInstance().apply {
            timeInMillis = baseDate
            add(Calendar.MONTH, plan.durationMonths)
        }
        val newExpiry = cal.timeInMillis

        // Update member's expiry & active flag & current plan
        val updatedMember = member.copy(
            expiryDate = newExpiry,
            isActive = true,
            planId = plan.id,
            planName = plan.title
        )
        memberDao.updateMember(updatedMember)

        // Record payment
        val txnRef = "RNW_MF_" + UUID.randomUUID().toString().take(6).uppercase()
        val payment = PaymentEntity(
            memberId = member.id,
            memberName = member.fullName,
            planName = plan.title,
            amount = amountPaid,
            paymentDate = now,
            paymentMethod = paymentMethod,
            transactionRef = txnRef,
            durationMonthsAdded = plan.durationMonths,
            notes = if (notes.isNotBlank()) notes else "Membership renewal (${plan.title})"
        )
        paymentDao.insertPayment(payment)
    }

    suspend fun recordCheckIn(member: MemberEntity) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        checkInDao.insertCheckIn(
            CheckInEntity(
                memberId = member.id,
                memberName = member.fullName,
                checkInTime = now
            )
        )
        memberDao.recordMemberAttendance(member.id, now)
    }

    // Plan Operations
    suspend fun savePlan(plan: PlanEntity) = withContext(Dispatchers.IO) {
        if (plan.id == 0L) {
            planDao.insertPlan(plan)
        } else {
            planDao.updatePlan(plan)
        }
    }

    suspend fun deletePlan(planId: Long) = withContext(Dispatchers.IO) {
        planDao.deletePlanById(planId)
    }

    // Payment Operations
    suspend fun recordManualPayment(payment: PaymentEntity) = withContext(Dispatchers.IO) {
        paymentDao.insertPayment(payment)
    }
}
