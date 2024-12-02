package com.ssafy.cafe_in_port_client.common

object Constants {
    const val BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    const val BEACON_UUID = "" // put your uuid here
    const val BEACON_MAJOR = "" // put your major here
    const val BEACON_MINOR = "" // put your minor here
    const val BLUETOOTH_ADDRESS = "" // put your mac address here
    const val BEACON_DISTANCE = 5.0 //거리
    const val SERVER_URL = "http://192.168.0.8:9987/"
    const val MENU_IMGS_URL = "${SERVER_URL}imgs/menu/"
    const val IMGS_URL = "${SERVER_URL}imgs/"
    const val GRADE_URL = "${SERVER_URL}imgs/grade/"
    const val OPENAI_URL = "https://api.openai.com/v1/chat/completions/"
    const val OPENAI_API_KEY = "" // put your api key here
    const val LONG_DISTANCE_THRESHOLD = 5_000
    const val HUMAN_VELOCITY_PER_MINUTE = 80
}