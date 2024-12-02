package com.ssafy.cafe_in_port_client.common.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.ssafy.cafe_in_port_client.base.ApplicationClass
import com.ssafy.cafe_in_port_client.data.model.response.OrderResponse
import com.ssafy.cafe_in_port_client.data.remote.OrderService
import com.ssafy.cafe_in_port_client.common.enums.OrderStatus
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseIsoDateString(dateString: String): Date {
        val offsetDateTime =
            OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val instant = offsetDateTime.toInstant()
        return Date.from(instant)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTime(): Date {
        return Date.from(OffsetDateTime.now().toInstant())
    }

    //날짜 포맷 출력
    fun dateformatYMDHM(time: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatYMD(time: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    // 시간 계산을 통해 완성된 제품인지 확인
    fun isOrderCompleted(order: OrderResponse): OrderStatus {
        return when (checkTime(order.orderTime.time)) {
            in (0..<ApplicationClass.ORDER_PROCESS_TIME) -> OrderStatus.PENDING
            in (ApplicationClass.ORDER_PROCESS_TIME..<ApplicationClass.ORDER_COMPLETE_TIME) -> OrderStatus.PROCESSING
            else -> OrderStatus.COMPLETED
        }
    }

    private fun checkTime(time: Long): Long {
        val curTime = (Date().time + 60 * 60 * 9 * 1000)
        return (curTime - time)
    }

    // 주문 목록에서 총가격, 주문 개수 구하여 List로 반환한다.
    fun calcTotalPrice(orderList: List<OrderResponse>): List<OrderResponse> {
        orderList.forEach { order ->
            calcTotalPrice(order)
        }
        return orderList
    }

    fun calcTotalPrice(order: OrderResponse): OrderResponse {
        order.details.forEach { detail ->
            order.totalPrice += detail.sumPrice
            order.orderCount += detail.quantity
        }
        return order
    }

    fun pickWeightedRandomKeys(frequencyMap: Map<Int, Int>, numberOfPicks: Int): List<Int> {
        // Step 1: Calculate inverse weights
        var weightMap = frequencyMap.mapValues { 1.0 / (it.value + 1) }

        // Step 2: Calculate the total weight
        var totalWeight = weightMap.values.sum()

        // Step 3: Normalize weights to probabilities
        var probabilityMap = weightMap.mapValues { it.value / totalWeight }

        // Step 4: Prepare a list of keys and their cumulative probabilities
        var cumulativeList = mutableListOf<Pair<Int, Double>>()
        var cumulative = 0.0
        for ((key, prob) in probabilityMap) {
            cumulative += prob
            cumulativeList.add(key to cumulative)
        }

        // Step 5: Initialize the result list and a random number generator
        val selectedKeys = mutableListOf<Int>()
        val random = Random.Default

        // Step 6: Perform weighted sampling without replacement
        repeat(frequencyMap.keys.size) {
            if (cumulativeList.isEmpty()) return@repeat // No more elements to pick
            if (selectedKeys.size >= numberOfPicks) {
                return@repeat
            }
            val r = random.nextDouble()
            val selectedEntry = cumulativeList.firstOrNull { it.second >= r }?.first
            if (selectedEntry != null && !selectedKeys.contains(selectedEntry)) {
                selectedKeys.add(selectedEntry)

                // Remove the selected key and adjust the probabilities
                weightMap = weightMap.filterKeys { it != selectedEntry }
                totalWeight -= (1.0 / frequencyMap[selectedEntry]!!)
                if (totalWeight <= 0.0) return@repeat // No more elements to pick

                // Recalculate probabilityMap
                probabilityMap = weightMap.mapValues { it.value / totalWeight }

                // Rebuild cumulativeList
                cumulativeList = mutableListOf()
                cumulative = 0.0
                for ((key, prob) in probabilityMap) {
                    cumulative += prob
                    cumulativeList.add(key to cumulative)
                }
            }
        }
        return selectedKeys
    }

    fun tierNumberToRomanLetter(n: Int): String {
        assert(n in 1..6)
        return listOf("", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ")[n]
    }
}