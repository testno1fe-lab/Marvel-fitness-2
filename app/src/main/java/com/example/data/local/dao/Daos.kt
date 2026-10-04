package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY isActive DESC, fullName ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    fun getMemberById(id: Long): Flow<MemberEntity?>

    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY fullName ASC")
    fun getActiveMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE expiryDate <= :thresholdTimestamp ORDER BY expiryDate ASC")
    fun getExpiringMembers(thresholdTimestamp: Long): Flow<List<MemberEntity>>

    @Query("SELECT COUNT(*) FROM members WHERE isActive = 1")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM members")
    fun getTotalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteMemberById(id: Long)

    @Query("UPDATE members SET expiryDate = :newExpiry, isActive = 1 WHERE id = :memberId")
    suspend fun extendMembership(memberId: Long, newExpiry: Long)

    @Query("UPDATE members SET lastCheckIn = :checkInTime, attendanceCount = attendanceCount + 1 WHERE id = :memberId")
    suspend fun recordMemberAttendance(memberId: Long, checkInTime: Long)
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans ORDER BY price ASC")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY price ASC")
    fun getActivePlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun deletePlanById(id: Long)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE memberId = :memberId ORDER BY paymentDate DESC")
    fun getPaymentsForMember(memberId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT SUM(amount) FROM payments")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :startTimestamp")
    fun getRevenueSince(startTimestamp: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payments: List<PaymentEntity>)
}

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins WHERE checkInTime >= :startOfDay ORDER BY checkInTime DESC")
    fun getTodayCheckIns(startOfDay: Long): Flow<List<CheckInEntity>>

    @Query("SELECT COUNT(*) FROM check_ins WHERE checkInTime >= :startOfDay")
    fun getTodayCheckInCount(startOfDay: Long): Flow<Int>

    @Query("SELECT * FROM check_ins ORDER BY checkInTime DESC LIMIT 50")
    fun getRecentCheckIns(): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkIns: List<CheckInEntity>)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(announcements: List<AnnouncementEntity>)
}
