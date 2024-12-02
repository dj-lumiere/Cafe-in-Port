package com.ssafy.cafe_in_port_client.data.model.dto

import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.ProductType
import com.ssafy.cafe_in_port_client.common.enums.Temperature

data class ShoppingCart(
    val menuId: Int,
    val menuType: ProductType = ProductType.FOOD,
    val temperature: Temperature = Temperature.HOT,
    val size: DrinkSize = DrinkSize.GRANDE,
    val menuDetailId: Int=0,
    val menuImg: String="",
    val menuName: String="",
    var menuCnt: Int=0,
    val menuPrice: Int=1000,
    var totalPrice: Int = menuCnt * menuPrice
) {
    fun addDupMenu(shoppingCart: ShoppingCart) {
        this.menuCnt += shoppingCart.menuCnt
        this.totalPrice = this.menuCnt * this.menuPrice
    }
}