package com.example.mytasks

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Saves and loads the task list so it survives closing the app.
 *
 * SharedPreferences is a tiny key/value file Android gives every app for free.
 * The whole list is turned into one JSON string and stored under one key.
 */
class TaskStore(context: Context) {

    private val prefs = context.getSharedPreferences("my_tasks_data", Context.MODE_PRIVATE)

    /** Reads the saved tasks. Returns an empty list the first time the app runs. */
    fun load(): List<Task> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()

        return try {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val obj = array.getJSONObject(index)
                Task(
                    id = obj.getLong("id"),
                    text = obj.getString("text"),
                    done = obj.getBoolean("done")
                )
            }
        } catch (e: Exception) {
            // If the saved text is damaged, start fresh instead of crashing.
            emptyList()
        }
    }

    /** Writes the whole list, replacing whatever was stored before. */
    fun save(tasks: List<Task>) {
        val array = JSONArray()
        for (task in tasks) {
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("text", task.text)
            obj.put("done", task.done)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TASKS, array.toString()).apply()
    }

    /** Wipes every task. Used by the Delete all button in Settings. */
    fun clear() {
        prefs.edit().remove(KEY_TASKS).apply()
    }

    private companion object {
        const val KEY_TASKS = "tasks"
    }
}
