package com.example.aiexpensemanager.parser

import com.example.aiexpensemanager.data.model.LearnedRule
import com.example.aiexpensemanager.data.model.TransactionType

/**
 * Data container for parsed natural-language expense output.
 */
data class ParsedExpenseResult(
    val amount: Double,
    val category: String,
    val description: String,
    val type: TransactionType,
    val date: String,
    val matchedKeyword: String? = null,
    val isLearnedRule: Boolean = false
)

/**
 * Extensible interface for parsing natural language into structured expense data.
 * Currently implemented by LocalExpenseParser (offline, fast, rule-based).
 * In the future, GeminiExpenseParser or NvidiaExpenseParser can be slotted in seamlessly.
 */
interface ExpenseParser {
    suspend fun parse(
        text: String,
        customRules: List<LearnedRule> = emptyList()
    ): ParsedExpenseResult
}
