package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: String = "SHOULD DO", // "MUST DO", "SHOULD DO", "CAN DO", "WAITING FOR"
    val priority: String = "Medium", // "Low", "Medium", "High"
    val status: String = "Pending", // "Pending", "Started", "Completed", "Blocked"
    val estimatedMinutes: Int = 30,
    val actualMinutes: Int = 0,
    val deadline: String = "Today", // "Today", "Tomorrow", "Thursday", etc.
    val postponedCount: Int = 0,
    val dependency: String? = null, // e.g., "Rahul's sales numbers"
    val notes: String? = null,
    val subtasksJson: String = "[]", // JSON serialized List<Subtask>
    val confidenceJson: String = "{}" // JSON serialized Map<String, Int> for AI confidence
) {
    val isCompleted: Boolean get() = status == "Completed"
    val isBlocked: Boolean get() = status == "Blocked"

    // Helper to get subtasks using Moshi
    fun getSubtasks(): List<Subtask> {
        return try {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val listType = Types.newParameterizedType(List::class.java, Subtask::class.java)
            val adapter = moshi.adapter<List<Subtask>>(listType)
            adapter.fromJson(subtasksJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Helper to get AI Confidence values
    fun getConfidence(): AIConfidence {
        return try {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(AIConfidence::class.java)
            adapter.fromJson(confidenceJson) ?: AIConfidence()
        } catch (e: Exception) {
            AIConfidence()
        }
    }
}

data class Subtask(
    val title: String,
    val isCompleted: Boolean = false
)

data class AIConfidence(
    val deadlineConfidence: Int = 85,
    val effortConfidence: Int = 75,
    val dependencyConfidence: Int = 90
)
