package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MarvelFitnessDatabase
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PlanEntity
import com.example.data.repository.MarvelFitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ExpiryTab {
    URGENT_7_DAYS,
    MONTH_30_DAYS,
    ALREADY_EXPIRED
}

class MarvelViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MarvelFitnessDatabase.getDatabase(application, viewModelScope)
    private val repository = MarvelFitnessRepository(
        memberDao = database.memberDao(),
        planDao = database.planDao(),
        paymentDao = database.paymentDao(),
        checkInDao = database.checkInDao(),
        announcementDao = database.announcementDao()
    )

    // Base flows
    val allMembers: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMembers: StateFlow<List<MemberEntity>> = repository.activeMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCount: StateFlow<Int> = repository.activeCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val plans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double> = repository.totalRevenue
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayCheckIns: StateFlow<List<CheckInEntity>> = repository.getTodayCheckIns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCheckInCount: StateFlow<Int> = repository.getTodayCheckInCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filter States for Members Screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlanFilter = MutableStateFlow("All")
    val selectedPlanFilter: StateFlow<String> = _selectedPlanFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("All") // "All", "Active", "Expired"
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    // Filtered Members Flow
    val filteredMembers: StateFlow<List<MemberEntity>> = combine(
        allMembers,
        _searchQuery,
        _selectedPlanFilter,
        _selectedStatusFilter
    ) { members, query, planFilter, statusFilter ->
        val now = System.currentTimeMillis()
        members.filter { member ->
            val matchesQuery = query.isBlank() ||
                member.fullName.contains(query, ignoreCase = true) ||
                member.phone.contains(query) ||
                member.planName.contains(query, ignoreCase = true)

            val matchesPlan = planFilter == "All" || member.planName.equals(planFilter, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                "Active" -> member.isActive && member.expiryDate >= now
                "Expired" -> !member.isActive || member.expiryDate < now
                else -> true
            }

            matchesQuery && matchesPlan && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expiry Screen Filter Tab
    private val _expiryTab = MutableStateFlow(ExpiryTab.URGENT_7_DAYS)
    val expiryTab: StateFlow<ExpiryTab> = _expiryTab.asStateFlow()

    // Categorized Expiring Members
    val urgentExpiringMembers: StateFlow<List<MemberEntity>> = allMembers.map { list ->
        val now = System.currentTimeMillis()
        val sevenDaysMs = 7 * 86_400_000L
        list.filter { it.expiryDate in now..(now + sevenDaysMs) }
            .sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthExpiringMembers: StateFlow<List<MemberEntity>> = allMembers.map { list ->
        val now = System.currentTimeMillis()
        val thirtyDaysMs = 30 * 86_400_000L
        list.filter { it.expiryDate in now..(now + thirtyDaysMs) }
            .sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiredMembers: StateFlow<List<MemberEntity>> = allMembers.map { list ->
        val now = System.currentTimeMillis()
        list.filter { it.expiryDate < now || !it.isActive }
            .sortedByDescending { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Feedback Messages
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    // Filter controls
    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setPlanFilter(plan: String) {
        _selectedPlanFilter.value = plan
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun setExpiryTab(tab: ExpiryTab) {
        _expiryTab.value = tab
    }

    // Actions
    fun addMember(
        name: String,
        phone: String,
        email: String,
        gender: String,
        selectedPlan: PlanEntity,
        paymentMethod: String,
        amount: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.MONTH, selectedPlan.durationMonths)
            }
            val newMember = MemberEntity(
                fullName = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                gender = gender,
                joinDate = now,
                planId = selectedPlan.id,
                planName = selectedPlan.title,
                expiryDate = cal.timeInMillis,
                isActive = true,
                emergencyContact = "",
                notes = notes.trim(),
                avatarColorIndex = (0..5).random(),
                lastCheckIn = 0,
                attendanceCount = 0
            )
            repository.addMemberWithPayment(newMember, paymentMethod, amount, notes)
            showMessage("Added new member: $name with ${selectedPlan.title}")
        }
    }

    fun updateMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.updateMember(member)
            showMessage("Updated ${member.fullName}'s profile")
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member.id)
            showMessage("Removed member: ${member.fullName}")
        }
    }

    fun renewMembership(
        member: MemberEntity,
        plan: PlanEntity,
        paymentMethod: String,
        amount: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.renewMembership(member, plan, paymentMethod, amount, notes)
            showMessage("Renewed ${member.fullName}'s plan for ${plan.durationMonths} mo")
        }
    }

    fun checkInMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.recordCheckIn(member)
            showMessage("Checked in: ${member.fullName}")
        }
    }

    fun savePlan(
        id: Long,
        title: String,
        price: Double,
        durationMonths: Int,
        features: String,
        isPopular: Boolean,
        badgeText: String
    ) {
        viewModelScope.launch {
            val plan = PlanEntity(
                id = id,
                title = title.trim(),
                price = price,
                durationMonths = durationMonths,
                features = features.trim(),
                isPopular = isPopular,
                isActive = true,
                badgeText = badgeText.trim()
            )
            repository.savePlan(plan)
            showMessage(if (id == 0L) "Created plan: $title" else "Updated plan: $title")
        }
    }

    fun deletePlan(planId: Long) {
        viewModelScope.launch {
            repository.deletePlan(planId)
            showMessage("Deleted membership plan")
        }
    }

    fun recordManualPayment(
        memberId: Long,
        memberName: String,
        planName: String,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val payment = PaymentEntity(
                memberId = memberId,
                memberName = memberName,
                planName = planName,
                amount = amount,
                paymentDate = System.currentTimeMillis(),
                paymentMethod = paymentMethod,
                transactionRef = "TXN_MF_" + (10000..99999).random(),
                durationMonthsAdded = 1,
                notes = notes
            )
            repository.recordManualPayment(payment)
            showMessage("Payment of ₹${amount.toInt()} recorded for $memberName")
        }
    }
}
