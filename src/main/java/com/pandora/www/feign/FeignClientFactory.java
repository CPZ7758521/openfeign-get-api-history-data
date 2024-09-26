package com.pandora.www.feign;

import com.pandora.www.client.HistoryDataClient;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

public class FeignClientFactory {
    public static HistoryDataClient getHistoryDataClient(String url) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logLevel(Logger.Level.NONE)
                .logger(new StdLogger())
                .target(HistoryDataClient.class, url);
    }
}
