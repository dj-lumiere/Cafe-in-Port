package com.ssafy.cafe_in_port_client.data.model.dto

data class OrderDetail (
    var id: Int,
    var orderId: Int,
    var productDetailId: Int,
    var quantity: Int ) {

    var unitPrice:Int = 0
    var img:String = ""
    var productName:String = ""


    constructor(productDetailId: Int, quantity: Int) :this(0, 0, productDetailId, quantity)
    constructor():this(0,0)

}
