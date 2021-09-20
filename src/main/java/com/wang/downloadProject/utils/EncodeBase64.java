package com.wang.downloadProject.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EncodeBase64 {
    public static String encodeBase64(String str) {
        final Base64.Encoder encoder = Base64.getEncoder();
        byte[] textByte = new byte[0];
        try {
            textByte = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoder.encodeToString(textByte);
    }
}
