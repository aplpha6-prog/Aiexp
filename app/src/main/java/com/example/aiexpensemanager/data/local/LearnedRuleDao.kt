package com.example.aiexpensemanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiexpensemanager.data.model.LearnedRule
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for storing personal learning mappings.
 * E.g., user teaches: "chaya" -> Food
 */
@Dao
interface LearnedRuleDao {

    @Query("SELECT * FROM learned_rules ORDER BY keyword ASC")
    fun getAllRules(): Flow<List<LearnedRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: LearnedRule)

    @Delete
    suspend fun deleteRule(rule: LearnedRule)

    @Query("DELETE FROM learned_rules WHERE keyword = :keyword")
    suspend fun deleteByKeyword(keyword: String)

    @Query("DELETE FROM learned_rules")
    suspend fun deleteAll()
}
