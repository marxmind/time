package com.marxmind.utils;

import java.text.DecimalFormat;

public class Numbers
{
    public static double formatDouble(double value) {
        try {
            final DecimalFormat df = new DecimalFormat("####0.00");
            value = Double.valueOf(df.format(value));
            System.out.println("value formatDouble : " + value);
        }
        catch (Exception e) {
            System.out.println("Error in formatDouble for value : " + value + " error : " + e.getMessage());
        }
        return value;
    }
    
    public static double formatDouble(final String value) {
        double val = 0.0;
        try {
            val = Double.parseDouble(value.replace(",", ""));
            final DecimalFormat df = new DecimalFormat("####0.00");
            val = Double.valueOf(df.format(val));
        }
        catch (Exception e) {
            System.out.println("Error in formatDouble for value : " + value + " error : " + e.getMessage());
        }
        return val;
    }
    
    public static double roundOf(double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        final long factor = (long)Math.pow(10.0, places);
        value *= factor;
        final long tmp = Math.round(value);
        return tmp / (double)factor;
    }
}
