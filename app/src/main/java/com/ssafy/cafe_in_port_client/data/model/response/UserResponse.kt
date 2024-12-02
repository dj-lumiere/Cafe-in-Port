package com.ssafy.cafe_in_port_client.data.model.response

import com.ssafy.cafe_in_port_client.data.model.dto.Grade
import com.ssafy.cafe_in_port_client.data.model.dto.Order
import com.ssafy.cafe_in_port_client.data.model.dto.User

data class UserResponse(val grade: Grade, val user: User, var order:List<Order>)