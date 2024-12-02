package com.ssafy.cafe.controller.rest;

import java.util.List;
import java.util.Map;

import com.ssafy.cafe.model.dto.OrderDetailInfo;
import com.ssafy.cafe.model.dto.OrderInfo;
import com.ssafy.cafe.model.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/rest/order")
@CrossOrigin("*")
public class OrderRestController {

    private static final Logger logger = LoggerFactory.getLogger(OrderRestController.class);

    @Autowired
    private OrderService oService;
    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "order 객체를 저장하고 추가된 Order의 id를 반환한다. FCMToken을 입력하면 승급 시 알림을 보낸다.", description = """
            <pre>아래 형태로 입력하면 주문이 입력된다. \r
            2번 고객이 1번 상품(아메리카노)를 2개 주문함. \r
            {\r
              "status": "PENDING",\r
              "details": [\r
                {\r
                  "productDetailId": 1,\r
                  "quantity": 2\r
                }\r
              ],\r
              "orderTable": "웹주문",\r
              "userNo": "2"\r
            } \
            </pre>""")
    public Integer makeOrder(@RequestBody Order order) {
        logger.debug("makeOrder", order);
        if (order.getDetails().size() <= 0) {
            return -1;
        } else {
            oService.makeOrder(order);
            return order.getId();
        }
    }

    @PostMapping("/updateOrderStatus")
    @Operation(summary = "order의 상태를 변화시킨다. FCMToken을 입력하면 알림을 보낸다.", description = """
            <pre>아래 형태로 입력하면 주문이 입력된다. \r
            1번 주문의 상태를 PENDING으로 변화. \r
            {\r
              "status": "PENDING",\r
              "id": "1",\r
            } \
            </pre>""")
    public Integer updateOrder(@RequestBody Order order) {
        logger.debug("updateOrder", order);
        return oService.updateOrder(order);
    }


    @GetMapping("/{orderId}")
    @Operation(summary = "{orderId}에 해당하는 주문의 상세 내역을 목록 형태로 반환한다." + "이 정보는 사용자 정보 화면의 주문 내역 조회에서 사용된다.")
    public OrderInfo getOrderDetail(@PathVariable Integer orderId) {
        return oService.getOrderInfo(orderId);
    }

    @GetMapping("/byUser")
    @Operation(summary = "{userNo}에 해당하는 사용자의 최근 1개월간 주문 내역을 반환한다." + "반환 정보는 1차 주문번호 내림차순, 2차 주문 상세 오름차순으로 정렬된다.")
    public List<OrderInfo> getLastMonthOrder(Integer userNo) {
        return oService.getLastMonthOrder(userNo);
    }

    @GetMapping("/byUser/counter")
    @Operation(summary = "{userNo}에 해당하는 사용자의 최근 1개월간 주문 내역을 반환한다." + "반환 정보는 1차 주문번호 내림차순, 2차 주문 상세 오름차순으로 정렬된다.")
    public Map<Integer, Integer> getLastMonthOrderItem(Integer userNo) {
        return oService.getLastMonthOrderItem(userNo);
    }

    @GetMapping("/byUserIn6Months")
    @Operation(summary = "{userNo}에 해당하는 사용자의 최근 6개월간 주문 내역을 반환한다." + "반환 정보는 1차 주문번호 내림차순, 2차 주문 상세 오름차순으로 정렬된다.")
    public List<OrderInfo> getLast6MonthOrder(Integer userNo) {
        return oService.getLast6MonthOrder(userNo);
    }
}
