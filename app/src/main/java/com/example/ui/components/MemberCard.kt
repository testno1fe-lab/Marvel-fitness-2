package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MemberEntity
import com.example.ui.theme.*

@Composable
fun MemberCard(
    member: MemberEntity,
    onCardClick: () -> Unit,
    onRenewClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val daysRemaining = calculateDaysRemaining(member.expiryDate)
    val avatarColor = avatarColors[member.avatarColorIndex % avatarColors.size]

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MarvelBorder, RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("member_card_${member.id}"),
        color = MarvelSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Avatar, Name, Plan, and Overflow Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.2f))
                            .border(1.5.dp, avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.fullName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = avatarColor
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = member.planName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MarvelRedLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("•", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = member.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp).testTag("member_menu_${member.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(MarvelSurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile", color = TextWhite) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                            onClick = {
                                menuExpanded = false
                                onCardClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Renew Plan", color = MarvelRed) },
                            leadingIcon = { Icon(Icons.Default.Autorenew, contentDescription = null, tint = MarvelRed) },
                            onClick = {
                                menuExpanded = false
                                onRenewClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Details", color = TextWhite) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary) },
                            onClick = {
                                menuExpanded = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Call Member", color = TextWhite) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary) },
                            onClick = {
                                menuExpanded = false
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                                context.startActivity(intent)
                            }
                        )
                        HorizontalDivider(color = MarvelBorder)
                        DropdownMenuItem(
                            text = { Text("Delete", color = MarvelError) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MarvelError) },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            // Expiry info & Quick Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Expiry: ${formatDate(member.expiryDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    ExpiryBadge(daysRemaining = daysRemaining)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Quick Check-in button
                    FilledTonalButton(
                        onClick = onCheckInClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MarvelSurfaceElevated,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp).testTag("check_in_btn_${member.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Check-in",
                            tint = MarvelSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Quick Renew button
                    Button(
                        onClick = onRenewClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MarvelRed,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp).testTag("renew_btn_${member.id}")
                    ) {
                        Text("Renew", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
