package com.example.aiexpensemanager.parser

import com.example.aiexpensemanager.data.model.LearnedRule
import com.example.aiexpensemanager.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * Highly optimized, offline-first natural language expense parser.
 * Extracts: Amount (500, Rs 500, 1.5k, etc.), Category, Description, Type, and Date.
 * Prioritizes user's personal learned rules before built-in categories.
 */
class LocalExpenseParser : ExpenseParser {

    companion object {
        // Built-in keyword mappings for fast local categorization
        val DEFAULT_CATEGORIES: Map<String, Pair<String, TransactionType>> = mapOf(
            // Food & Dining & Groceries
            "tea" to Pair("Food", TransactionType.Expense),
            "chai" to Pair("Food", TransactionType.Expense),
            "chaya" to Pair("Food", TransactionType.Expense),
            "cha" to Pair("Food", TransactionType.Expense),
            "coffee" to Pair("Food", TransactionType.Expense),
            "kaapi" to Pair("Food", TransactionType.Expense),
            "breakfast" to Pair("Food", TransactionType.Expense),
            "lunch" to Pair("Food", TransactionType.Expense),
            "dinner" to Pair("Food", TransactionType.Expense),
            "tiffin" to Pair("Food", TransactionType.Expense),
            "meals" to Pair("Food", TransactionType.Expense),
            "thali" to Pair("Food", TransactionType.Expense),
            "food" to Pair("Food", TransactionType.Expense),
            "snacks" to Pair("Food", TransactionType.Expense),
            "biriyani" to Pair("Food", TransactionType.Expense),
            "biryani" to Pair("Food", TransactionType.Expense),
            "dosa" to Pair("Food", TransactionType.Expense),
            "idli" to Pair("Food", TransactionType.Expense),
            "vada" to Pair("Food", TransactionType.Expense),
            "samosa" to Pair("Food", TransactionType.Expense),
            "puff" to Pair("Food", TransactionType.Expense),
            "roti" to Pair("Food", TransactionType.Expense),
            "chapati" to Pair("Food", TransactionType.Expense),
            "paneer" to Pair("Food", TransactionType.Expense),
            "chicken" to Pair("Food", TransactionType.Expense),
            "mutton" to Pair("Food", TransactionType.Expense),
            "fish" to Pair("Food", TransactionType.Expense),
            "egg" to Pair("Food", TransactionType.Expense),
            "shawarma" to Pair("Food", TransactionType.Expense),
            "burger" to Pair("Food", TransactionType.Expense),
            "pizza" to Pair("Food", TransactionType.Expense),
            "sandwich" to Pair("Food", TransactionType.Expense),
            "swiggy" to Pair("Food", TransactionType.Expense),
            "zomato" to Pair("Food", TransactionType.Expense),
            "zepto" to Pair("Food", TransactionType.Expense),
            "blinkit" to Pair("Food", TransactionType.Expense),
            "groceries" to Pair("Food", TransactionType.Expense),
            "grocery" to Pair("Food", TransactionType.Expense),
            "vegetables" to Pair("Food", TransactionType.Expense),
            "fruits" to Pair("Food", TransactionType.Expense),
            "milk" to Pair("Food", TransactionType.Expense),
            "curd" to Pair("Food", TransactionType.Expense),
            "sweets" to Pair("Food", TransactionType.Expense),
            "ice cream" to Pair("Food", TransactionType.Expense),
            "restaurant" to Pair("Food", TransactionType.Expense),
            "hotel" to Pair("Food", TransactionType.Expense),
            "cafe" to Pair("Food", TransactionType.Expense),

            // Transport & Travel
            "petrol" to Pair("Transport", TransactionType.Expense),
            "diesel" to Pair("Transport", TransactionType.Expense),
            "cng" to Pair("Transport", TransactionType.Expense),
            "fuel" to Pair("Transport", TransactionType.Expense),
            "uber" to Pair("Transport", TransactionType.Expense),
            "ola" to Pair("Transport", TransactionType.Expense),
            "rapido" to Pair("Transport", TransactionType.Expense),
            "auto" to Pair("Transport", TransactionType.Expense),
            "rickshaw" to Pair("Transport", TransactionType.Expense),
            "taxi" to Pair("Transport", TransactionType.Expense),
            "cab" to Pair("Transport", TransactionType.Expense),
            "bus" to Pair("Transport", TransactionType.Expense),
            "train" to Pair("Transport", TransactionType.Expense),
            "metro" to Pair("Transport", TransactionType.Expense),
            "flight" to Pair("Transport", TransactionType.Expense),
            "toll" to Pair("Transport", TransactionType.Expense),
            "fastag" to Pair("Transport", TransactionType.Expense),
            "parking" to Pair("Transport", TransactionType.Expense),
            "puncture" to Pair("Transport", TransactionType.Expense),
            "mechanic" to Pair("Transport", TransactionType.Expense),

            // Bills & Utilities
            "electricity" to Pair("Bills", TransactionType.Expense),
            "current bill" to Pair("Bills", TransactionType.Expense),
            "eb" to Pair("Bills", TransactionType.Expense),
            "eb bill" to Pair("Bills", TransactionType.Expense),
            "kseb" to Pair("Bills", TransactionType.Expense),
            "water" to Pair("Bills", TransactionType.Expense),
            "internet" to Pair("Bills", TransactionType.Expense),
            "wifi" to Pair("Bills", TransactionType.Expense),
            "mobile bill" to Pair("Bills", TransactionType.Expense),
            "mobile" to Pair("Bills", TransactionType.Expense),
            "recharge" to Pair("Bills", TransactionType.Expense),
            "gas" to Pair("Bills", TransactionType.Expense),
            "cylinder" to Pair("Bills", TransactionType.Expense),
            "broadband" to Pair("Bills", TransactionType.Expense),
            "dth" to Pair("Bills", TransactionType.Expense),
            "maintenance" to Pair("Bills", TransactionType.Expense),
            "maid" to Pair("Bills", TransactionType.Expense),

            // Shopping & Lifestyle
            "amazon" to Pair("Shopping", TransactionType.Expense),
            "flipkart" to Pair("Shopping", TransactionType.Expense),
            "myntra" to Pair("Shopping", TransactionType.Expense),
            "meesho" to Pair("Shopping", TransactionType.Expense),
            "shopping" to Pair("Shopping", TransactionType.Expense),
            "clothes" to Pair("Shopping", TransactionType.Expense),
            "shoes" to Pair("Shopping", TransactionType.Expense),
            "shirt" to Pair("Shopping", TransactionType.Expense),
            "jeans" to Pair("Shopping", TransactionType.Expense),
            "dress" to Pair("Shopping", TransactionType.Expense),
            "phone" to Pair("Shopping", TransactionType.Expense),
            "electronics" to Pair("Shopping", TransactionType.Expense),

            // Health & Medical
            "medicine" to Pair("Health", TransactionType.Expense),
            "medicines" to Pair("Health", TransactionType.Expense),
            "doctor" to Pair("Health", TransactionType.Expense),
            "hospital" to Pair("Health", TransactionType.Expense),
            "clinic" to Pair("Health", TransactionType.Expense),
            "pharmacy" to Pair("Health", TransactionType.Expense),
            "tablets" to Pair("Health", TransactionType.Expense),
            "gym" to Pair("Health", TransactionType.Expense),
            "gym fee" to Pair("Health", TransactionType.Expense),

            // Rent & Housing
            "rent" to Pair("Rent", TransactionType.Expense),
            "room rent" to Pair("Rent", TransactionType.Expense),
            "house rent" to Pair("Rent", TransactionType.Expense),
            "hostel" to Pair("Rent", TransactionType.Expense),
            "pg" to Pair("Rent", TransactionType.Expense),
            "deposit" to Pair("Rent", TransactionType.Expense),

            // Entertainment & Leisure
            "movie" to Pair("Entertainment", TransactionType.Expense),
            "movies" to Pair("Entertainment", TransactionType.Expense),
            "netflix" to Pair("Entertainment", TransactionType.Expense),
            "cinema" to Pair("Entertainment", TransactionType.Expense),
            "game" to Pair("Entertainment", TransactionType.Expense),
            "hotstar" to Pair("Entertainment", TransactionType.Expense),
            "spotify" to Pair("Entertainment", TransactionType.Expense),
            "trip" to Pair("Entertainment", TransactionType.Expense),

            // Family & Personal
            "family" to Pair("Family", TransactionType.Expense),
            "mom" to Pair("Family", TransactionType.Expense),
            "dad" to Pair("Family", TransactionType.Expense),
            "gift" to Pair("Family", TransactionType.Expense),
            "temple" to Pair("Family", TransactionType.Expense),
            "pooja" to Pair("Family", TransactionType.Expense),
            "emi" to Pair("Family", TransactionType.Expense),

            // Education
            "education" to Pair("Education", TransactionType.Expense),
            "books" to Pair("Education", TransactionType.Expense),
            "book" to Pair("Education", TransactionType.Expense),
            "course" to Pair("Education", TransactionType.Expense),
            "college" to Pair("Education", TransactionType.Expense),
            "tuition" to Pair("Education", TransactionType.Expense),
            "fees" to Pair("Education", TransactionType.Expense),

            // Income
            "salary" to Pair("Income", TransactionType.Income),
            "received salary" to Pair("Income", TransactionType.Income),
            "income" to Pair("Income", TransactionType.Income),
            "freelance payment" to Pair("Income", TransactionType.Income),
            "freelance" to Pair("Income", TransactionType.Income),
            "stipend" to Pair("Income", TransactionType.Income),
            "bonus" to Pair("Income", TransactionType.Income),
            "cashback" to Pair("Income", TransactionType.Income),
            "dividend" to Pair("Income", TransactionType.Income),
            "received" to Pair("Income", TransactionType.Income),
            "credited" to Pair("Income", TransactionType.Income),
            "refund" to Pair("Income", TransactionType.Income)
        )

        val STOP_WORDS = setOf(
            "had", "paid", "bought", "got", "spent", "for", "on", "a", "an", "the",
            "to", "at", "in", "of", "and", "my", "yesterday", "today", "rs", "rupees", "inr", "rupee"
        )
    }

    override suspend fun parse(
        text: String,
        customRules: List<LearnedRule>
    ): ParsedExpenseResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ParsedExpenseResult(
                amount = 0.0,
                category = "Other",
                description = "Expense",
                type = TransactionType.Expense,
                date = getTodayDate()
            )
        }

        // 1. Extract Amount
        val (amount, textWithoutAmount) = extractAmount(trimmed)

        // 2. Detect Category & Type (Personal Learning check first)
        val (category, type, matchedKeyword, isLearned) = detectCategoryAndType(textWithoutAmount, customRules)

        // 3. Extract Clean Description
        val description = extractDescription(textWithoutAmount, matchedKeyword)

        // 4. Extract Date
        val date = extractDate(trimmed)

        return ParsedExpenseResult(
            amount = amount,
            category = category,
            description = description.ifEmpty { if (category != "Other") category else "Expense" },
            type = type,
            date = date,
            matchedKeyword = matchedKeyword,
            isLearnedRule = isLearned
        )
    }

    private fun extractAmount(text: String): Pair<Double, String> {
        var cleaned = text

        // Check for 'k' notation (e.g., 1.5k, 2k, Rs 2k, ₹1.5k)
        val kPattern = Pattern.compile("(?i)(?:₹|rs\.?|inr)?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*k\\b")
        val kMatcher = kPattern.matcher(text)
        if (kMatcher.find()) {
            val numStr = kMatcher.group(1) ?: "0"
            val value = (numStr.toDoubleOrNull() ?: 0.0) * 1000
            cleaned = cleaned.replace(kMatcher.group(0) ?: "", " ")
            return Pair(value, cleaned)
        }

        // Check for currency prefixes (e.g., ₹500, Rs 500, Rs. 1,200)
        val prefixPattern = Pattern.compile("(?i)(?:₹|rs\.?|inr)\\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]+)?|[0-9]+(?:\\.[0-9]+)?)")
        val prefixMatcher = prefixPattern.matcher(text)
        if (prefixMatcher.find()) {
            val numStr = (prefixMatcher.group(1) ?: "0").replace(",", "")
            val value = numStr.toDoubleOrNull() ?: 0.0
            cleaned = cleaned.replace(prefixMatcher.group(0) ?: "", " ")
            return Pair(value, cleaned)
        }

        // Check for currency suffixes (e.g., 500 rupees, 500 rs, 500/-)
        val suffixPattern = Pattern.compile("(?i)([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]+)?|[0-9]+(?:\\.[0-9]+)?)\\s*(?:rupees|rupee|rs|inr|/-)")
        val suffixMatcher = suffixPattern.matcher(text)
        if (suffixMatcher.find()) {
            val numStr = (suffixMatcher.group(1) ?: "0").replace(",", "")
            val value = numStr.toDoubleOrNull() ?: 0.0
            cleaned = cleaned.replace(suffixMatcher.group(0) ?: "", " ")
            return Pair(value, cleaned)
        }

        // Standalone numbers (e.g., "Tea 20", "Petrol 500", "1200")
        val numberPattern = Pattern.compile("\\b([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]+)?|[0-9]+(?:\\.[0-9]+)?)\\b")
        val numberMatcher = numberPattern.matcher(text)
        if (numberMatcher.find()) {
            val numStr = (numberMatcher.group(1) ?: "0").replace(",", "")
            val value = numStr.toDoubleOrNull() ?: 0.0
            cleaned = cleaned.replace(numberMatcher.group(0) ?: "", " ")
            return Pair(value, cleaned)
        }

        return Pair(0.0, cleaned)
    }

    private fun detectCategoryAndType(
        text: String,
        customRules: List<LearnedRule>
    ): Quadruple<String, TransactionType, String?, Boolean> {
        val lower = text.lowercase(Locale.getDefault())

        // 1. Check custom user-learned rules first (sorted by keyword length descending)
        val sortedLearned = customRules.sortedByDescending { it.keyword.length }
        for (rule in sortedLearned) {
            val kw = rule.keyword.trim().lowercase(Locale.getDefault())
            if (kw.isNotEmpty() && containsKeyword(lower, kw)) {
                return Quadruple(rule.category, rule.type, rule.keyword, true)
            }
        }

        // 2. Check built-in categories (sorted by keyword length descending)
        val sortedBuiltIn = DEFAULT_CATEGORIES.keys.sortedByDescending { it.length }
        for (kw in sortedBuiltIn) {
            if (containsKeyword(lower, kw)) {
                val match = DEFAULT_CATEGORIES[kw]!!
                return Quadruple(match.first, match.second, kw, false)
            }
        }

        // Fallback check for common income verbs
        if (lower.contains("received") || lower.contains("credited") || lower.contains("earned")) {
            return Quadruple("Income", TransactionType.Income, "income", false)
        }

        return Quadruple("Other", TransactionType.Expense, null, false)
    }

    private fun containsKeyword(text: String, keyword: String): Boolean {
        val regex = ("(^|\\W)" + Pattern.quote(keyword) + "(\\W|$)").toRegex(RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(text)
    }

    private fun extractDescription(textWithoutAmount: String, matchedKeyword: String?): String {
        val words = textWithoutAmount
            .replace(Regex("[^a-zA-Z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() && !STOP_WORDS.contains(it.lowercase(Locale.getDefault())) }

        if (words.isNotEmpty()) {
            val result = words.joinToString(" ")
            return result.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        if (matchedKeyword != null) {
            return matchedKeyword.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        return "Expense"
    }

    private fun extractDate(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        val cal = Calendar.getInstance()
        if (lower.contains("yesterday")) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
