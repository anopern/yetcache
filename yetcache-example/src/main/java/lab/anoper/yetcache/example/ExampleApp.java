package lab.anoper.yetcache.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "lab.anoper.yetcache.*")
public class ExampleApp {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApp.class, args);
    }
}
