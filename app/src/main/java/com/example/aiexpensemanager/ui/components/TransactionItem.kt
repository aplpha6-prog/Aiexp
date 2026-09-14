package com.example.aiexpensemanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.TransactionType

@Composable
fun TransactionItem(
    expense: Expense,
    currencySymbol: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = getCategoryColor(expense.category).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getCategoryIcon(expense.category),
                            contentDescription = expense.category,
                            tint = getCategoryColor(expense.category),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${expense.category} • ${expense.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = (if (expense.type == TransactionType.Income) "+ " else "") +
                        "$currencySymbol${expense.amount.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (expense.type == TransactionType.Income)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Default.Restaurant
        "transport" -> Icons.Default.DirectionsCar
        "bills" -> Icons.Default.Receipt
        "shopping" -> Icons.Default.ShoppingBag
        "health" -> Icons.Default.LocalHospital
        "rent" -> Icons.Default.Home
        "entertainment" -> Icons.Default.Movie
        "family" -> Icons.Default.People
        "education" -> Icons.Default.School
        "income" -> Icons.Default.ArrowDownward
        else -> Icons.Default.MoreHoriz
    }
}

@Composable
fun getCategoryColor(category: String): androidx.compose.ui.graphics.Color {
    return when (category.lowercase()) {
        "food" -> androidx.compose.ui.graphics.Color(0xFFE65100)
        "transport" -> androidx.compose.ui.graphics.Color(0xFF1565C0)
        "bills" -> androidx.compose.ui.graphics.Color(0xFFF57C00)
        "shopping" -> androidx.compose.ui.graphics.Color(0xFF7B1FA2)
        "health" -> androidx.compose.ui.graphics.Color(0xFFC2185B)
        "rent" -> androidx.compose.ui.graphics.Color(0xFF303F9F)
        "entertainment" -> androidx.compose.ui.graphics.Color(0xFFC51162)
        "income" -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.secondary
    }
}
