package com.example.aiexpensemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aiexpensemanager.data.model.LearnedRule
import com.example.aiexpensemanager.ui.ExpenseUiState

@Composable
fun SettingsScreen(
    uiState: ExpenseUiState,
    onSaveBudget: (limit: Double, enabled: Boolean) -> Unit,
    onSaveCurrency: (String) -> Unit,
    onAddLearnedRule: (keyword: String, category: String, type: com.example.aiexpensemanager.data.model.TransactionType) -> Unit = { _, _, _ -> },
    onDeleteLearnedRule: (LearnedRule) -> Unit
) {
    var budgetInput by remember(uiState.budgetLimit) {
        mutableStateOf(uiState.budgetLimit.toInt().toString())
    }
    var budgetEnabled by remember(uiState.isBudgetEnabled) {
        mutableStateOf(uiState.isBudgetEnabled)
    }
    var newKeyword by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    val categories = listOf("Food", "Transport", "Bills", "Shopping", "Health", "Rent", "Entertainment", "Family", "Education", "Income", "Other")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Currency Setting: Strictly Indian Rupee (INR)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Currency: Indian Rupee (₹ INR)", fontWeight = FontWeight.SemiBold)
                    Text(
                        "All calculations, expense entries, and budget limits strictly operate in INR (₹).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AssistChip(
                        onClick = { onSaveCurrency("₹") },
                        label = { Text("Locked to ₹ (INR)") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }

        // Monthly Budget Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Monthly Budget (INR)", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Alert when spending exceeds limit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = budgetEnabled,
                            onCheckedChange = {
                                budgetEnabled = it
                                onSaveBudget(budgetInput.toDoubleOrNull() ?: uiState.budgetLimit, it)
                            }
                        )
                    }

                    if (budgetEnabled) {
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            label = { Text("Budget Limit (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val limit = budgetInput.toDoubleOrNull() ?: uiState.budgetLimit
                                onSaveBudget(limit, budgetEnabled)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Budget")
                        }
                    }
                }
            }
        }

        // Add Keyword Mapping Section ("Tea means Food")
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Add Keyword Mapping ("Tea means Food")", fontWeight = FontWeight.SemiBold)
                    }

                    Text(
                        "Teach the local offline engine custom words. For example, typing 'tea' will map to 'Food'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = newKeyword,
                        onValueChange = { newKeyword = it },
                        label = { Text("When text contains word (e.g. tea, chai, shawarma)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Means Category:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Food", "Transport", "Bills", "Health", "Shopping").forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val kw = newKeyword.trim().lowercase()
                            if (kw.isNotBlank()) {
                                val type = if (selectedCategory == "Income") {
                                    com.example.aiexpensemanager.data.model.TransactionType.Income
                                } else {
                                    com.example.aiexpensemanager.data.model.TransactionType.Expense
                                }
                                onAddLearnedRule(kw, selectedCategory, type)
                                newKeyword = ""
                            }
                        },
                        enabled = newKeyword.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Mapping ("$newKeyword" means $selectedCategory)")
                    }

                    if (uiState.learnedRules.isEmpty()) {
                        Text(
                            "No custom rules added yet. Use the form above to add terms!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "Active Custom Mappings (${uiState.learnedRules.size}):",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        items(uiState.learnedRules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = ""$" + "{rule.keyword}" → $" + "{rule.category}",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Learned offline on device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDeleteLearnedRule(rule) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Rule")
                    }
                }
            }
        }
    }
}
