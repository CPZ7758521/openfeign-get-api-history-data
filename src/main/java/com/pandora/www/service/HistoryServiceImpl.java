package com.pandora.www.service;

import com.pandora.www.client.HistoryDataClient;
import com.pandora.www.config.Config;
import com.pandora.www.feign.FeignClientFactory;

public class HistoryServiceImpl implements HistoryService{
    private HistoryDataClient historyDataClient = FeignClientFactory.getHistoryDataClient(Config.dataUrl);
    @Override
    public void saveTradingData(String startDay, String endDay) {
        historyDataClient.getTradingData()
    }

    @Override
    public void saveBidData(String startDay, String endDay) {

    }
}
