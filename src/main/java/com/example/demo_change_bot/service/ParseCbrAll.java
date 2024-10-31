package com.example.demo_change_bot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class ParseCbrAll {

    public String getDocument() {
        Document doc;
        try {
            doc = Jsoup.connect("http://www.cbr.ru/currency_base/daily/")
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       return getParseAllCurrency(doc);


    }

    private String getParseAllCurrency(Document doc) {
        StringBuilder stringBuilder = new StringBuilder();

        Elements elements = doc.select("table.data tr");

        for (Element element : elements){
            Elements val = element.select("td");

            if(!val.isEmpty()){
                String nominal = val.get(2).text();
                String nameVal = val.get(3).text();
                String price = val.get(4).text();

                stringBuilder
                        .append("\n")
                        .append(nominal)
                        .append(" - ").append(nameVal)
                        .append(" - ").append(price)
                        ;
            }
        }

        return stringBuilder.toString();
    }
}
