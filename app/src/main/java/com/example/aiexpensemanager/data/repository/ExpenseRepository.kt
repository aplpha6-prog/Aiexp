package com.example.aiexpensemanager.data.repository

import com.example.aiexpensemanager.data.local.ExpenseDao
import com.example.aiexpensemanager.data.local.LearnedRuleDao
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.LearnedRule
import com.example.aiexpensemanager.data.model.TransactionType
import com.example.aiexpensemanager.parser.ExpenseParser
import com.example.aiexpensemanager.parser.ParsedExpenseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Single source of truth for expenses and learning rules.
 */
class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val learnedRuleDao: LearnedRuleDao,
    private val parser: ExpenseParser
) {

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getRecentExpenses(limit: Int = 10): Flow<List<Expense>> = expenseDao.getRecentExpenses(limit)

    fun getExpensesForMonth(yearMonth: String): Flow<List<Expense>> = expenseDao.getExpensesForMonth(yearMonth)

    fun getTotalForMonth(yearMonth: String, type: TransactionType): Flow<Double?> =
        expenseDao.getTotalForMonthAndType(yearMonth, type)

    fun getLearnedRules(): Flow<List<LearnedRule>> = learnedRuleDao.getAllRules()

    suspend fun parseExpenseText(text: String): ParsedExpenseResult {
        val currentRules = learnedRuleDao.getAllRules().first()
        return parser.parse(text, currentRules)
    }

    suspend fun saveExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteById(id: Long) = expenseDao.deleteById(id)

    suspend fun learnKeywordMapping(keyword: String, category: String, type: TransactionType) {
        val cleanKeyword = keyword.trim().lowercase()
        if (cleanKeyword.isNotEmpty()) {
            learnedRuleDao.insertRule(
                LearnedRule(
                    keyword = cleanKeyword,
                    category = category,
                    type = type
                )
            )
        }
    }

    suspend fun deleteLearnedRule(rule: LearnedRule) = learnedRuleDao.deleteRule(rule)
}
