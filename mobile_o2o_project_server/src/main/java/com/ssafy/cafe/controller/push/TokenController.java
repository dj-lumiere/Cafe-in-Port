package com.ssafy.cafe.controller.push;

import com.ssafy.cafe.model.service.FirebaseCloudMessageService;
import com.ssafy.cafe.model.service.FirebaseCloudMessageServiceWithData;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin("*")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    FirebaseCloudMessageService service;

    @Autowired
    FirebaseCloudMessageServiceWithData serviceWithData;


    @PostMapping("/sendMessageTo")
    @Operation(summary = "{token}을 이용해 메시지를 보낸다.", description = "알림의 제목이 {title}, 알림의 내용이 {body}가 된다.")
    public void sendMessageTo(String token, String title, String body) throws IOException {
        logger.info("sendMessageTo : token:{}, title:{}, body:{}", token, title, body);
        service.sendMessageTo(token, title, body);
    }
}

