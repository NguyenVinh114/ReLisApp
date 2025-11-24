package com.example.relisapp.nam.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ROLE = "role"
    }

    fun saveLogin(userId: Int, role: String?) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun isLoggedIn(): Boolean =
        prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int =
        prefs.getInt(KEY_USER_ID, -1)

    fun getUserRole(): String? =
        prefs.getString(KEY_ROLE, null)

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)
}

