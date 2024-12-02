package com.ssafy.cafe_in_port_client.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.cafe_in_port_client.data.model.dto.ShoppingCart

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    // my 페이지에서 detail로 이동시 사용
    private val _myPageOrderId = MutableLiveData<Int>()
    val myPageOrderId: LiveData<Int>
        get() = _myPageOrderId

    val nfcId = MutableLiveData<String>()

    fun setOrderId(orderId: Int) {
        _myPageOrderId.value = orderId
    }

    private val _homeOrderID = MutableLiveData<Int>()

    val homeOrderID: LiveData<Int>
        get() = _homeOrderID

    fun setHomeOrderId(orderId: Int) {
        _homeOrderID.value = orderId
    }

    // 선택된 productId
    private val _productId = MutableLiveData<Int>()
    val productId: LiveData<Int>
        get() = _productId

    fun setProductId(productId: Int) {
        _productId.value = productId
    }
    private val _distance = MutableLiveData<Long>()
    val distance: LiveData<Long>
        get() = _distance

    fun setDistance(value: Long) {
        _distance.value = value
    }



    // 장바구니
    private val _shoppingList = MutableLiveData<MutableList<ShoppingCart>>(mutableListOf())
    val shoppingList: LiveData<MutableList<ShoppingCart>>
        get() = _shoppingList

    fun addShoppingList(shoppingCart: ShoppingCart) {
        Log.e(TAG, "addShoppingList: adding $shoppingCart")
        val position = checkDuplication(shoppingCart.menuDetailId)

        val currentList = _shoppingList.value ?: mutableListOf()
        if (position == -1) {
            currentList.add(shoppingCart)
        } else {
            currentList[position].addDupMenu(shoppingCart)
        }
        _shoppingList.value = currentList
    }

    fun editCount(idx: Int, delta: Int) {
        val currentList = _shoppingList.value ?: mutableListOf()
        if (idx >= currentList.size) {
            return
        }
        if (idx < 0) {
            return
        }
        val currentCount = currentList[idx].menuCnt
        if (currentCount + delta < 0) {
            return
        }
        if (currentCount + delta == 0) {
            removeShoppingList(idx)
        } else {
            currentList[idx].menuCnt = currentCount + delta
        }
        _shoppingList.value = currentList
    }

    fun removeShoppingList(idx: Int) {
        val currentList = _shoppingList.value ?: return
        if (currentList.size <= idx) {
            return
        }
        currentList.removeAt(idx)
        _shoppingList.value = currentList
    }

    fun clearShoppingList() {
        _shoppingList.value = mutableListOf()
    }

    private fun checkDuplication(menuDetailId: Int): Int {
        var position = -1
        _shoppingList.value?.forEachIndexed { index, item ->
            if (item.menuDetailId == menuDetailId) position = index
        }
        return position
    }

    private val _orderResponse = MutableLiveData<Boolean>()
    val orderResponse: LiveData<Boolean> get() = _orderResponse

    fun setOrderResponse(success: Boolean) {
        _orderResponse.value = success
    }
}