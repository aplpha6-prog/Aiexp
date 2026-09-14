package com.example.aiexpensemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room Entity storing individual expense and income items.
 * Lightweight and battery-friendly, stored 100% locally on device.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val type: TransactionType,
    val date: String, // Stored as YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)
