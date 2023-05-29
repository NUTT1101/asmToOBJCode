package com.github.nutt1101;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class IntroductionTable {
    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject introductions;

    private static void init() throws IOException {
        InputStream inputStream = IntroductionTable.class.getClassLoader()
                .getResourceAsStream("introductions.json");
        assert inputStream != null;
        String incString = new String(inputStream.readAllBytes());
        introductions = new JSONObject(incString);
        inputStream.close();
    }

    public static String getCode(String intro) {
        if (introductions.isNull(intro)) {
            return "";
        }
        return introductions.getString(intro);
    }
}
