package com.ssafy.cafe.model.service;

import com.ssafy.cafe.model.dao.CommentDao;
import com.ssafy.cafe.model.dao.ProductDao;
import com.ssafy.cafe.model.dto.CommentInfo;
import com.ssafy.cafe.model.dto.Product;
import com.ssafy.cafe.model.dto.ProductWithComment;
import com.ssafy.cafe.model.dto.ProductWithDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao pDao;

    @Autowired
    private CommentDao cDao;

    @Override
    @Cacheable(value = "getProductList")
    public List<Product> getProductList() {
        return pDao.selectAll();
    }

    @Override
    public Product selectProductById(Integer productId) {
        return pDao.selectProductById(productId);
    }

    @Override
    public ProductWithComment selectWithComment(Integer productId) {
        ProductWithComment product = pDao.selectInfo(productId);

        if (product != null) {
            List<CommentInfo> commentInfo = cDao.selectByProduct(productId);
            product.setComments(commentInfo);
        }

        return product;
    }

    @Override
    public List<ProductWithDetail> getProductWithDetail(Integer productId) {
        List<ProductWithDetail> products = pDao.getProductWithDetail(productId);
        return products;
    }

    @Override
    public Integer getProductDetailId(ProductWithDetail productWithDetail) {
        Integer result = pDao.getProductDetailId(productWithDetail);
        if (result == null) {
            result = 0;
        }
        return result;
    }
}
