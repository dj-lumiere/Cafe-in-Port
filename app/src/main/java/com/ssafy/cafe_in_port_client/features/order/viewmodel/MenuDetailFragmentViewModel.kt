package com.ssafy.cafe_in_port_client.features.order.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.data.model.dto.MenuDetailUiData
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithCommentResponse
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithDetailResponse
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MenuDetailFragmentVM_싸피"

class MenuDetailFragmentViewModel(private val handle: SavedStateHandle) : ViewModel() {
    private var _menuDetailUiData = MutableLiveData<MenuDetailUiData>(MenuDetailUiData())
    val menuDetailUiData: LiveData<MenuDetailUiData>
        get() = _menuDetailUiData

    var productId = handle.get<Int>("productId") ?: 0
        set(value) {
            handle["productId"] = value
            field = value
        }

    fun reset() {
        _menuDetailUiData.value = MenuDetailUiData()
    }

    fun getProductInfo() {
        getProductInfo(productId)
    }

    fun getProductDetails() {
        getProductDetails(productId)
    }

    fun getProductInfo(pId: Int) {
        Log.e(TAG, "getProductInfo: called")
        viewModelScope.launch {
            var info: ProductWithCommentResponse
            try {
                info = RetrofitUtil.productService.getProductWithComments(pId)
            } catch (e: Exception) {
                info = ProductWithCommentResponse()
            }
            Log.e(TAG, "getProductInfo: $info")
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(productInfo = info)
        }
    }

    fun getProductDetails(pId: Int) {
        viewModelScope.launch {
            var info: List<ProductWithDetailResponse>
            try {
                info = RetrofitUtil.productService.getProductWithDetail(pId)
            } catch (e: Exception) {
                info = listOf()
                Log.e(TAG, "getProductDetails: $e")
            }
            Log.e(TAG, "getProductDetails: $info")
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(productDetails = info)
        }
    }

    fun setDrinkSize(value: DrinkSize) {
        if (_menuDetailUiData.value?.drinkSize != value) {
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(drinkSize = value)
            getProductDetailIdAndPrice()
        }
    }

    fun setTemperature(value: Temperature) {
        if (_menuDetailUiData.value?.temperature != value) {
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(temperature = value)
            getProductDetailIdAndPrice()
        }
    }

    fun setProductDetailId(value: Int) {
        if (_menuDetailUiData.value?.productDetailId != value) {
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(productDetailId = value)
        }
    }

    fun getProductDetailIdAndPrice() {
        Log.e(TAG, "getProductDetailIdAndPrice: called")
        val currentTemp = _menuDetailUiData.value?.temperature
        val currentSize = _menuDetailUiData.value?.drinkSize

        val matchingDetail = _menuDetailUiData.value?.productDetails?.find {
            it.temperature == currentTemp && it.size == currentSize
        }

        if (matchingDetail != null) {
            setProductDetailId(matchingDetail.detailId)
            _menuDetailUiData.value =
                _menuDetailUiData.value?.copy(unitPrice = matchingDetail.price)
            Log.d(
                TAG,
                "setProductDetailId: ${matchingDetail.detailId}, price: ${matchingDetail.price}"
            )
        } else {
            setProductDetailId(0)
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(unitPrice = 0)
        }
    }

    fun setCount(value: Int) {
        if (_menuDetailUiData.value?.count != value) {
            _menuDetailUiData.value = _menuDetailUiData.value?.copy(count = value)
        }
    }
}