package com.ssafy.cafe.model.service;

import com.ssafy.cafe.controller.push.TokenController;
import com.ssafy.cafe.controller.rest.UserRestController;
import com.ssafy.cafe.model.dao.OrderDao;
import com.ssafy.cafe.model.dao.OrderDetailDao;
import com.ssafy.cafe.model.dao.StampDao;
import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ssafy.cafe.util.Constants.PORTMASTER_TIER;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String[] romanLetters = {"", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ"};

    @Autowired
    TokenController tokenController;

    @Autowired
    OrderDao oDao;

    @Autowired
    OrderDetailDao dDao;

    @Autowired
    StampDao sDao;

    @Autowired
    UserDao uDao;

    @Override
    @Transactional
    public void makeOrder(Order order) {
        //client에서 들어온 id 는 무시해야 오류생기지 않음.
        order.setId(null);

        // 주문 및 주문 상세 테이블 저장
        oDao.insert(order);
        List<OrderDetail> details = order.getDetails();
        int quantitySum = 0;
        for (OrderDetail detail : details) {
            detail.setOrderId(order.getId());
            dDao.insert(detail);
            quantitySum += detail.getQuantity();
        }
        Integer currentStampCount = sDao.getStampCount(order.getUserNo());
        if (currentStampCount == null) {
            currentStampCount = 0;
        }
        Map<String, Object> currentGrade = UserRestController.getGrade(currentStampCount);
        String currentGradeString = currentGrade.get("title") + " " + romanLetters[6 - (int) currentGrade.get("step")];
        if (currentGradeString.equals(PORTMASTER_TIER)) {
            currentGradeString = PORTMASTER_TIER;
        }
        Integer nextStampCount = currentStampCount + quantitySum;
        Map<String, Object> nextGrade = UserRestController.getGrade(nextStampCount);
        String nextGradeString = nextGrade.get("title") + " " + romanLetters[6 - (int) nextGrade.get("step")];
        if (nextGradeString.equals(PORTMASTER_TIER)) {
            nextGradeString = PORTMASTER_TIER;
        }
        log.error("currentGrade=" + currentGrade);
        log.error("nextGrade=" + nextGrade);
        User user = uDao.selectByUserNo(order.getUserNo());
        String fcmToken = user.getFcmToken();
        if (!currentGradeString.equals(nextGradeString)) {
            if (fcmToken != null && !fcmToken.isEmpty()) {
                String title = "승급 알림";
                String message = "축하합니다. 당신은 " + currentGradeString + "에서 " + nextGradeString + "로 승급하셨습니다.";
                try {
                    tokenController.sendMessageTo(fcmToken, title, message);
                } catch (IOException ignored) {

                }
            }
        }
        // 스템프 정보 저장
        Stamp stamp = new Stamp(order.getUserNo(), order.getId(), quantitySum);
        sDao.insert(stamp);
        // 사용자 정보 업데이트
        user.setStamps(stamp.getQuantity());
        uDao.updateStamp(user);
        String title;
        String message;
        if (fcmToken != null && !fcmToken.isEmpty() && order.getStatus() != null) {
            title = "주문 확인";
            message = "주문번호 #" + order.getId() + "번이 확인중입니다.";
            try {
                tokenController.sendMessageTo(fcmToken, title, message);
            } catch (IOException ignored) {

            }
        }
    }

    @Override
    public List<Order> getOrderByUser(Integer userNo) {
        return oDao.selectByUser(userNo);
    }

    @Override
    public Integer updateOrder(Order order) {
        OrderInfo currentOrder = getOrderInfo(order.getId());
        if (currentOrder == null) {
            log.error("current order is null");
            return 0;
        }
        if (currentOrder.getStatus() == order.getStatus()) {
            log.error("status match");
            return 0;
        }
        if (currentOrder.getUserNo() == null) {
            log.error("user no is null");
            return 0;
        }
        User user = uDao.selectByUserNo(currentOrder.getUserNo());
        String fcmToken = user.getFcmToken();
        String title = "";
        String message = "";
        if (fcmToken != null && !fcmToken.isEmpty() && order.getStatus() != null) {
            log.error("no token or no order status");
            switch (order.getStatus()) {
                case PENDING -> {
                    title = "주문 확인";
                    message = "주문번호 #" + order.getId() + "번이 확인중입니다.";
                }
                case PROCESSING -> {
                    title = "주문 처리 중";
                    message = "주문번호 #" + order.getId() + "번을 만드는 중입니다.";
                }
                case COMPLETED -> {
                    title = "주문 완료";
                    message = "주문번호 #" + order.getId() + "번이 완료되었습니다.";
                }
                case CANCELLED -> {
                    title = "주문 취소";
                    message = "주문번호 #" + order.getId() + "번이 확인중입니다.";
                }
                case REFUNDED -> {
                    title = "주문 환불";
                    message = "주문번호 #" + order.getId() + "번이 환불되었습니다.";
                }
                case RETURNED -> {
                    title = "주문 반환됨";
                    message = "주문번호 #" + order.getId() + "번이 반환되었습니다.";
                }
            }
            try {
                tokenController.sendMessageTo(fcmToken, title, message);
            } catch (IOException ignored) {

            }
        }
        return oDao.update(order);
    }

    @Override
    public OrderInfo getOrderInfo(Integer id) {
        return oDao.selectOrderInfo(id);
    }

    @Override
    public List<OrderInfo> getLastMonthOrder(Integer userNo) {
        List<OrderInfo> info = oDao.getLastMonthOrder(userNo);
        for (OrderInfo orderInfo : info) {
            List<OrderDetailInfo> detailInfo = oDao.getOrderDetailInfo(orderInfo.getId());
            orderInfo.setDetails(detailInfo);
        }
        return info;
    }

    public Map<Integer, Integer> getLastMonthOrderItem(Integer userNo) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        List<OrderFrequency> target = oDao.getLastMonthOrderItem(userNo);
        for (OrderFrequency orderInfo : target) {
            map.put(orderInfo.getpId(), orderInfo.getTotalQuantity());
        }
        return map;
    }

    @Override
    public List<OrderInfo> getLast6MonthOrder(Integer userNo) {
        return oDao.getLast6MonthOrder(userNo);
    }
}
