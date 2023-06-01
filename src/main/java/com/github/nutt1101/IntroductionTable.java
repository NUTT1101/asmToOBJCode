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

    private static JSONObject table;

    private static void init() throws IOException {
        readTableFromResource();
    }

    public static String getCode(String intro) {
        if (table.isNull(intro)) {
            return null;
        }
        return table.getString(intro);
    }

    private static void readTableFromResource() throws IOException {
        InputStream inputStream = IntroductionTable.class.getClassLoader()
                .getResourceAsStream("introductions.json");
        assert inputStream != null;
        String incString = new String(inputStream.readAllBytes());
        table = new JSONObject(incString);
        inputStream.close();
    }
}
