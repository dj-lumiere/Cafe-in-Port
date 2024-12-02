package com.ssafy.cafe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.ssafy.cafe.model.dao.OrderDao;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@EnableCaching
@SpringBootApplication
@MapperScan(basePackageClasses = OrderDao.class)
public class CafeInPortServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CafeInPortServerApplication.class, args);
    }

    @Bean
    public OpenAPI postsApi() {
        Info info = new Info()
                .title("Cafe in Port Server API")
                .description("<h3>Cafe in Port에서 제공되는 API에 대한 설명</h3><br>"
                        + "<img src=\"/imgs/port_logo.png\" width=\"200\">"
                )
                .contact(new Contact()
                        .name("Cafe in Port Corp.")
                        .email("admin@cafeinport.xyz")
                )
                .license(new License()
                        .name("Cafe in Port Corp. License")
                        .url("https://www.cafeinport.xyz/licenses")
                )
                .version("1.0");
        return new OpenAPI().info(info);
    }
}
