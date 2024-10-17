package com.pandora.www;

import com.pandora.www.config.Config;
import com.pandora.www.service.HistoryServiceImpl;

public class Application {

    private static final HistoryServiceImpl historyService = new HistoryServiceImpl();
    public static void main(String[] args) throws Exception {
        System.out.println("cj开始:..........");
        historyService.saveTradingData(Config.cjStartDay, Config.cjEndDay);

        System.out.println("bj开始:..........");
        historyService.saveBidData(Config.bjStartDay, Config.bjEndDay);

    }
}
