package com.pandora.www.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidBean {
    private String symbol;
    private String securityID;
    private String scQuoteTime;
}
