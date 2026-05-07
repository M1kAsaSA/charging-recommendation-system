package com.charging.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.charging.system.mapper")
public class ChargingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChargingApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ Charging System Application started successfully   ლ(´ڡ`ლ)ﾞ");
    }
}
