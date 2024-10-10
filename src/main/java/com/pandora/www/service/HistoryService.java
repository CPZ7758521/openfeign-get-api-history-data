package com.pandora.www.service;

public interface HistoryService {
    void saveTradingData(String startDay, String endDay);
    void saveBidData(String startDay, String endDay);
}