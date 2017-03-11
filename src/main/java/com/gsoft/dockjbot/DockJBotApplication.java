package com.gsoft.dockjbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "com.gsoft.dockjbot"})
public class DockJBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockJBotApplication.class, args);
    }
}
