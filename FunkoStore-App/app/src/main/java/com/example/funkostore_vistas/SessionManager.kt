package com.example.funkostore_vistas

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var userEmail: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var userRole: String?
        get() = prefs.getString(KEY_ROLE, null)
        set(value) = prefs.edit().putString(KEY_ROLE, value).apply()

    var userId: Int
        get() = prefs.getInt(KEY_USER_ID, -1)
        set(value) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    val isLoggedIn: Boolean get() = token != null

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "funko_store_session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_ROLE = "user_role"
        private const val KEY_USER_ID = "user_id"
    }
}
