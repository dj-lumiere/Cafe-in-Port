package com.ssafy.cafe.model.service;

import java.util.List;

import com.ssafy.cafe.model.dto.Stamp;

public interface StampService {
    /**
     * userNo 사용자의 Stamp 이력을 반환한다.
     *
     * @param id
     * @return
     */
    List<Stamp> selectByUserNo(Integer userNo);
    Integer getStampCount(Integer userNo);
}
