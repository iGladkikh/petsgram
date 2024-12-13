package ru.yandex.practicum.catsgram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CatsgramApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CatsgramApplication.class, args);
        System.out.println(context.getBeanDefinitionCount());
    }
}
