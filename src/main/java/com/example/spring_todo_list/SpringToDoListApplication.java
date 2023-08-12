package com.example.spring_todo_list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude =
        {DataSourceAutoConfiguration.class})
public class SpringToDoListApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringToDoListApplication.class, args);
    }

}
