package com.ssafy.cafe_in_port_client.data.model.dto

import com.google.android.gms.maps.model.LatLng

data class Store(
    var location: LatLng, var storeName: String = ""
)