package com.example.mytasks

/**
 * One item in the list.
 *
 * "data class" is Kotlin shorthand: it writes equals(), hashCode(), toString()
 * and copy() for you, which is all a simple holder of values needs.
 */
data class Task(
    val id: Long,
    val text: String,
    val done: Boolean = false
)
