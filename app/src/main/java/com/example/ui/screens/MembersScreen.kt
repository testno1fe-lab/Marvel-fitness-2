package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PlanEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarvelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    viewModel: MarvelViewModel,
    modifier: Modifier = Modifier
) {
    val members by viewModel.filteredMembers.collectAsStateWithLifecycle()
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val selectedPlanFilter by viewModel.selectedPlanFilter.collectAsStateWithLifecycle()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<MemberEntity?>(null) }
    var memberToRenew by remember { mutableStateOf<MemberEntity?>(null) }
    var memberDetailView by remember { mutableStateOf<MemberEntity?>(null) }
    var memberToDelete by remember { mutableStateOf<MemberEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarvelBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = MarvelRed,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_member")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Header
            MarvelHeader(
                title = "Active Members",
                subtitle = "${members.size} members shown"
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by name, phone, or plan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MarvelRed) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("members_search_bar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MarvelRed,
                    unfocusedBorderColor = MarvelBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = MarvelDark,
                    unfocusedContainerColor = MarvelDark
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Active", "Expired").forEach { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MarvelRed,
                            selectedLabelColor = TextWhite,
                            containerColor = MarvelDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedStatusFilter == status) MarvelRed else MarvelBorder,
                            enabled = true,
                            selected = selectedStatusFilter == status
                        )
                    )
                }

                // Divider dot
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(MarvelBorder)
                        .align(Alignment.CenterVertically)
                )

                // Plan filter chips
                FilterChip(
                    selected = selectedPlanFilter == "All",
                    onClick = { viewModel.setPlanFilter("All") },
                    label = { Text("All Plans") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MarvelSurfaceElevated,
                        selectedLabelColor = TextWhite,
                        containerColor = MarvelDark,
                        labelColor = TextSecondary
                    )
                )

                plans.forEach { plan ->
                    FilterChip(
                        selected = selectedPlanFilter == plan.title,
                        onClick = { viewModel.setPlanFilter(plan.title) },
                        label = { Text(plan.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MarvelSurfaceElevated,
                            selectedLabelColor = MarvelRedLight,
                            containerColor = MarvelDark,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            // Member List
            if (members.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "No members match criteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Try adjusting your search query or filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(members, key = { it.id }) { member ->
                        MemberCard(
                            member = member,
                            onCardClick = { memberDetailView = member },
                            onRenewClick = { memberToRenew = member },
                            onCheckInClick = { viewModel.checkInMember(member) },
                            onEditClick = { memberToEdit = member },
                            onDeleteClick = { memberToDelete = member }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddMemberDialog) {
        AddMemberDialog(
            plans = plans,
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { name, phone, email, gender, plan, paymentMethod, amount, notes ->
                viewModel.addMember(name, phone, email, gender, plan, paymentMethod, amount, notes)
            }
        )
    }

    memberToEdit?.let { member ->
        EditMemberDialog(
            member = member,
            onDismiss = { memberToEdit = null },
            onConfirm = { updated ->
                viewModel.updateMember(updated)
            }
        )
    }

    memberToRenew?.let { member ->
        RenewMemberDialog(
            member = member,
            plans = plans,
            onDismiss = { memberToRenew = null },
            onConfirm = { plan, paymentMethod, amount, notes ->
                viewModel.renewMembership(member, plan, paymentMethod, amount, notes)
            }
        )
    }

    memberDetailView?.let { member ->
        MemberDetailDialog(
            member = member,
            onDismiss = { memberDetailView = null },
            onRenewClick = {
                memberDetailView = null
                memberToRenew = member
            },
            onCheckInClick = {
                viewModel.checkInMember(member)
            },
            onEditClick = {
                memberDetailView = null
                memberToEdit = member
            }
        )
    }

    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            containerColor = MarvelDark,
            title = { Text("Delete Member?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${member.fullName} from Marvel Fitness?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMember(member)
                        memberToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelError)
                ) {
                    Text("Delete", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
