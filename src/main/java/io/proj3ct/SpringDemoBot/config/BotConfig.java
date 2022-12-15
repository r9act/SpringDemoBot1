package io.proj3ct.SpringDemoBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


//аннтотации
@Configuration          //чтобы работал @Value
@Data                   //из библиотеки lombok (для удобной манипуляции с классами)
@PropertySource("application.properties") //указываем адрес свойств для @Value
public class BotConfig {

    @Value("${bot.name}")
    String botName;            //свойство бота 1

    @Value("${bot.token}")
    String token;               //свойство бота 2

}
