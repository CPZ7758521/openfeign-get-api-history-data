package com.pandora.www.client;

import com.pandora.www.bean.RequestBean;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

public interface HistoryDataClient {

    @Headers({"Content-type: application/json", "Accept: application/json"})
    @RequestLine("POST /cj")
    Map<String, String> getTradingData(RequestBean requestBean);

    @Headers({"Content-type: application/json", "Accept: application/json"})
    @RequestLine("POST /bj")
    Map<String, String> getBidData(RequestBean requestBean);
}
