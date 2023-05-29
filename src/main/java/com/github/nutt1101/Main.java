package com.github.nutt1101;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String currentPath;
    static List<AddressedCode> addressedCodes;

    static {
        currentPath = System.getProperty("user.dir");
        addressedCodes = new ArrayList<>();
    }

    public static void main(String[] args) throws FileNotFoundException {
         ObjectCodeEncoder.encode(addressedCodes);
    }
}