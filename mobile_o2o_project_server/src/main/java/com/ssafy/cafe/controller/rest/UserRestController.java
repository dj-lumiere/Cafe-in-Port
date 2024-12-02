package com.ssafy.cafe.controller.rest;

import com.ssafy.cafe.model.dto.Level;
import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.User;
import com.ssafy.cafe.model.service.OrderService;
import com.ssafy.cafe.model.service.StampService;
import com.ssafy.cafe.model.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ssafy.cafe.util.Constants.*;

@RestController
@RequestMapping("/rest/user")
@CrossOrigin("*")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private static final Integer THIRTY_DAYS = 30 * 24 * 60 * 60;
    private static List<Level> levels;
    @Autowired
    UserService uService;
    @Autowired
    StampService sService;
    @Autowired
    OrderService oService;

    public static Map<String, Object> getGrade(Integer stamp) {
        Map<String, Object> grade = new HashMap<>();
        int pre = 0;
        if (stamp == null || stamp == 0) {
            stamp = 0;
        }
        for (Level level : levels) {
            if (level.getMax() >= stamp) {
                grade.put("title", level.getTitle());
                grade.put("img", level.getImg());
                if (!level.getTitle().equals(PORTMASTER_TIER)) {
                    int step = (stamp - pre) / level.getUnit() + ((stamp - pre) % level.getUnit() > 0 ? 1 : 0);
                    grade.put("step", step);
                    int to = level.getUnit() - (stamp - pre) % level.getUnit();
                    grade.put("to", to);
                }
                grade.put("stepMax", level.getUnit());
                break;
            } else {
                pre = level.getMax();
            }
        }
        return grade;
    }

    @PostMapping
    @Operation(summary = "사용자 정보를 추가한다. 성공하면 true를 리턴한다. ", description = "<pre> json중에서 id, name, pass, email만 입력해도 정상동작한다. \n" + "아래는 aa12 사용자를 추가하는 샘플코드\n " + "{\r\n" + "  \"id\": \"aa12\",\r\n" + "  \"username\": \"aa12\",\r\n" + "  \"pass\": \"aa12\",\r\n" + "  \"email\": \"example@ssafy.io\"\r\n" + "}" + "</pre>")
    public Boolean insert(@RequestBody User user) {
        logger.debug("{}.insert", user);
        int result = 0;
        try {
            result = uService.join(user);
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }

        return result == 1;
    }

    @GetMapping("/isUsed")
    @Operation(summary = "request parameter로 전달된 id가 이미 사용중인지 반환한다.")
    public Boolean isUsedId(String id) {
        return uService.isUsedId(id);
    }

    @GetMapping("/isEmailDuplicate")
    @Operation(summary = "request parameter로 전달된 email이 이미 사용중인지 반환한다.")
    public Boolean isEmailDuplicate(String email) {
        return uService.isEmailDuplicate(email);
    }

    @PostMapping("/findIdFromEmail")
    @Operation(summary = "request parameter로 전달된 email로 유저의 ID를 찾는다.")
    public String findIdFromEmail(String email) {
        return uService.findIdFromEmail(email);
    }

    @PostMapping("/checkValidUser")
    @Operation(summary = "아이디와 이메일로 유저의 번호를 찾는다.", description = """
            <pre>id와 email 두개만 넘겨도 정상동작한다.\s
             아래는 id, email 입력한 샘플코드
            {\r
              "id": "aa12",\r
              "email": "example@ssafy.io"\r
            }\
            </pre>""")
    Integer checkValidUser(@RequestBody User user) {
        return uService.checkValidUser(user);
    }

    @PostMapping("/changePassword")
    @Operation(summary = "유저번호의 비밀번호를 변경한다.", description = """
            <pre>userNo와 pass 두개만 넘겨도 정상동작한다.\s
             아래는 userNo, email 입력한 샘플코드
            {\r
              "userNo": "1",\r
              "pass": "ssafy"\r
            }\
            </pre>""")
    Integer changePassword(@RequestBody User user) {
        return uService.changePassword(user);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려보낸다.", description = """
            <pre>id와 pass 두개만 넘겨도 정상동작한다.\s
             아래는 id, pass만 입력한 샘플코드
            {\r
              "id": "aa12",\r
              "pass": "aa12"\r
            }\
            </pre>""")

    public User login(@RequestBody User user, HttpServletResponse response) {
        User selected = uService.login(user.getId(), user.getPass());
        logger.error("login user=" + selected.toString());
        if (selected != null) {
            String loginIdValue = URLEncoder.encode(selected.getId(), StandardCharsets.UTF_8);
            Cookie loginIdCookie = new Cookie("loginId", loginIdValue);
            loginIdCookie.setPath("/");
            loginIdCookie.setMaxAge(THIRTY_DAYS);
            logger.error(String.valueOf(loginIdCookie.getAttributes()));
            response.addCookie(loginIdCookie);
        }
        return selected;
    }

    // 위에 꺼 대신해서 이걸 만들었다.
    // password를 sharedpreference에 저장하면 안되니, id만 받는데,
    // 이 id와 쿠키에 있는 id가 같은지 확인해서 로그인 사용자를 조회해서 리턴함.
    @GetMapping("/info")
    @Operation(summary = "사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보를 반환한다.", description = "로그인 성공한 cookie 정보가 없으면 전체 null이 리턴됨")
    public Map<String, Object> getInfo(HttpServletRequest request, String id) {
        String idInCookie = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("loginId".equals(cookie.getName())) {
                    idInCookie = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    logger.error("value : " + URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
                }
            }
        }

        User selected = uService.selectUser(id);

        if (!id.equals(idInCookie)) {
            logger.info("different cookie value : inputValue : {}, inCookie:{}", id, idInCookie);
            selected = null; // 사용자 정보 삭제.
        } else {
            logger.info("valid cookie value : inputValue : {}, inCookie:{}", id, idInCookie);
        }

        if (selected == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", new User());
            return map;
        } else {
            Map<String, Object> info = new HashMap<>();
            info.put("user", selected);
            List<Order> orders = oService.getOrderByUser(selected.getUserNo());
            info.put("order", orders);
            info.put("grade", getGrade(selected.getStamps()));
            return info;
        }
    }

    @PostMapping("/{userNo}/fcm-token")
    @Operation(summary = "userNo에 해당하는 사용자의 fcm-token을 업데이트한다.")
    public Integer updateFcmToken(@PathVariable Integer userNo, @RequestBody String fcmToken) {
        var user = new User();
        logger.error("token received: " + fcmToken);
        user.setUserNo(userNo);
        user.setFcmToken(fcmToken);
        return uService.updateFcmToken(user);
    }

    @PostMapping("/info")
    @Operation(summary = "사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보를 반환한다.", description = "아래 User객체에서 id, pass 두개의 정보만 json으로 넘기면 정상동작한다.")
    public Map<String, Object> getInfo(@RequestBody User user) {
        User selected = uService.login(user.getId(), user.getPass());
        if (selected == null) {
            return null;
        } else {
            logger.info("{}", selected);
            Map<String, Object> info = new HashMap<>();
            info.put("user", selected);
            List<Order> orders = oService.getOrderByUser(selected.getUserNo());
            info.put("order", orders);
            info.put("grade", getGrade(selected.getStamps()));
            return info;
        }
    }

    @PostConstruct
    public void setup() {
        levels = new ArrayList<>();
        levels.add(new Level(TRAVELER_TIER, 10, 50, "seeds.png"));
        levels.add(new Level(NAVIGATOR_TIER, 15, 125, "flower.png"));
        levels.add(new Level(LIGHTHOUSE_KEEPER_TIER, 20, 225, "coffee_fruit.png"));
        levels.add(new Level(CAPTAIN_TIER, 25, 350, "coffee_beans.png"));
        levels.add(new Level(PORTMASTER_TIER, Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png"));
    }
}
