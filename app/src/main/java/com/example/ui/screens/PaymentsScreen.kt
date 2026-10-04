package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PlanEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarvelViewModel

@Composable
fun PaymentsScreen(
    viewModel: MarvelViewModel,
    modifier: Modifier = Modifier
) {
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val members by viewModel.allMembers.collectAsStateWithLifecycle()
    val plans by viewModel.plans.collectAsStateWithLifecycle()

    var selectedMethodFilter by remember { mutableStateOf("All") }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var selectedPaymentForReceipt by remember { mutableStateOf<PaymentEntity?>(null) }

    val filteredPayments = payments.filter {
        selectedMethodFilter == "All" || it.paymentMethod.equals(selectedMethodFilter, ignoreCase = true)
    }

    val avgTicket = if (payments.isNotEmpty()) (totalRevenue / payments.size).toInt() else 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarvelBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRecordPaymentDialog = true },
                containerColor = MarvelRed,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_record_payment")
            ) {
                Icon(Icons.Default.AddCard, contentDescription = "Record Payment")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            MarvelHeader(
                title = "Payments & Renewals",
                subtitle = "${payments.size} verified transactions"
            )

            // Revenue Metrics Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, MarvelBorder, RoundedCornerShape(18.dp)),
                color = MarvelSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Revenue Collected", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "₹${totalRevenue.toInt()}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MarvelGold
                        )
                        Text("Gym collections & membership fees", color = TextMuted, fontSize = 11.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = MarvelSurfaceElevated,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Avg: ₹$avgTicket",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${payments.size} receipts", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            // Payment Method Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "UPI", "Cash", "Card").forEach { method ->
                    FilterChip(
                        selected = selectedMethodFilter == method,
                        onClick = { selectedMethodFilter = method },
                        label = { Text(if (method == "All") "All Methods" else method) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MarvelRed,
                            selectedLabelColor = TextWhite,
                            containerColor = MarvelDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedMethodFilter == method) MarvelRed else MarvelBorder,
                            enabled = true,
                            selected = selectedMethodFilter == method
                        )
                    )
                }
            }

            // Transactions List
            if (filteredPayments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Text("No Payments Found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Payments will appear here when memberships are renewed.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPayments, key = { it.id }) { payment ->
                        PaymentReceiptCard(
                            payment = payment,
                            onClick = { selectedPaymentForReceipt = payment }
                        )
                    }
                }
            }
        }
    }

    if (showRecordPaymentDialog) {
        RecordPaymentModal(
            members = members,
            plans = plans,
            onDismiss = { showRecordPaymentDialog = false },
            onConfirm = { memberId, memberName, planName, amount, method, notes ->
                viewModel.recordManualPayment(memberId, memberName, planName, amount, method, notes)
                showRecordPaymentDialog = false
            }
        )
    }

    selectedPaymentForReceipt?.let { payment ->
        PaymentReceiptDialog(
            payment = payment,
            onDismiss = { selectedPaymentForReceipt = null }
        )
    }
}

@Composable
fun PaymentReceiptCard(
    payment: PaymentEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val methodColor = when (payment.paymentMethod) {
        "UPI" -> Color(0xFF3B82F6)
        "Cash" -> Color(0xFF10B981)
        "Card" -> Color(0xFF8B5CF6)
        else -> MarvelGold
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MarvelBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("payment_card_${payment.id}"),
        color = MarvelSurface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(methodColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (payment.paymentMethod) {
                            "UPI" -> Icons.Default.QrCode
                            "Cash" -> Icons.Default.Payments
                            "Card" -> Icons.Default.CreditCard
                            else -> Icons.Default.Receipt
                        },
                        contentDescription = null,
                        tint = methodColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = payment.memberName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${payment.planName} • ${formatDate(payment.paymentDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = payment.transactionRef,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+₹${payment.amount.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MarvelSuccess
                )
                Surface(
                    color = methodColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = payment.paymentMethod,
                        color = methodColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecordPaymentModal(
    members: List<MemberEntity>,
    plans: List<PlanEntity>,
    onDismiss: () -> Unit,
    onConfirm: (memberId: Long, memberName: String, planName: String, amount: Double, method: String, notes: String) -> Unit
) {
    var selectedMember by remember { mutableStateOf<MemberEntity?>(members.firstOrNull()) }
    var selectedPlan by remember { mutableStateOf<PlanEntity?>(plans.firstOrNull()) }
    var amountText by remember { mutableStateOf(selectedPlan?.price?.toInt()?.toString() ?: "1500") }
    var method by remember { mutableStateOf("UPI") }
    var notes by remember { mutableStateOf("Manual desk collection") }

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
                    text = "Record Membership Payment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                // Select Member
                Text("Select Athlete", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                var memberSearch by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = memberSearch,
                    onValueChange = { memberSearch = it },
                    placeholder = { Text("Type member name...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                val filteredMembers = members.filter {
                    memberSearch.isBlank() || it.fullName.contains(memberSearch, ignoreCase = true)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredMembers.take(6).forEach { m ->
                        FilterChip(
                            selected = selectedMember?.id == m.id,
                            onClick = {
                                selectedMember = m
                                selectedPlan = plans.find { it.title == m.planName } ?: selectedPlan
                                amountText = selectedPlan?.price?.toInt()?.toString() ?: amountText
                            },
                            label = { Text(m.fullName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MarvelRed,
                                selectedLabelColor = TextWhite,
                                containerColor = MarvelSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Payment Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Mode
                Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("UPI", "Cash", "Card").forEach { m ->
                        FilterChip(
                            selected = method == m,
                            onClick = { method = m },
                            label = { Text(m) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MarvelRed,
                                selectedLabelColor = TextWhite,
                                containerColor = MarvelSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Reference") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MarvelRed,
                        unfocusedBorderColor = MarvelBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Button(
                    onClick = {
                        val member = selectedMember ?: return@Button
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        onConfirm(
                            member.id,
                            member.fullName,
                            selectedPlan?.title ?: member.planName,
                            amount,
                            method,
                            notes
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_manual_payment_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Record Receipt", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun PaymentReceiptDialog(
    payment: PaymentEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gym Receipt Header
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MarvelRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MF", fontWeight = FontWeight.Black, fontSize = 22.sp, color = TextWhite)
                }

                Text("MARVEL FITNESS", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextWhite)
                Text("OFFICIAL PAYMENT RECEIPT", color = MarvelRedLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                HorizontalDivider(color = MarvelBorder)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceiptRow("Transaction ID", payment.transactionRef)
                    ReceiptRow("Member Name", payment.memberName)
                    ReceiptRow("Plan Subscribed", payment.planName)
                    ReceiptRow("Payment Mode", payment.paymentMethod)
                    ReceiptRow("Date & Time", "${formatDate(payment.paymentDate)} at ${formatTime(payment.paymentDate)}")
                    if (payment.notes.isNotBlank()) {
                        ReceiptRow("Remarks", payment.notes)
                    }
                }

                HorizontalDivider(color = MarvelBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Paid:", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 16.sp)
                    Text("₹${payment.amount.toInt()}", fontWeight = FontWeight.Black, color = MarvelSuccess, fontSize = 22.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val shareText = """
                                Marvel Fitness Gym Receipt
                                Receipt ID: ${payment.transactionRef}
                                Member: ${payment.memberName}
                                Plan: ${payment.planName}
                                Amount Paid: ₹${payment.amount.toInt()} (${payment.paymentMethod})
                                Date: ${formatDate(payment.paymentDate)}
                                Thank you for choosing Marvel Fitness!
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Receipt"))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MarvelRed)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 13.sp)
        Text(value, color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
