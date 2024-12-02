package com.ssafy.cafe_in_port_client.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.ssafy.cafe_in_port_client.data.model.dto.User

private const val TAG = "SharedPreferencesUtil"

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "smartstore_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getLastShownTime(): Long {
        return preferences.getLong("getLastShownTime", 0)
    }

    fun setLastShownTime(lastShownTime: Long) {
        val editor = preferences.edit()
        editor.putLong("getLastShownTime", lastShownTime)
        editor.apply()
    }

    //사용자 정보 저장
    fun addUser(user: User) {
        Log.e(TAG, "addUser: $user")
        val editor = preferences.edit()
        editor.putInt("no", user.userNo)
        editor.putString("id", user.id)
        editor.putString("name", user.username)
        editor.putString("email", user.email)
        editor.apply()
    }

    fun getUser(): User {
        val id = preferences.getString("id", "")
        val email = preferences.getString("email", "")
        val no = preferences.getInt("no", 0)
        if (id != "") {
            val name = preferences.getString("name", "")
            return User(id!!, name!!, "XXXX", email ?: "").apply { userNo = no }
        } else {
            return User()
        }
    }

    fun deleteUser() {
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    fun addNotice(info: String) {
        val list = getNotice()

        list.add(info)
        val json = Gson().toJson(list)

        preferences.edit().let {
            it.putString("notice", json)
            it.apply()
        }
    }

    fun setNotice(list: MutableList<String>) {
        preferences.edit().let {
            it.putString("notice", Gson().toJson(list))
            it.apply()
        }
    }

    fun getNotice(): MutableList<String> {
        val str = preferences.getString("notice", "")!!
        val list = if (str.isEmpty()) mutableListOf<String>() else Gson().fromJson(
            str, MutableList::class.java
        ) as MutableList<String>

        return list
    }

}