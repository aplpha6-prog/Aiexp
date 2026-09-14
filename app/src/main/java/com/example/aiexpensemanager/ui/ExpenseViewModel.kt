package com.example.aiexpensemanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiexpensemanager.data.local.AppDatabase
import com.example.aiexpensemanager.data.local.PreferencesManager
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.LearnedRule
import com.example.aiexpensemanager.data.model.TransactionType
import com.example.aiexpensemanager.data.repository.ExpenseRepository
import com.example.aiexpensemanager.parser.LocalExpenseParser
import com.example.aiexpensemanager.parser.ParsedExpenseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * UI State container for AI Expense Manager.
 */
data class ExpenseUiState(
    val currentYearMonth: String = "",
    val currentMonthDisplayName: String = "",
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val balance: Double = 0.0,
    val budgetLimit: Double = 25000.0,
    val isBudgetEnabled: Boolean = true,
    val currencySymbol: String = "₹",
    val recentExpenses: List<Expense> = emptyList(),
    val allExpenses: List<Expense> = emptyList(),
    val learnedRules: List<LearnedRule> = emptyList(),
    val previewExpense: ParsedExpenseResult? = null,
    val isParsing: Boolean = false,
    val naturalInputText: String = "",
    val selectedCategoryFilter: String? = null,
    val selectedTypeFilter: TransactionType? = null
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefs = PreferencesManager(application)
    private val repository = ExpenseRepository(
        expenseDao = db.expenseDao(),
        learnedRuleDao = db.learnedRuleDao(),
        parser = LocalExpenseParser()
    )

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        val sdfMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val sdfDisplay = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val now = Date()
        val currentYearMonth = sdfMonth.format(now)
        val monthDisplay = sdfDisplay.format(now)

        _uiState.value = _uiState.value.copy(
            currentYearMonth = currentYearMonth,
            currentMonthDisplayName = monthDisplay
        )

        // Observe monthly transactions
        viewModelScope.launch {
            repository.getExpensesForMonth(currentYearMonth).collect { monthlyList ->
                val totalExp = monthlyList.filter { it.type == TransactionType.Expense }.sumOf { it.amount }
                val totalInc = monthlyList.filter { it.type == TransactionType.Income }.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(
                    totalExpense = totalExp,
                    totalIncome = totalInc,
                    balance = totalInc - totalExp
                )
            }
        }

        // Observe recent expenses
        viewModelScope.launch {
            repository.getRecentExpenses(10).collect { recents ->
                _uiState.value = _uiState.value.copy(recentExpenses = recents)
            }
        }

        // Observe all expenses
        viewModelScope.launch {
            repository.getAllExpenses().collect { all ->
                _uiState.value = _uiState.value.copy(allExpenses = all)
            }
        }

        // Observe learned rules
        viewModelScope.launch {
            repository.getLearnedRules().collect { rules ->
                _uiState.value = _uiState.value.copy(learnedRules = rules)
            }
        }

        // Observe preferences
        viewModelScope.launch {
            combine(prefs.budgetLimit, prefs.budgetEnabled, prefs.currencySymbol) { limit, enabled, symbol ->
                Triple(limit, enabled, symbol)
            }.collect { (limit, enabled, symbol) ->
                _uiState.value = _uiState.value.copy(
                    budgetLimit = limit,
                    isBudgetEnabled = enabled,
                    currencySymbol = symbol
                )
            }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.value = _uiState.value.copy(naturalInputText = text)
    }

    fun parseNaturalInput(text: String = _uiState.value.naturalInputText) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isParsing = true)
            val result = repository.parseExpenseText(text)
            _uiState.value = _uiState.value.copy(
                previewExpense = result,
                isParsing = false
            )
        }
    }

    fun dismissPreview() {
        _uiState.value = _uiState.value.copy(previewExpense = null)
    }

    fun saveParsedExpense(
        amount: Double,
        category: String,
        description: String,
        type: TransactionType,
        date: String,
        originalDetectedCategory: String? = null
    ) {
        viewModelScope.launch {
            val expense = Expense(
                amount = amount,
                category = category,
                description = description,
                type = type,
                date = date
            )
            repository.saveExpense(expense)

            // Personal Learning:
            // If the user modified the category from what was originally detected, learn it!
            if (originalDetectedCategory != null && originalDetectedCategory != category) {
                // Extract clean keyword from description
                val keyword = description.trim().split(" ").firstOrNull()?.lowercase()
                if (!keyword.isNullOrBlank()) {
                    repository.learnKeywordMapping(keyword, category, type)
                }
            }

            _uiState.value = _uiState.value.copy(
                previewExpense = null,
                naturalInputText = ""
            )
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun setBudget(limit: Double, enabled: Boolean) {
        prefs.setBudgetLimit(limit)
        prefs.setBudgetEnabled(enabled)
    }

    fun setCurrencySymbol(symbol: String) {
        prefs.setCurrencySymbol(symbol)
    }

    fun deleteLearnedRule(rule: LearnedRule) {
        viewModelScope.launch {
            repository.deleteLearnedRule(rule)
        }
    }

    fun addLearnedRule(keyword: String, category: String, type: TransactionType) {
        viewModelScope.launch {
            repository.learnKeywordMapping(keyword, category, type)
        }
    }

    fun setCategoryFilter(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryFilter = category)
    }

    fun setTypeFilter(type: TransactionType?) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = type)
    }
}
