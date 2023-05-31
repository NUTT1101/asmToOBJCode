package com.github.nutt1101.calculator;

import java.util.List;

public class HexCalculator {
    public static String add(String hexNumb1, String hexNumb2) {
        int decNumb1 = Integer.parseInt(hexNumb1, 16);
        int decNumb2 = Integer.parseInt(hexNumb2, 16);
        int sum = decNumb1 + decNumb2;
        return Integer.toHexString(sum).toUpperCase();
    }

    public static String sub(String hexNumb1, String hexNumb2) {
        int decNumb1 = Integer.parseInt(hexNumb1, 16);
        int decNumb2 = Integer.parseInt(hexNumb2, 16);
        int sum = decNumb1 - decNumb2;
        return Integer.toHexString(sum).toUpperCase();
    }

    public static String decimalToHex(int dec) {
        return Integer.toHexString(dec).toUpperCase();
    }
}
