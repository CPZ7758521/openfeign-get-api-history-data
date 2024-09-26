package com.pandora.www.client;

import com.pandora.www.bean.RequestBean;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

public interface HistoryDataClient {

    @Headers("Content-type:")
    @RequestLine("")
    Map<String, String> getTradingData(RequestBean requestBean);

    @Headers("")
    @RequestLine("")
    Map<String, String> getBidData(RequestBean requestBean);
}
