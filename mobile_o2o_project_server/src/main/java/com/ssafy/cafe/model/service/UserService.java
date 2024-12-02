package com.ssafy.cafe.model.service;

import com.ssafy.cafe.model.dto.User;


public interface UserService {
    /**
     * 사용자 정보를 DB에 저장한다.
     *
     * @param user
     */
    Integer join(User user);

    /**
     * id, pass에 해당하는 User 정보를 반환한다.
     *
     * @param id
     * @param pass
     * @return 조회된 User 정보를 반환한다.
     */
    User login(String id, String pass);


    /**
     * 해당 아이디가 이미 사용 중인지를 반환한다.
     *
     * @param id
     * @return
     */
    Boolean isUsedId(String id);

    /**
     * id 에 해당하는 User 정보를 반환한다.
     *
     * @param id
     * @return 조회된 User 정보를 반환한다.
     */
    User selectUser(String id);

    User selectByUserNo(Integer userNo);

    Boolean isEmailDuplicate(String email);

    String findIdFromEmail(String email);

    Integer checkValidUser(User user);

    Integer changePassword(User user);

    Integer updateFcmToken(User user);
}
