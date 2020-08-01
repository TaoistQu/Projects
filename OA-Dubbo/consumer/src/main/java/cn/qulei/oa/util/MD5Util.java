package cn.qulei.oa.util;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author QuLei
 */
@Component
public final class MD5Util {
    /**
     * @param password
     * @return
     */
    public static String md5(String password) {
        if (password.isEmpty()) {
            return "";
        }

        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(password.getBytes("UTF-8"));
            String result = "";
            String temp = "";
            for (byte b : bytes) {
                temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
