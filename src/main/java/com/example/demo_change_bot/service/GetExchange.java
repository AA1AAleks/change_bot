package com.example.demo_change_bot.service;

import com.example.demo_change_bot.exception.ServiceException;

public interface GetExchange {

 String getUSDExchange() throws ServiceException;
 String getEURExchange() throws ServiceException;
}
