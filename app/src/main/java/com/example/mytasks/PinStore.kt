package com.example.mytasks

import android.content.Context
import java.security.MessageDigest

/**
 * Stores the app's PIN.
 *
 * The PIN itself is never written to the phone. Instead it is run through
 * SHA-256, a one-way function: the same input always gives the same 64-character
 * output, but there is no way to turn that output back into the PIN.
 *
 * To check a PIN later, hash whatever was typed and compare the two hashes.
 *
 * Worth knowing: this stops someone reading the PIN out of the app's files.
 * It does not encrypt your tasks, and a 4-digit PIN only has 10,000 possible
 * values, so this is a door lock, not a safe.
 */
class PinStore(context: Context) {

    private val prefs = context.getSharedPreferences("my_tasks_lock", Context.MODE_PRIVATE)

    /** True once a PIN has been set, so the app knows to ask instead of create. */
    fun hasPin(): Boolean = prefs.contains(KEY_PIN_HASH)

    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hash(pin)).apply()
    }

    /** Compares the typed PIN against the stored hash. */
    fun checkPin(pin: String): Boolean {
        val saved = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return saved == hash(pin)
    }

    /** Turns "1234" into a fixed-length fingerprint that cannot be reversed. */
    private fun hash(pin: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(pin.toByteArray(Charsets.UTF_8))

        // Each byte becomes two hex characters, e.g. 0F, A3 - so 32 bytes = 64 chars.
        return bytes.joinToString("") { byte -> "%02x".format(byte) }
    }

    private companion object {
        const val KEY_PIN_HASH = "pin_hash"
    }
}
