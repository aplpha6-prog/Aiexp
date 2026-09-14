package com.example.aiexpensemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.TransactionType
import com.example.aiexpensemanager.ui.ExpenseUiState
import com.example.aiexpensemanager.ui.components.TransactionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: ExpenseUiState,
    onCategoryFilterChange: (String?) -> Unit,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onDeleteExpense: (Expense) -> Unit
) {
    val categories = listOf(
        "All", "Food", "Transport", "Bills", "Shopping", "Health",
        "Rent", "Entertainment", "Family", "Education", "Other", "Income"
    )

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // Filter items
    val filteredList = uiState.allExpenses.filter { expense ->
        val matchesCategory = uiState.selectedCategoryFilter == null || expense.category == uiState.selectedCategoryFilter
        val matchesType = uiState.selectedTypeFilter == null || expense.type == uiState.selectedTypeFilter
        matchesCategory && matchesType
    }

    val totalFilteredExpense = filteredList.filter { it.type == TransactionType.Expense }.sumOf { it.amount }
    val totalFilteredIncome = filteredList.filter { it.type == TransactionType.Income }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Type Filter Chips (All, Expense, Income)
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.selectedTypeFilter == null,
                    onClick = { onTypeFilterChange(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedTypeFilter == TransactionType.Expense,
                    onClick = { onTypeFilterChange(TransactionType.Expense) },
                    label = { Text("Expenses") }
                )
                FilterChip(
                    selected = uiState.selectedTypeFilter == TransactionType.Income,
                    onClick = { onTypeFilterChange(TransactionType.Income) },
                    label = { Text("Income") }
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = (cat == "All" && uiState.selectedCategoryFilter == null) ||
                            (uiState.selectedCategoryFilter == cat)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onCategoryFilterChange(if (cat == "All") null else cat)
                        },
                        label = { Text(cat) }
                    )
                }
            }
        }

        // Filtered Total Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Filtered Expenses", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${uiState.currencySymbol}${totalFilteredExpense.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Filtered Income", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${uiState.currencySymbol}${totalFilteredIncome.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Transaction List
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions match the filter.")
                }
            }
        } else {
            items(filteredList) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            TransactionItem(
                                expense = expense,
                                currencySymbol = uiState.currencySymbol
                            )
                        }
                        IconButton(onClick = { expenseToDelete = expense }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    expenseToDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete '${expense.description}' (${uiState.currencySymbol}${expense.amount.toInt()})?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExpense(expense)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
