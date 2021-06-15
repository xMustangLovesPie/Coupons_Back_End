package app.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class Program {
	public static void main(String[] args) {
		SpringApplication.run(Program.class, args);
		System.out.println("WELCOME TO THE COUPONS PROJECT BY NOAM GONOPOLSKI");
	}
}