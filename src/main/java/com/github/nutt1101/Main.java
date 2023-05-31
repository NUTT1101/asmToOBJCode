package com.github.nutt1101;

import com.github.nutt1101.calculator.HexCalculator;
import com.github.nutt1101.introduction.AddressedIntroduction;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String currentPath;
    static List<AddressedIntroduction> addressedCodes;

    static {
        currentPath = System.getProperty("user.dir");
        addressedCodes = new ArrayList<>();
    }

    public static void main(String[] args) throws FileNotFoundException {
        ObjectCodeEncoder.encode(addressedCodes);
    }
}