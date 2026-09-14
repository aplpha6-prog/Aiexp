package com.example.aiexpensemanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local expense storage.
 * Reactive Flow queries provide real-time updates to the UI.
 */
@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int = 10): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date LIKE :yearMonth || '%' ORDER BY date DESC, createdAt DESC")
    fun getExpensesForMonth(yearMonth: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE type = :type AND date LIKE :yearMonth || '%'")
    fun getTotalForMonthAndType(yearMonth: String, type: TransactionType): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC, createdAt DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}
