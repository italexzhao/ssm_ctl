package com.ss.ctrai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ss.ctrai.mapper")
public class CtraiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtraiApplication.class, args);
    }

}
