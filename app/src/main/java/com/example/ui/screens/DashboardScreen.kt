package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun DashboardScreen(
    viewModel: MarvelViewModel,
    onNavigateToMembers: () -> Unit,
    onNavigateToExpiry: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onNavigateToPayments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCount by viewModel.activeCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val urgentExpiring by viewModel.urgentExpiringMembers.collectAsStateWithLifecycle()
    val todayCheckInCount by viewModel.todayCheckInCount.collectAsStateWithLifecycle()
    val todayCheckIns by viewModel.todayCheckIns.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showQuickCheckInDialog by remember { mutableStateOf(false) }
    var selectedMemberForCheckIn by remember { mutableStateOf<MemberEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarvelBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            MarvelHeader(
                title = "Marvel Fitness",
                subtitle = "Strength • Discipline • Excellence",
                actions = {
                    FilledTonalIconButton(
                        onClick = { showQuickCheckInDialog = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MarvelSurfaceElevated,
                            contentColor = TextWhite
                        ),
                        modifier = Modifier.testTag("dashboard_checkin_header_btn")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Quick Check-in")
                    }
                }
            )
        }

        // Hero Gym Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
                color = MarvelSurface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF261010),
                                    MarvelSurfaceElevated,
                                    MarvelSurface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MarvelRedGlow,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MarvelRed.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "GYM STATUS: OPEN",
                                    color = MarvelRedLight,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "5:00 AM - 11:00 PM",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Text(
                            text = "UNLEASH YOUR STRENGTH",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "$activeCount active athletes training. $todayCheckInCount check-ins logged today.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showAddMemberDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MarvelRed,
                                    contentColor = TextWhite
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("hero_add_member_btn")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Member", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = onNavigateToExpiry,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MarvelBorderLight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("View Expiries (${urgentExpiring.size})", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Text(
                text = "Gym Metrics Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Active Members",
                    value = activeCount.toString(),
                    icon = Icons.Default.FitnessCenter,
                    accentColor = MarvelRed,
                    subtext = "Out of $totalCount total",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMembers
                )

                StatCard(
                    title = "Expiring (7 Days)",
                    value = urgentExpiring.size.toString(),
                    icon = Icons.Default.WarningAmber,
                    accentColor = if (urgentExpiring.isNotEmpty()) MarvelWarning else MarvelSuccess,
                    subtext = if (urgentExpiring.isNotEmpty()) "Requires renewal" else "All up to date",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToExpiry
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Today's Attendance",
                    value = todayCheckInCount.toString(),
                    icon = Icons.Default.DirectionsRun,
                    accentColor = MarvelSuccess,
                    subtext = "Active on gym floor",
                    modifier = Modifier.weight(1f),
                    onClick = { showQuickCheckInDialog = true }
                )

                StatCard(
                    title = "Total Collections",
                    value = "₹${(totalRevenue / 1000).toInt()}k",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = MarvelGold,
                    subtext = "₹${totalRevenue.toInt()} total",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPayments
                )
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "Quick Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "Register",
                    color = MarvelRed,
                    modifier = Modifier.weight(1f),
                    onClick = { showAddMemberDialog = true }
                )
                QuickActionButton(
                    icon = Icons.Default.CheckCircle,
                    label = "Check-In",
                    color = MarvelSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { showQuickCheckInDialog = true }
                )
                QuickActionButton(
                    icon = Icons.Default.Autorenew,
                    label = "Renewals",
                    color = MarvelWarning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToExpiry
                )
                QuickActionButton(
                    icon = Icons.Default.Tune,
                    label = "Plans",
                    color = MarvelGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPlans
                )
            }
        }

        // Today's Live Attendance Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Gym Check-ins",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "$todayCheckInCount athletes",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        if (todayCheckIns.isEmpty()) {
            item {
                Surface(
                    color = MarvelSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No check-ins recorded yet today", color = TextSecondary, fontSize = 14.sp)
                        TextButton(onClick = { showQuickCheckInDialog = true }) {
                            Text("Record Fast Check-in", color = MarvelRedLight)
                        }
                    }
                }
            }
        } else {
            items(todayCheckIns.take(4)) { checkIn ->
                Surface(
                    color = MarvelSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MarvelSuccessGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MarvelSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(checkIn.memberName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                Text("Checked in at ${formatTime(checkIn.checkInTime)}", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Surface(
                            color = MarvelSurfaceElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Training",
                                color = MarvelSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Gym Updates & Announcements
        item {
            Text(
                text = "Gym Notices & Updates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        items(announcements) { note ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MarvelBorder, RoundedCornerShape(14.dp)),
                color = MarvelSurface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(note.title, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 15.sp)
                        Surface(
                            color = MarvelSurfaceElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = note.category,
                                color = MarvelRedLight,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(note.message, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(formatDate(note.date), color = TextMuted, fontSize = 11.sp)
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

    if (showQuickCheckInDialog) {
        QuickCheckInDialog(
            members = allMembers.filter { it.isActive },
            onDismiss = { showQuickCheckInDialog = false },
            onCheckIn = { member ->
                viewModel.checkInMember(member)
                showQuickCheckInDialog = false
            }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MarvelBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = MarvelSurface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickCheckInDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onCheckIn: (MemberEntity) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = members.filter {
        query.isBlank() || it.fullName.contains(query, ignoreCase = true) || it.phone.contains(query)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MarvelDark,
        title = { Text("Quick Gym Check-In", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search member by name or phone...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filtered) { m ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onCheckIn(m) },
                            color = MarvelSurface
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(m.fullName, fontWeight = FontWeight.Bold, color = TextWhite)
                                    Text(m.planName, color = TextSecondary, fontSize = 11.sp)
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MarvelSuccess)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = TextSecondary) }
        }
    )
}
