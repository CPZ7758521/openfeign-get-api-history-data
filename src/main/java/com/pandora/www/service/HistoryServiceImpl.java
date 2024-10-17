package com.pandora.www.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandora.www.bean.RequestBean;
import com.pandora.www.client.HistoryDataClient;
import com.pandora.www.config.Config;
import com.pandora.www.constant.Constant;
import com.pandora.www.feign.FeignClientFactory;
import com.pandora.www.utils.FileUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryServiceImpl implements HistoryService{
    private final HistoryDataClient historyDataClient = FeignClientFactory.getHistoryDataClient(Config.dataUrl);
    @Override
    public void saveTradingData(String startDay, String endDay) throws Exception {
        FileUtils.deleteFile(Constant.ODS_INDUSTRY_CJ_RT);
        RequestBean requestBean = new RequestBean();
        requestBean.setSecurity("[]");
        requestBean.setUser(Config.user);
        requestBean.setPassword(Config.password);

        boolean hasMore = true;
        String oneStartDay = startDay;
        int sum = 0;
        do {
            sum ++;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date startDate = sdf.parse(oneStartDay);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(startDate);
            calendar.add(calendar.DATE,1);
            Date dateAdd = calendar.getTime();
            System.out.println("第一天：\n" + sdf.format(startDate) + "\n");
            String dateAddStr = sdf.format(dateAdd);
            System.out.println("第二天：\n" + dateAddStr + "\n");

            String oneEndDay = dateAddStr;

            requestBean.setStartDay(oneStartDay);
            requestBean.setEndDay(oneEndDay);

            Map<String, String> tradingData = historyDataClient.getTradingData(requestBean);

            System.out.println("开始时间：" + oneStartDay + "------->>trading<<--------结束时间" + oneEndDay + "\n");

            if (Objects.nonNull(tradingData)) {
                ObjectMapper objectMapper = new ObjectMapper();

                List<String[][]> mapValuesList = tradingData.values().stream().map(row -> {
                    try {
                        row.replaceAll("\\\"", "\\\\\\\\\\\\\"");
                        row.replaceAll("'", "\"");
                        return objectMapper.readValue(row, new TypeReference<String[][]>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());

                //将数据追加到本地的文件中
                try {
                    for (String[][] array : mapValuesList) {
                        FileUtils.appendFile(Constant.ODS_INDUSTRY_CJ_RT, array);
                    }
                    System.out.println("追加文件正常>>>>>>>>>>>>");
                } catch (Exception e) {
                    System.out.println("追加文件异常>>>>>>>>>>>>");
                    e.printStackTrace();
                }
            }
            oneStartDay = oneEndDay;
            if (sdf.parse(oneStartDay).after(sdf.parse(endDay))) {
                hasMore = false;
                FileUtils.lastFile(hasMore, Constant.ODS_INDUSTRY_CJ_RT);
                System.out.println("共" + sum + "天");
            }

        } while (hasMore);
    }

    @Override
    public void saveBidData(String startDay, String endDay) throws Exception {
        FileUtils.deleteFile(Constant.ODS_INDUSTRY_BJ_RT);
        RequestBean requestBean = new RequestBean();
        requestBean.setSecurity("[]");
        requestBean.setUser(Config.user);
        requestBean.setPassword(Config.password);

        boolean hasMore = true;
        String oneStartDay = startDay;
        int sum = 0;
        do {
            sum ++;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date startDate = sdf.parse(oneStartDay);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(startDate);
            calendar.add(calendar.DATE,1);
            Date dateAdd = calendar.getTime();
            System.out.println("第一天：\n" + sdf.format(startDate) + "\n");
            String dateAddStr = sdf.format(dateAdd);
            System.out.println("第二天：\n" + dateAddStr + "\n");

            String oneEndDay = dateAddStr;

            requestBean.setStartDay(oneStartDay);
            requestBean.setEndDay(oneEndDay);

            Map<String, String> bidData = historyDataClient.getBidData(requestBean);
            System.out.println("开始时间：" + oneStartDay + "------->>trading<<--------结束时间" + oneEndDay + "\n");

            if (Objects.nonNull(bidData)) {
                ObjectMapper objectMapper = new ObjectMapper();

                List<String[][]> mapValueList = bidData.values().stream().map(row -> {
                    try {
                        row.replaceAll("\\\"", "\\\\\\\\\\\\\"");
                        row.replaceAll("'", "\"");
                        return objectMapper.readValue(row, new TypeReference<String[][]>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());

                try {
                    for (String[][] array : mapValueList) {
                        FileUtils.appendFile(Constant.ODS_INDUSTRY_BJ_RT, array);
                    }
                    System.out.println("追加文件正常>>>>>>>>>>>>");
                } catch (Exception e) {
                    System.out.println("追加文件异常>>>>>>>>>>>>");
                    e.printStackTrace();
                }
            }
            oneStartDay = oneEndDay;
            if (sdf.parse(oneStartDay).after(sdf.parse(oneEndDay))) {
                hasMore = false;
                FileUtils.lastFile(hasMore, Constant.ODS_INDUSTRY_BJ_RT);
                System.out.println("共" + sum + "天");
            }
        } while (hasMore);
    }
}
