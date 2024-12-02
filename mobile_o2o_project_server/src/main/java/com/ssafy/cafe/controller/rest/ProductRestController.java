package com.ssafy.cafe.controller.rest;

import com.ssafy.cafe.enumclasses.DrinkSize;
import com.ssafy.cafe.enumclasses.Temperature;
import com.ssafy.cafe.model.dto.Product;
import com.ssafy.cafe.model.dto.ProductWithComment;
import com.ssafy.cafe.model.dto.ProductWithDetail;
import com.ssafy.cafe.model.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/product")
@CrossOrigin("*")
public class ProductRestController {

    @Autowired
    ProductService pService;

    @GetMapping()
    @Operation(summary = "전체 상품의 목록을 반환한다.")
    public ResponseEntity<List<Product>> getProductList() {
        return new ResponseEntity<>(pService.getProductList(), HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "{productId}에 해당하는 상품의 정보를 반환한다.")
    Product selectProductById(@PathVariable Integer productId){
        return pService.selectProductById(productId);
    }

    @GetMapping("/{productId}/comments")
    @Operation(summary = "{productId}에 해당하는 상품의 정보를 comment와 함께 반환한다." + "이 기능은 상품의 comment를 조회할 때 사용된다.")
    public ProductWithComment getProductWithComments(@PathVariable Integer productId) {
        return pService.selectWithComment(productId);
    }

    @GetMapping("/{productId}/details")
    @Operation(summary = "{productId}에 해당하는 상품의 옵션을 조회한다.")
    public List<ProductWithDetail> getProductWithDetail(@PathVariable Integer productId) {
        return pService.getProductWithDetail(productId);
    }

    @GetMapping("/{productId}/{size}/{temperature}")
    @Operation(summary = "{productId}, {size}, {temperature}인 상품의 번호를 조회한다. 존재하지 않는 옵션이면 0을 출력한다.")
    public Integer getProductDetailId(@PathVariable Integer productId, @PathVariable DrinkSize size, @PathVariable Temperature temperature) {
        return pService.getProductDetailId(new ProductWithDetail(productId, size, temperature));
    }

}










