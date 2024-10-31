package com.example.demo_change_bot.service;

import com.example.demo_change_bot.exception.ServiceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CbrClient {

    @Autowired
    private OkHttpClient client;


    public Optional<String> getCurrencyRetestXML() throws ServiceException{
        String cbrCurrencyRatesXmlUrl = "http://www.cbr.ru/scripts/XML_daily.asp";
        Request request = new Request.Builder()
                .url(cbrCurrencyRatesXmlUrl)
                .build();

        try (var response = client.newCall(request).execute()){
            ResponseBody body = response.body();
            return body==null ? Optional.empty() : Optional.of(body.string());

        }catch (IOException ex) {
            throw new ServiceException("Ошибка получения курса валют", ex);
        }
    }
}
