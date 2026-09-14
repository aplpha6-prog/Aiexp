package com.example.aiexpensemanager.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages user preferences such as monthly budget limit and currency symbol.
 * Completely lightweight and uses Android SharedPreferences.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "ai_expense_manager_prefs",
        Context.MODE_PRIVATE
    )

    private val _budgetLimit = MutableStateFlow(getBudgetLimit())
    val budgetLimit: StateFlow<Double> = _budgetLimit.asStateFlow()

    private val _budgetEnabled = MutableStateFlow(isBudgetEnabled())
    val budgetEnabled: StateFlow<Boolean> = _budgetEnabled.asStateFlow()

    private val _currencySymbol = MutableStateFlow(getCurrencySymbol())
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    fun getBudgetLimit(): Double = prefs.getFloat("monthly_budget", 25000f).toDouble()

    fun setBudgetLimit(limit: Double) {
        prefs.edit().putFloat("monthly_budget", limit.toFloat()).apply()
        _budgetLimit.value = limit
    }

    fun isBudgetEnabled(): Boolean = prefs.getBoolean("budget_enabled", true)

    fun setBudgetEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("budget_enabled", enabled).apply()
        _budgetEnabled.value = enabled
    }

    fun getCurrencySymbol(): String = prefs.getString("currency_symbol", "₹") ?: "₹"

    fun setCurrencySymbol(symbol: String) {
        prefs.edit().putString("currency_symbol", symbol).apply()
        _currencySymbol.value = symbol
    }
}
