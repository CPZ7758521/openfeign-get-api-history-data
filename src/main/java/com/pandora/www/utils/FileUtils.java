package com.pandora.www.utils;

import com.pandora.www.config.Config;
import org.apache.hadoop.conf.Configuration;

import java.net.URL;

public class FileUtils {
    private static Configuration conf;
    private static String HDFS_BASE_PATH = "/user/pandora/history/";
    private static String LOCAL_BASE_PATH = System.getProperty("java.io.tmpdir") + "/" + System.getProperty("user.name");
    private static int flag = 1;
    private static long sumLine = 0L;
    private static boolean tableIsChange = true;

    static {
        try {
            URL krb5Url = FileUtils.class.getClassLoader().getResource(Config.env + "/krb5.conf");
            URL keytabUrl = FileUtils.class.getClassLoader().getResource(Config.env + "/pandora.keytab");
        } catch ()
    }
}
