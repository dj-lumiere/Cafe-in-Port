package com.ssafy.cafe_in_port_client.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel: ViewModel() {
    private val _homeProductId = MutableLiveData<Int>()
    val homeProductId: LiveData<Int>
        get() = _homeProductId

    fun setProductId(productId:Int){
        _homeProductId.value = productId
    }
}
