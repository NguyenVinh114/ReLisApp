package com.example.relisapp.nam.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }

    // Lưu trạng thái đăng nhập + ID user
    fun saveLogin(userId: Int) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    // Kiểm tra đã đăng nhập chưa
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Lấy ID user (dùng cho Repository → Database)
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // Xoá toàn bộ session (logout)
    fun logout() {
        prefs.edit().clear().apply()
    }
}
