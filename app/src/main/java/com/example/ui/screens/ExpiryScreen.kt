package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.MemberEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExpiryTab
import com.example.ui.viewmodel.MarvelViewModel

@Composable
fun ExpiryScreen(
    viewModel: MarvelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.expiryTab.collectAsStateWithLifecycle()
    val urgentList by viewModel.urgentExpiringMembers.collectAsStateWithLifecycle()
    val monthList by viewModel.monthExpiringMembers.collectAsStateWithLifecycle()
    val expiredList by viewModel.expiredMembers.collectAsStateWithLifecycle()
    val plans by viewModel.plans.collectAsStateWithLifecycle()

    var memberToRenew by remember { mutableStateOf<MemberEntity?>(null) }
    var memberDetailView by remember { mutableStateOf<MemberEntity?>(null) }

    val activeList = when (currentTab) {
        ExpiryTab.URGENT_7_DAYS -> urgentList
        ExpiryTab.MONTH_30_DAYS -> monthList
        ExpiryTab.ALREADY_EXPIRED -> expiredList
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarvelBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            MarvelHeader(
                title = "Membership Expiry",
                subtitle = "${urgentList.size} urgent • ${expiredList.size} expired"
            )

            // Tabs for Expiry Categorization
            TabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = MarvelBlack,
                contentColor = MarvelRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                        color = MarvelRed,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(color = MarvelBorder) }
            ) {
                Tab(
                    selected = currentTab == ExpiryTab.URGENT_7_DAYS,
                    onClick = { viewModel.setExpiryTab(ExpiryTab.URGENT_7_DAYS) },
                    text = {
                        Text(
                            "Next 7 Days (${urgentList.size})",
                            fontWeight = if (currentTab == ExpiryTab.URGENT_7_DAYS) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == ExpiryTab.URGENT_7_DAYS) MarvelRedLight else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = currentTab == ExpiryTab.MONTH_30_DAYS,
                    onClick = { viewModel.setExpiryTab(ExpiryTab.MONTH_30_DAYS) },
                    text = {
                        Text(
                            "30 Days (${monthList.size})",
                            fontWeight = if (currentTab == ExpiryTab.MONTH_30_DAYS) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == ExpiryTab.MONTH_30_DAYS) MarvelRedLight else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = currentTab == ExpiryTab.ALREADY_EXPIRED,
                    onClick = { viewModel.setExpiryTab(ExpiryTab.ALREADY_EXPIRED) },
                    text = {
                        Text(
                            "Expired (${expiredList.size})",
                            fontWeight = if (currentTab == ExpiryTab.ALREADY_EXPIRED) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == ExpiryTab.ALREADY_EXPIRED) MarvelError else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            // Banner reminder
            Surface(
                color = when (currentTab) {
                    ExpiryTab.URGENT_7_DAYS -> MarvelWarningGlow
                    ExpiryTab.MONTH_30_DAYS -> MarvelSurface
                    ExpiryTab.ALREADY_EXPIRED -> MarvelErrorGlow
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (currentTab) {
                        ExpiryTab.URGENT_7_DAYS -> MarvelWarning.copy(alpha = 0.4f)
                        ExpiryTab.MONTH_30_DAYS -> MarvelBorder
                        ExpiryTab.ALREADY_EXPIRED -> MarvelError.copy(alpha = 0.4f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = when (currentTab) {
                            ExpiryTab.URGENT_7_DAYS -> Icons.Default.AccessTime
                            ExpiryTab.MONTH_30_DAYS -> Icons.Default.CalendarMonth
                            ExpiryTab.ALREADY_EXPIRED -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when (currentTab) {
                            ExpiryTab.URGENT_7_DAYS -> MarvelWarning
                            ExpiryTab.MONTH_30_DAYS -> MarvelGold
                            ExpiryTab.ALREADY_EXPIRED -> MarvelError
                        },
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = when (currentTab) {
                            ExpiryTab.URGENT_7_DAYS -> "Memberships expiring soon. Reach out to secure early renewals!"
                            ExpiryTab.MONTH_30_DAYS -> "Athletes up for renewal within the next 30 days."
                            ExpiryTab.ALREADY_EXPIRED -> "Inactive accounts requiring win-back discounts or reactivation."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite
                    )
                }
            }

            // Expiry List
            if (activeList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MarvelSuccess,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No Members in this Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "All memberships are in healthy standing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeList, key = { it.id }) { member ->
                        ExpiryMemberCard(
                            member = member,
                            onCardClick = { memberDetailView = member },
                            onRenewClick = { memberToRenew = member },
                            onSendReminder = {
                                val message = "Hi ${member.fullName}, your Marvel Fitness membership (${member.planName}) is due on ${formatDate(member.expiryDate)}. Renew today to keep your fitness gains! - Marvel Fitness"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("sms:${member.phone}?body=${Uri.encode(message)}")
                                }
                                context.startActivity(intent)
                            },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
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
            }
        )
    }
}

@Composable
fun ExpiryMemberCard(
    member: MemberEntity,
    onCardClick: () -> Unit,
    onRenewClick: () -> Unit,
    onSendReminder: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysRemaining = calculateDaysRemaining(member.expiryDate)
    val avatarColor = avatarColors[member.avatarColorIndex % avatarColors.size]

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MarvelBorder, RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("expiry_card_${member.id}"),
        color = MarvelSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.2f))
                            .border(1.5.dp, avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.fullName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = avatarColor
                        )
                    }

                    Column {
                        Text(
                            text = member.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "${member.planName} • ${member.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                ExpiryBadge(daysRemaining = daysRemaining)
            }

            HorizontalDivider(color = MarvelBorder)

            // Action row: Call, Reminder SMS, and Renew
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onSendReminder,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    modifier = Modifier.weight(1.3f).height(38.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(15.dp), tint = MarvelGold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reminder", fontSize = 12.sp)
                }

                Button(
                    onClick = onRenewClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelRed),
                    modifier = Modifier.weight(1.3f).height(38.dp).testTag("expiry_renew_btn_${member.id}")
                ) {
                    Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Renew", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
