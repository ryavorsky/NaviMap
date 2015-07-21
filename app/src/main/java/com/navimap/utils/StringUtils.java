package com.navimap.utils;

import android.location.Address;

/**
 * Created by Makvit on 21.07.2015.
 */
public class StringUtils {
    public static String addressToString(Address address) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            sb.append(", ").append(address.getAddressLine(i));
        if (sb.length()>0)
            return sb.substring(2);
        else return "";
    }
    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }
}
