package org.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@org.example.taskmanager.SpringBootApplication
public class TaskmanagerApplication {

	public static void main(String[] args) {

        SpringApplication.run(TaskmanagerApplication.class, args);
        System.out.println("Hello World! Task Manager Application is running...");
	}

}
