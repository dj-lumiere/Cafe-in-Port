package com.ssafy.cafe_in_port_client.data.model.dto

import java.util.Date

data class User(
    var userNo: Int = -1,
    var id: String,
    var username: String,
    var pass: String,
    var email: String,
    var registerTime: Date?=null,
    var stamps: Int,
    var stampList: ArrayList<Stamp> = ArrayList()
) {
    constructor() : this(-1, "", "", "", "", null, 0)
    constructor(id: String, pass: String) : this(-1, id, "", pass, "", null, 0)
    constructor(id: String, name: String, pass: String) : this(-1, id, name, pass, "", null, 0)
    constructor(id: String, name: String, pass: String, email: String) : this(
        -1,
        id,
        name,
        pass,
        email,
        null,
        0
    )
}