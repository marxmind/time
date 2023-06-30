package com.marxmind.utils;

import java.util.Base64;

public class SecureChar
{
    public static String encode(final String val) {
        try {
            final String encoded = Base64.getEncoder().encodeToString(val.getBytes(AppConf.SECURITY_ENCRYPTION_FORMAT.getValue()));
            return encoded;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static String decode(final String val) {
        try {
            final byte[] barr = Base64.getDecoder().decode(val);
            return new String(barr, AppConf.SECURITY_ENCRYPTION_FORMAT.getValue());
        }
        catch (Exception ex) {
            return null;
        }
    }
}