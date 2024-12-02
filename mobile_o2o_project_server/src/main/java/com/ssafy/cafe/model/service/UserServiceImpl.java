package com.ssafy.cafe.model.service;

import com.ssafy.cafe.model.dao.UserDao;
import com.ssafy.cafe.model.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserDao userDao;

    @Override
    public Integer join(User user) {
        return userDao.insert(user);
    }

    @Override
    public User login(String id, String pass) {
        User user = userDao.selectById(id);
        if (user != null && user.getPass().equals(pass)) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public Boolean isUsedId(String id) {
        return userDao.selectById(id) != null;
    }

    @Override
    public User selectUser(String id) {
        return userDao.selectById(id);
    }

    @Override
    public User selectByUserNo(Integer userNo) {
        return userDao.selectByUserNo(userNo);
    }

    @Override
    public Boolean isEmailDuplicate(String email) {
        return userDao.isEmailDuplicate(email) != 0;
    }

    @Override
    public String findIdFromEmail(String email) {
        return userDao.findIdFromEmail(email);
    }

    public Integer checkValidUser(User user) {
        Integer userId = userDao.checkValidUser(user);
        if (userId != null) {
            return userId;
        }
        return 0;
    }

    public Integer changePassword(User user) {
        return userDao.changePassword(user);
    }

    @Override
    public Integer updateFcmToken(User user) {
        user.setFcmToken(user.getFcmToken().replace("\"", ""));
        log.info(user.getFcmToken());
        return userDao.updateFcmToken(user);
    }
}

