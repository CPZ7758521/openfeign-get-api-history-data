package com.pandora.www.service;

import java.io.IOException;
import java.text.ParseException;

public interface HistoryService {
    void saveTradingData(String startDay, String endDay) throws Exception;
    void saveBidData(String startDay, String endDay) throws Exception;
}