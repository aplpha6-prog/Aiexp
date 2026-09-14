package com.example.aiexpensemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local learning rule entity.
 * Remembers user corrections (e.g., "Chaya" -> Food) without any cloud or ML model.
 */
@Entity(tableName = "learned_rules")
data class LearnedRule(
    @PrimaryKey
    val keyword: String, // Stored in lowercase
    val category: String,
    val type: TransactionType = TransactionType.Expense,
    val learnedAt: Long = System.currentTimeMillis()
)
