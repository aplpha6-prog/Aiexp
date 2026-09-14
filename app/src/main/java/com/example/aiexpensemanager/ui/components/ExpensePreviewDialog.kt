package com.example.aiexpensemanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aiexpensemanager.data.model.TransactionType
import com.example.aiexpensemanager.parser.ParsedExpenseResult

/**
 * Modal dialog matching prompt specification:
 * Detected Expense
 * Amount: ₹500
 * Category: Transport
 * Description: Petrol
 * Type: Expense
 * [ Edit ] [ Save ]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensePreviewDialog(
    preview: ParsedExpenseResult,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, description: String, type: TransactionType, date: String, originalCategory: String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editAmount by remember { mutableStateOf(preview.amount.toInt().toString()) }
    var editCategory by remember { mutableStateOf(preview.category) }
    var editDescription by remember { mutableStateOf(preview.description) }
    var editType by remember { mutableStateOf(preview.type) }
    var editDate by remember { mutableStateOf(preview.date) }

    val categories = listOf(
        "Food", "Transport", "Bills", "Shopping", "Health",
        "Rent", "Entertainment", "Family", "Education", "Other", "Income"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detected Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (preview.isLearnedRule) {
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text("Learned Rule", modifier = Modifier.padding(2.dp))
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isEditing) {
                    // Read-only Clean Preview
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$currencySymbol${preview.amount.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Category", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(preview.category, fontWeight = FontWeight.SemiBold)
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Description", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(preview.description, fontWeight = FontWeight.SemiBold)
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Type", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    preview.type.name,
                                    color = if (preview.type == TransactionType.Income)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(preview.date)
                            }
                        }
                    }
                } else {
                    // Editable Form for Corrections
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        label = { Text("Amount ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = editCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        editCategory = cat
                                        if (cat == "Income") {
                                            editType = TransactionType.Income
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Transaction Type toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = editType == TransactionType.Expense,
                            onClick = { editType = TransactionType.Expense },
                            label = { Text("Expense") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = editType == TransactionType.Income,
                            onClick = { editType = TransactionType.Income },
                            label = { Text("Income") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalAmount = editAmount.toDoubleOrNull() ?: preview.amount
                    onSave(
                        finalAmount,
                        editCategory,
                        editDescription,
                        editType,
                        editDate,
                        preview.category
                    )
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { isEditing = !isEditing }) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (isEditing) "Preview" else "Edit")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
