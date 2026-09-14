package com.example.aiexpensemanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aiexpensemanager.data.model.Expense
import com.example.aiexpensemanager.data.model.LearnedRule

/**
 * Main local SQLite Room database for AI Expense Manager.
 * Zero cloud syncing, zero remote calls — 100% offline and secure.
 */
@Database(
    entities = [Expense::class, LearnedRule::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun learnedRuleDao(): LearnedRuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_expense_manager.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
