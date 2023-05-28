package com.github.nutt1101;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Introduction {
    static {
        try {
            init();
        } catch (IOException e) {
            // TODO: handle
            e.printStackTrace();
        }
    }

    private static JSONObject introductions;

    private static void init() throws IOException {
        InputStream inputStream = Introduction.class.getClassLoader()
                .getResourceAsStream("introductions.json");
        assert inputStream != null;
        String incString = new String(inputStream.readAllBytes());
        introductions = new JSONObject(incString);
        inputStream.close();
    }

    public static String getCode(String inc) {
        return introductions.getString(inc);
    }
}
