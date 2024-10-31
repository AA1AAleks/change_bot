package com.example.demo_change_bot.config;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("application.properties")
public class BotConfig {

    @Bean
    public OkHttpClient client(){
        return new OkHttpClient();
    }

    @Value("${bot.name}")
    String name;
    @Value("${bot.token}")
    String token;
    @Value("cbr.currency.rates.xml.url")
    private String cbrCurrencyRatesXmlUrl;
}
