package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;

import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.OrderDetailInfo;
import com.ssafy.cafe.model.dto.OrderFrequency;
import com.ssafy.cafe.model.dto.OrderInfo;

public interface OrderService {
    /**
     * 새로운 Order를 생성한다.
     * Order와 OrderDetail에 정보를 추가한다.
     * [심화]User 테이블에 사용자의 Stamp 개수를 업데이트 한다.
     * [심화]Stamp 테이블에 Stamp 이력을 추가한다.
     *
     * @param order
     */
    void makeOrder(Order order);

    /**
     * userNo번에 해당하는 사용자의 Order 목록을 주문 번호의 내림차순으로 반환한다.
     *
     * @param userNo
     * @return
     */
    List<Order> getOrderByUser(Integer userNo);

    /**
     * 주문 정보를 수정한다. - 주문의 상태만 변경된다.
     *
     * @param order
     */
    Integer updateOrder(Order order);

    /**
     * orderId에 대한 Order와 OrderDetail에 대한 내용까지 반환한다.
     * 그리고, 추가적으로 토탈금액, 상품명 등의 추가적인 정보가 담긴
     * OrderInfo객체를 리턴한다.
     * OrderDetail의 내용은 detail id의 오름차순으로 조회한다.
     *
     * @param id
     * @return
     */
    OrderInfo getOrderInfo(Integer orderId);

    /**
     * 사용자가 주문한 최근 1개월의 주문 주문번호 내림차순으로 조회된다.
     * 주문번호의 상세내용은 detail id의 오름차순으로 조회된다.
     * 관통 6단계에서 사용된다.
     *
     * @param userNo
     * @return
     */
    List<OrderInfo> getLastMonthOrder(Integer userNo);

    /**
     * 사용자가 주문한 최근 6개월의 주문 주문번호 내림차순으로 조회된다.
     * 주문번호의 상세내용은 detail id의 오름차순으로 조회된다.
     * 관통 6단계에서 사용된다.
     *
     * @param userNo
     * @return
     */
    List<OrderInfo> getLast6MonthOrder(Integer userNo);

    Map<Integer, Integer> getLastMonthOrderItem(Integer userNo);
}
