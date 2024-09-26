package com.pandora.www.feign;

import feign.Logger;

public class StdLogger extends Logger {
    @Override
    protected void log(String configKey, String format, Object... args) {
        System.out.println(String.format(methodTag(configKey) + format, args));
    }
}
