package com.pandora.www.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBean {
    private String security;
    private String user;
    private String password;
    private String startDay;
    private String endDay;
}
