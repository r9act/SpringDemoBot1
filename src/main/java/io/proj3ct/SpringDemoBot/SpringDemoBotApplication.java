package io.proj3ct.SpringDemoBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	//будет загружать наше приложение (контекстов создавать не нужно)
public class SpringDemoBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDemoBotApplication.class, args);
	}

}
