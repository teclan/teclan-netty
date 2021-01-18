package teclan.netty.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void createIfNeed(String fileName) {

        File file = new File(fileName);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void rename(String src, String dst) {
        rename(new File(src), new File(dst));
    }

    public static void rename(File src, File dst) {
       rename(src,dst,true);
    }

    public static void rename(File src, File dst,boolean overwrite) {
        if (dst.exists()) {
            if(overwrite){
              dst.delete();
            }else{
                LOGGER.warn("检测到目标文件 {} 已经存在，即将在文件名后加入时间戳保存...",dst.getAbsolutePath());
                String fileName = dst.getName();
                String name = fileName.lastIndexOf(".") > 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
                String suffix = fileName.lastIndexOf(".") > 0 ? fileName.substring(fileName.lastIndexOf(".") , fileName.length()) : "";
                fileName = name + "_"+SDF.format(new Date()) + suffix;
                dst = new File(dst.getParentFile().getAbsolutePath() + File.separator + fileName);
            }
        }
        src.renameTo(dst);
    }

    /**
     * 获取文件摘要
     *
     * @param file
     * @param algorithm
     *            MD5,SHA-1,SHA-256
     * @return
     */
    public static String getFileSummary(File file, String algorithm) throws Exception {
        if (!file.isFile()) {
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance(algorithm);
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
           LOGGER.error(e.getMessage(),e);
           throw e;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

}
