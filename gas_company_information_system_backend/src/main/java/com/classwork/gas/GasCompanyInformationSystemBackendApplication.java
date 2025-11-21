package com.classwork.gas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.classwork.gas.mapper")
public class GasCompanyInformationSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasCompanyInformationSystemBackendApplication.class, args);
    }
}
