package com.ssafy.cafe_in_port_client.features.order.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.ssafy.cafe_in_port_client.data.model.dto.Product
import com.ssafy.cafe_in_port_client.data.model.dto.Store
import com.ssafy.cafe_in_port_client.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _fullProductList = MutableLiveData<List<Product>>()
    val fullProductList: LiveData<List<Product>> get() = _fullProductList

    private val _filteredProductList = MutableLiveData<List<Product>>()
    val filteredProductList: LiveData<List<Product>> get() = _filteredProductList

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private val _nearestStore = MutableLiveData<Store>()
    val nearestStore: LiveData<Store> get() = _nearestStore

    fun fetchProducts() {
        viewModelScope.launch {
            runCatching {
                setFullProductList(RetrofitUtil.productService.getProductList())
                _isEmpty.value = fullProductList.value?.isEmpty() ?: true
            }.onFailure {
                _isEmpty.value = true
                setFullProductList(listOf())
            }
        }
    }

    fun setFullProductList(products: List<Product>) {
        _fullProductList.value = products
        _filteredProductList.value = products
    }

    fun setFilteredProductList(products: List<Product>) {
        _filteredProductList.value = products
    }

    fun filterProducts(query: String?) {
        val list = _fullProductList.value
        if (list != null) {
            if (query.isNullOrEmpty()) {
                setFilteredProductList(list)
            } else {
                setFilteredProductList(list.filter { product ->
                    product.name.contains(query, ignoreCase = true)
                })
            }
            _isEmpty.value = _filteredProductList.value.isNullOrEmpty()
        }
    }

    fun setNearestStore(store: Store) {
        _nearestStore.value = store
    }
}