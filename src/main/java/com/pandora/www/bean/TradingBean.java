package com.pandora.www.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingBean {
    private String symbol;
    private String orderQty;
    private String side;
}
