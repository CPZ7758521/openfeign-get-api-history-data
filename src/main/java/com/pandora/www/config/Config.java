package com.pandora.www.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Config {
    public static String env;
    public static String dataUrl;
    public static String kerberosUsername;
    public static String user;
    public static String password;
    public static String cjStartDay;
    public static String cjEndDay;
    public static String bjStartDay;
    public static String bjEndDay;
    public static String linesOfOneFile;

    private static Logger LOG = LoggerFactory.getLogger(Config.class);

    static {
        Properties properties = new Properties();
        LOG.info("Current Envoriment is " + env);

        env = System.getProperty("env");
        cjStartDay = System.getProperty("cjStartDay");
        cjEndDay = System.getProperty("cjEndDay");
        bjStartDay = System.getProperty("bjStartDay");
        bjEndDay = System.getProperty("bjEndDay");

        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream(env + "/config.properties"));

            dataUrl = properties.getProperty("qeubee.history.url");
            kerberosUsername = properties.getProperty("kerberos.username");
            user = properties.getProperty("api.user");
            password = properties.getProperty("api.password");
            linesOfOneFile = properties.getProperty("linesOfOneFile");

        } catch (Exception e) {
            LOG.error("init properties failure!\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
