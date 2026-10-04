package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PlanEntity
import com.example.ui.theme.*

@Composable
fun PlanCard(
    plan: PlanEntity,
    subscriberCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onSelectPlan: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val borderColor = if (plan.isPopular) MarvelRed else MarvelBorder
    val borderWidth = if (plan.isPopular) 1.5.dp else 1.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(18.dp))
            .testTag("plan_card_${plan.id}"),
        color = MarvelSurface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${plan.durationMonths} ${if (plan.durationMonths == 1) "Month" else "Months"} Duration • $subscriberCount members",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                if (plan.badgeText.isNotBlank() || plan.isPopular) {
                    val badgeLabel = if (plan.badgeText.isNotBlank()) plan.badgeText else "Popular"
                    Surface(
                        color = if (plan.isPopular) MarvelRedGlow else MarvelSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (plan.isPopular) MarvelRed else MarvelBorderLight
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (plan.isPopular) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MarvelGold,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = badgeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (plan.isPopular) MarvelRedLight else MarvelGold
                            )
                        }
                    }
                }
            }

            // Price Row
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "₹${plan.price.toInt()}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MarvelRedLight
                )
                Text(
                    text = "/ ${plan.durationMonths} mo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            HorizontalDivider(color = MarvelBorder)

            // Features Checklist
            val featuresList = plan.features.lines().filter { it.isNotBlank() }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                featuresList.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MarvelRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MarvelRed,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = feat.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f).height(44.dp).testTag("edit_plan_btn_${plan.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MarvelBorderLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Customize", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                if (onSelectPlan != null) {
                    Button(
                        onClick = onSelectPlan,
                        modifier = Modifier.weight(1f).height(44.dp).testTag("select_plan_btn_${plan.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MarvelRed,
                            contentColor = TextWhite
                        )
                    ) {
                        Text("Assign / Apply", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (onDeleteClick != null && subscriberCount == 0) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Plan",
                            tint = MarvelError
                        )
                    }
                }
            }
        }
    }
}
