package com.ssafy.cafe_in_port_client.features.recent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.cafe_in_port_client.data.model.response.OrderResponse

class RecentOrderViewModel : ViewModel() {
    private val _orderList = MutableLiveData<List<OrderResponse>>()
    val orderList: LiveData<List<OrderResponse>> get() = _orderList

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    fun setOrderList(orders: List<OrderResponse>) {
        _orderList.value = orders
        _isEmpty.value = orders.isEmpty()
    }
}