package com.ssafy.cafe.model.dao;

import com.ssafy.cafe.model.dto.Stamp;

import java.util.List;

public interface StampDao {

    /**
     * stamp정보를 입력한다.(make order에서)
     * order detail에는 주문의 상세 내역이 들어가고,
     * 이 테이블에는 해당 주문번호로 총 몇건이 주문되어 몇개의 stamp가 적립되었는지 기록된다.
     *
     * @param stamp
     * @return
     */
    int insert(Stamp stamp);

    /**
     * userNo번 사용자의 Stamp 이력을 반환한다.
     *
     * @param userNo : 조회할 유저의 고유번호
     * @return
     */
    List<Stamp> selectByUserNo(Integer userNo);
    Integer getStampCount(Integer userNo);
}
