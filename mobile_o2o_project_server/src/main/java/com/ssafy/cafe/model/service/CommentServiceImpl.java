package com.ssafy.cafe.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.cafe.model.dao.CommentDao;
import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.dto.CommentInfo;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentDao cDao;

    @Override
    @Transactional
    public int addComment(Comment comment) {
        return cDao.insert(comment);
    }

    @Override
    public Comment selectComment(Integer id) {
        return cDao.select(id);
    }

    @Override
    @Transactional
    public int removeComment(Integer commentId) {
        return cDao.delete(commentId);

    }

    @Override
    @Transactional
    public int updateComment(Comment comment) {
        return cDao.update(comment);
    }

    @Override
    public List<CommentInfo> selectByProduct(Integer productId) {
        return cDao.selectByProduct(productId);
    }


}
