package com.ssafy.cafe_in_port_client.features.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private var _userNo = MutableLiveData<Int>()
    val userNo: LiveData<Int>
        get() = _userNo

    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId

    fun setUserNo(userNo: Int) {
        _userNo.value = userNo
    }

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun login(id: String, pass: String) {
        viewModelScope.launch {
            runCatching {
                val user = RetrofitUtil.userService.login(User(id, pass)).body()!!
                _user.value = user
            }.onSuccess {
            }.onFailure {
                _user.value = User()
            }
        }
    }

}