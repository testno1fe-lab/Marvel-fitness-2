package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PlanEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    plans: List<PlanEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        phone: String,
        email: String,
        gender: String,
        plan: PlanEntity,
        paymentMethod: String,
        amount: Double,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedPlan by remember { mutableStateOf(plans.firstOrNull()) }
    var paymentMethod by remember { mutableStateOf("UPI") }
    var amountText by remember(selectedPlan) {
        mutableStateOf(selectedPlan?.price?.toInt()?.toString() ?: "0")
    }
    var notes by remember { mutableStateOf("") }
    var isPlanMenuExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
            color = MarvelDark
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Register New Member",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Member Details Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MarvelRed) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_member_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MarvelRed) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_member_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Gender Selection
                Text("Gender", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Male", "Female", "Other").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MarvelRed,
                                selectedLabelColor = TextWhite,
                                containerColor = MarvelSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Membership Plan Selector
                Text("Select Membership Plan *", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                ExposedDropdownMenuBox(
                    expanded = isPlanMenuExpanded,
                    onExpandedChange = { isPlanMenuExpanded = !isPlanMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPlan?.let { "${it.title} (₹${it.price.toInt()} / ${it.durationMonths}mo)" } ?: "Select a plan",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPlanMenuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MarvelRed,
                            unfocusedBorderColor = MarvelBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isPlanMenuExpanded,
                        onDismissRequest = { isPlanMenuExpanded = false },
                        modifier = Modifier.background(MarvelSurfaceElevated)
                    ) {
                        plans.forEach { plan ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(plan.title, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text("₹${plan.price.toInt()} for ${plan.durationMonths} mo", color = MarvelRedLight, fontSize = 12.sp)
                                    }
                                },
                                onClick = {
                                    selectedPlan = plan
                                    amountText = plan.price.toInt().toString()
                                    isPlanMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Payment Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MarvelRed,
                            unfocusedBorderColor = MarvelBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    // Payment Method selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Payment Mode", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("UPI", "Cash", "Card").forEach { method ->
                                FilterChip(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    label = { Text(method, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MarvelRed,
                                        selectedLabelColor = TextWhite,
                                        containerColor = MarvelSurface,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Fitness Goals / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Confirm Button
                Button(
                    onClick = {
                        val plan = selectedPlan ?: return@Button
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            val amount = amountText.toDoubleOrNull() ?: plan.price
                            onConfirm(name, phone, email, gender, plan, paymentMethod, amount, notes)
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank() && phone.isNotBlank() && selectedPlan != null,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_add_member_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarvelRed,
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register & Activate Membership", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditMemberDialog(
    member: MemberEntity,
    onDismiss: () -> Unit,
    onConfirm: (MemberEntity) -> Unit
) {
    var name by remember { mutableStateOf(member.fullName) }
    var phone by remember { mutableStateOf(member.phone) }
    var email by remember { mutableStateOf(member.email) }
    var emergencyContact by remember { mutableStateOf(member.emergencyContact) }
    var notes by remember { mutableStateOf(member.notes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
            color = MarvelDark
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Edit Member Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & Fitness Profile") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    member.copy(
                                        fullName = name.trim(),
                                        phone = phone.trim(),
                                        email = email.trim(),
                                        emergencyContact = emergencyContact.trim(),
                                        notes = notes.trim()
                                    )
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MarvelRed)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun RenewMemberDialog(
    member: MemberEntity,
    plans: List<PlanEntity>,
    onDismiss: () -> Unit,
    onConfirm: (plan: PlanEntity, paymentMethod: String, amount: Double, notes: String) -> Unit
) {
    var selectedPlan by remember {
        mutableStateOf(plans.find { it.title == member.planName } ?: plans.firstOrNull())
    }
    var paymentMethod by remember { mutableStateOf("UPI") }
    var amountText by remember(selectedPlan) {
        mutableStateOf(selectedPlan?.price?.toInt()?.toString() ?: "0")
    }
    var notes by remember { mutableStateOf("Renewal processed at desk") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
            color = MarvelDark
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Renew Membership",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Member info card
                Surface(
                    color = MarvelSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(member.fullName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
                        Text("Current Plan: ${member.planName}", color = MarvelRedLight, fontSize = 13.sp)
                        Text("Current Expiry: ${formatDate(member.expiryDate)}", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                // Choose Renewal Plan
                Text("Select Renewal Package", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    plans.forEach { plan ->
                        val isSelected = selectedPlan?.id == plan.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) MarvelRed else MarvelBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedPlan = plan
                                    amountText = plan.price.toInt().toString()
                                },
                            color = if (isSelected) MarvelSurfaceHighlight else MarvelSurface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan.title, fontWeight = FontWeight.Bold, color = TextWhite)
                                    Text("+${plan.durationMonths} Months Access", color = TextSecondary, fontSize = 12.sp)
                                }
                                Text("₹${plan.price.toInt()}", fontWeight = FontWeight.Bold, color = MarvelRedLight)
                            }
                        }
                    }
                }

                // Payment Mode & Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MarvelRed,
                            unfocusedBorderColor = MarvelBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Payment Mode", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("UPI", "Cash", "Card").forEach { m ->
                                FilterChip(
                                    selected = paymentMethod == m,
                                    onClick = { paymentMethod = m },
                                    label = { Text(m, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MarvelRed,
                                        selectedLabelColor = TextWhite,
                                        containerColor = MarvelSurface,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val plan = selectedPlan ?: return@Button
                        val amount = amountText.toDoubleOrNull() ?: plan.price
                        onConfirm(plan, paymentMethod, amount, notes)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_renew_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Autorenew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Renewal & Payment", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun AddEditPlanDialog(
    planToEdit: PlanEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        price: Double,
        durationMonths: Int,
        features: String,
        isPopular: Boolean,
        badgeText: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(planToEdit?.title ?: "") }
    var priceText by remember { mutableStateOf(planToEdit?.price?.toInt()?.toString() ?: "2999") }
    var durationText by remember { mutableStateOf(planToEdit?.durationMonths?.toString() ?: "3") }
    var features by remember {
        mutableStateOf(
            planToEdit?.features ?: "Gym Floor Access\nCrossFit & Functional Zone\nLocker & Shower"
        )
    }
    var isPopular by remember { mutableStateOf(planToEdit?.isPopular ?: false) }
    var badgeText by remember { mutableStateOf(planToEdit?.badgeText ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
            color = MarvelDark
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (planToEdit == null) "Create Custom Plan" else "Edit Membership Plan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Plan Title * (e.g. Pro Quarterly)") },
                    modifier = Modifier.fillMaxWidth().testTag("plan_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹) *") },
                        modifier = Modifier.weight(1f).testTag("plan_price_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MarvelRed,
                            unfocusedBorderColor = MarvelBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Months *") },
                        modifier = Modifier.weight(1f).testTag("plan_duration_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MarvelRed,
                            unfocusedBorderColor = MarvelBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }

                OutlinedTextField(
                    value = badgeText,
                    onValueChange = { badgeText = it },
                    label = { Text("Badge Label (e.g. Best Value)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = features,
                    onValueChange = { features = it },
                    label = { Text("Features & Perks (one per line)") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Highlight as Most Popular", color = TextWhite, fontSize = 14.sp)
                    Switch(
                        checked = isPopular,
                        onCheckedChange = { isPopular = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = MarvelRed
                        )
                    )
                }

                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        val duration = durationText.toIntOrNull() ?: 1
                        if (title.isNotBlank() && price > 0) {
                            onConfirm(title, price, duration, features, isPopular, badgeText)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_plan_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Plan", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun MemberDetailDialog(
    member: MemberEntity,
    onDismiss: () -> Unit,
    onRenewClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val daysRemaining = calculateDaysRemaining(member.expiryDate)
    val avatarColor = avatarColors[member.avatarColorIndex % avatarColors.size]

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MarvelBorder, RoundedCornerShape(20.dp)),
            color = MarvelDark
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Avatar & Name
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
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.2f))
                                .border(2.dp, avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.fullName.take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
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
                                text = "${member.gender} • Joined ${formatDate(member.joinDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = MarvelBorder)

                // Plan & Expiry Details Card
                Surface(
                    color = MarvelSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Plan", color = TextSecondary, fontSize = 13.sp)
                            Text(member.planName, fontWeight = FontWeight.Bold, color = MarvelRedLight)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Expiry Date", color = TextSecondary, fontSize = 13.sp)
                            Text(formatDate(member.expiryDate), fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Status", color = TextSecondary, fontSize = 13.sp)
                            ExpiryBadge(daysRemaining = daysRemaining)
                        }
                    }
                }

                // Attendance stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MarvelSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Check-ins", color = TextSecondary, fontSize = 11.sp)
                            Text("${member.attendanceCount} days", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MarvelSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Last Visit", color = TextSecondary, fontSize = 11.sp)
                            Text(formatDate(member.lastCheckIn), fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                        }
                    }
                }

                // Contact & Notes
                Surface(
                    color = MarvelSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Phone: ${member.phone}", color = TextWhite, fontSize = 13.sp)
                        if (member.email.isNotBlank()) {
                            Text("Email: ${member.email}", color = TextWhite, fontSize = 13.sp)
                        }
                        if (member.emergencyContact.isNotBlank()) {
                            Text("Emergency: ${member.emergencyContact}", color = MarvelWarning, fontSize = 13.sp)
                        }
                        if (member.notes.isNotBlank()) {
                            Text("Notes: ${member.notes}", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                // Quick Communication Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${member.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SMS", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                }

                // Big Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = onCheckInClick,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MarvelSurfaceElevated,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MarvelSuccess)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check-In")
                    }

                    Button(
                        onClick = onRenewClick,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MarvelRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Renew Plan")
                    }
                }
            }
        }
    }
}
