package com.github.nutt1101.calculator;

import java.util.List;

public class HexCalculator {
    private static final List<Character> hexList;

    static {
        hexList= List.of(
                '0', '1', '2',
                '3', '4', '5',
                '6', '7', '8',
                '9', 'A', 'B',
                'C', 'D', 'E',
                'F'
        );
    }

    public static String add(String a, String b) {
        int maxLength = a.length();
        StringBuilder result = new StringBuilder();

        if (a.length() != b.length()) {
            maxLength = Math.max(a.length(), b.length());
            a = fillZero(a, maxLength);
            b = fillZero(b, maxLength);
        }

        int carry = 0;
        for (int i = maxLength - 1; i >= 0; i--) {
            int aIndex = getDecimal(a.charAt(i));
            int bIndex = getDecimal(b.charAt(i));

            int sum = aIndex + bIndex;

            result.insert(0, getCode((sum + carry) % 16));
            carry = (sum / 16);
        }

        return result.toString();
    }

    private static String fillZero(String des, int howMany) {
        StringBuilder desBuilder = new StringBuilder(des);
        while (desBuilder.length() != howMany) {
            desBuilder.insert(0, "0");
        }
        des = desBuilder.toString();
        return des;
    }

    public static String decimalToHex(int dec) {
        StringBuilder hex = new StringBuilder();
        if (dec < 16) {
            return String.valueOf(
                    hexList.get(dec)
            );
        }

        while (dec / 16 != 0) {
            hex.insert(0, dec % 16);
            dec /= 16;
        }

        return hex.toString();
    }

    private static int getDecimal(Character d) {
        return hexList.indexOf(d);
    }

    private static Character getCode(int index) {
        return hexList.get(index);
    }
}
