package com.example.demo_change_bot.service;

import com.example.demo_change_bot.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Optional;

@Service
public class ParsePathVal implements GetExchange{

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;



    @Override
    public String getUSDExchange() throws ServiceException {
        Optional<String> xmlOptional = client.getCurrencyRetestXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Не удалось получить ВСЕ ВОТ!")
        );

        return extractCurrencyValueFromXml(xml, USD_XPATH);
    }



    @Override
    public String getEURExchange() throws ServiceException {
        Optional<String> xmlOptional = client.getCurrencyRetestXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException(" Не удалось получить ВСЕ ВОТ!")
        );

        return extractCurrencyValueFromXml(xml, EUR_XPATH);
    }
    private String extractCurrencyValueFromXml(String xml, String path) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            Document document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            return xpath.evaluate(path, document);
        }catch (XPathExpressionException ex){
            throw  new ServiceException("Не удалось распарсить", ex);
        }
    }
}
