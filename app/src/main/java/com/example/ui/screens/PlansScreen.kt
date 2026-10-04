package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.PlanEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarvelViewModel

@Composable
fun PlansScreen(
    viewModel: MarvelViewModel,
    onNavigateToMembersWithPlan: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()

    var showAddPlanDialog by remember { mutableStateOf(false) }
    var planToEdit by remember { mutableStateOf<PlanEntity?>(null) }
    var planToDelete by remember { mutableStateOf<PlanEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MarvelBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPlanDialog = true },
                containerColor = MarvelRed,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_plan")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Plan")
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
                title = "Membership Plans",
                subtitle = "Customizable membership tiers & rates"
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(plans, key = { it.id }) { plan ->
                    val subscriberCount = allMembers.count { it.planName == plan.title && it.isActive }
                    PlanCard(
                        plan = plan,
                        subscriberCount = subscriberCount,
                        onEditClick = { planToEdit = plan },
                        onDeleteClick = if (subscriberCount == 0 && plans.size > 1) {
                            { planToDelete = plan }
                        } else null,
                        onSelectPlan = {
                            viewModel.setPlanFilter(plan.title)
                            onNavigateToMembersWithPlan?.invoke(plan.title)
                        }
                    )
                }
            }
        }
    }

    if (showAddPlanDialog) {
        AddEditPlanDialog(
            planToEdit = null,
            onDismiss = { showAddPlanDialog = false },
            onConfirm = { title, price, duration, features, isPopular, badgeText ->
                viewModel.savePlan(0L, title, price, duration, features, isPopular, badgeText)
                showAddPlanDialog = false
            }
        )
    }

    planToEdit?.let { plan ->
        AddEditPlanDialog(
            planToEdit = plan,
            onDismiss = { planToEdit = null },
            onConfirm = { title, price, duration, features, isPopular, badgeText ->
                viewModel.savePlan(plan.id, title, price, duration, features, isPopular, badgeText)
                planToEdit = null
            }
        )
    }

    planToDelete?.let { plan ->
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            containerColor = MarvelDark,
            title = { Text("Delete Membership Plan?", color = TextWhite) },
            text = { Text("Are you sure you want to remove the '${plan.title}' tier?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePlan(plan.id)
                        planToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MarvelError)
                ) {
                    Text("Delete", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
