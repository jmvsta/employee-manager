package com.jmvstv_v;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(
        info = @Info(
                title = "Employee Service API",
                version = "v1",
                description = "CRUD operations for Employees"
        )
)
public class AdminServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApp.class, args);
    }

}
