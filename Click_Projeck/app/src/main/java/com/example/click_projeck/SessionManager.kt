package com.example.click_projeck

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_EMAIL = "userEmail"
        const val KEY_USER_NAME = "userName"
        const val KEY_IS_ADMIN = "is_admin"


    }

    fun createSession(email: String, name: String,  isAdmin: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_NAME, name)
        editor.putBoolean(KEY_IS_ADMIN, isAdmin)

        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserDetails(): Map<String, String?> {
        val userDetails = HashMap<String, String?>()
        userDetails[KEY_USER_EMAIL] = sharedPreferences.getString(KEY_USER_EMAIL, null)
        userDetails[KEY_USER_NAME] = sharedPreferences.getString(KEY_USER_NAME, null)
        return userDetails
    }
    fun isAdmin(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
    }
    fun logout() {
        editor.clear()
        editor.apply()
    }
}
