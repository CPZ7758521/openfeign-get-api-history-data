package com.pandora.www.utils;

import com.pandora.www.config.Config;
import com.pandora.www.constant.Constant;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.*;
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

            String krb5ConfPath = krb5Url.getPath();
            String keytabPath = keytabUrl.getPath();

            String protocol = keytabUrl.getProtocol();
            if ("jar".equals(protocol)) {
                krb5ConfPath = LOCAL_BASE_PATH + "/krb5.conf";
                keytabPath = LOCAL_BASE_PATH + "/pandora.keytab";

                File dir = new File(LOCAL_BASE_PATH);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                FileOutputStream krb5Fos = new FileOutputStream(krb5ConfPath);
                FileOutputStream keytabFos = new FileOutputStream(keytabPath);

                IOUtils.copy(krb5Url.openStream(), krb5Fos);
                IOUtils.copy(keytabUrl.openStream(), keytabFos);
            }

            System.setProperty("java.security.krb5.conf", krb5ConfPath);

            conf = new Configuration();
            conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            conf.setBoolean("fs.hdfs.impl.disable.cache", true);
            conf.addResource(Config.env + "core-site.xml");
            conf.addResource(Config.env + "hdfs-site.xml");
            conf.addResource(Config.env + "hive-site.xml");

            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(Config.kerberosUsername, keytabPath);


        } catch (Exception e) {
            System.out.println("kerberos login failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteFile(String tableName) throws IOException {
        //先删除之前的文件在创建新文件
        File dir = new File(LOCAL_BASE_PATH + "/" + tableName);
        org.apache.commons.io.FileUtils.deleteDirectory(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //同时删除hdfs上的数据文件
        FileSystem hdfs = FileSystem.get(conf);
        Path hdfsPath = new Path(HDFS_BASE_PATH + tableName);
        if (hdfs.exists(hdfsPath)) {
            hdfs.delete(hdfsPath, true);
        }

        hdfs.close();
    }

    /**
     * 向本地临时文件中追加数据，当该文件到达一定行数，也就是通过文件的行数 控制文件的大小，当文件到达一定的行数，便flush该文件，并删除，然后向新文件中追加。
     * @param tableName
     * @param arrays
     * @param <T>
     */
    public static <T> void appendFile(String tableName, String[][] arrays) throws IOException {
        //换新表，则重新计算行数
        changeFlag(tableName, tableIsChange);

        String filePath = LOCAL_BASE_PATH + "/" + tableName + "/db-" + Thread.currentThread().getName() + "-" + tableName.substring(tableName.length() - 2) + "-" + flag + ".csv";

        FileWriter fw = new FileWriter(filePath, true);
        BufferedWriter bw = new BufferedWriter(fw);

        StringBuilder dataStr = new StringBuilder();

        //二维数组 一行一行的数据 拼成一行一行的文件
        for (int i = 0; i < arrays.length; i++) {
            for (int j = 0; j < arrays[i].length; j++) {
                if (j == arrays[i].length - 1) {
                    String s = arrays[i][j].replaceAll(",", "，");
                    dataStr.append(s);
                } else {
                    String s = arrays[i][j].replaceAll(",", "，");
                    dataStr.append(s).append(",");
                }
            }

            bw.write(dataStr.toString());
            bw.newLine();
            sumLine ++;

            if (sumLine == Long.parseLong(Config.linesOfOneFile)) {
                fw.flush();
                bw.flush();

                bw.close();
                fw.close();

                flushFile(tableName, LOCAL_BASE_PATH + "/" + tableName + "/db-" + Thread.currentThread().getName() + "-" + tableName.substring(tableName.length() - 2) + "-" + flag + ".csv");

                flag ++;
                sumLine = 0L;

                fw = new FileWriter(LOCAL_BASE_PATH + "/" + tableName + "/db-" + Thread.currentThread().getName() + "-" + tableName.substring(tableName.length() - 2) + "-" + flag + ".csv");

                bw = new BufferedWriter(fw);
            }
        }
        fw.flush();
        bw.flush();
        bw.close();
        fw.close();
    }

    /**
     * 将本地文件上传到hdfs文件系统，并将该文件删除
     * @param tableName
     * @param singleFilePath
     * @throws IOException
     */
    public static void flushFile(String tableName, String singleFilePath) throws IOException {
        FileSystem hdfs = FileSystem.get(conf);
        Path path = new Path(HDFS_BASE_PATH + tableName);

        //由于我们每次运行都会先删除hdfs的文件，为了copyFromLocalFile可以找到hdfs的路径，下面先创建hdfs的路径。
        if (!hdfs.exists(path)) {
            hdfs.mkdirs(path);
        }

        hdfs.copyFromLocalFile(new Path(singleFilePath), path);
        //copy到hdfs后，删除linux系统中tmp文件夹下对应表名文件下的文件
        System.out.println("刷写出>>>>>>" + singleFilePath);
        File oneFile = new File(singleFilePath);
        boolean delete = oneFile.delete();
        if (delete) {
            System.out.println(singleFilePath + " 文件删除成功>>>>>>>");
        }

        hdfs.close();

    }

    /**
     * 判断是否一张表是否已经完成，如果第一张表已经完成，表已经更换，那么flag，sumlines重新计数。
     * @param tableName
     * @param bol
     */
    private static void changeFlag(String tableName, boolean bol) {
        if (Constant.ODS_INDUSTRY_BJ_RT.equals(tableName) && bol) {
            flag = 1;
            sumLine = 0L;
            tableIsChange = false;
        }
    }

    /**
     * 判断是否这张表，是否已经写道最后一个文件，如果是最后一个文件了，那么很可能该文件还没有达到指定的行数，就刷写不到hdfs
     * 该方法就是为了解决这个问题，已经是最后一个文件的话，最后执行刷写。
     * @param hasMore
     * @param tableName
     * @throws IOException
     */
    public static void lastFile(boolean hasMore, String tableName) throws IOException {
        if (!hasMore) {
            String filePath = LOCAL_BASE_PATH + "/" + tableName + "/db-" + Thread.currentThread().getName() + "-" + tableName.substring(tableName.length() - 2) + "-" + flag + ".csv";

            flushFile(tableName, filePath);
        }
    }

}
