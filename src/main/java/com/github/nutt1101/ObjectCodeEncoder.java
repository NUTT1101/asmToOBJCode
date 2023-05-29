package com.github.nutt1101;

import com.github.nutt1101.calculator.HexCalculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static com.github.nutt1101.Main.currentPath;

public class ObjectCodeEncoder {
    private static final String startAddress = "0000";
    private static final String variableSyntax = "^[A-Za-z]+:[ \t]+(WORD|RESW|RESB|BYTE)[ \t]+[^ ]+(,[ \t]*[^ ,]+)*[ \t]*$";
    private static final String codeBlockSyntax= "[A-Za-z]+:";

    public static String encode(List<AddressedCode> addressedCodes) throws FileNotFoundException {
        File readFile = new File(currentPath + "/02.txt");
        Scanner scanner = new Scanner(readFile);

        String currentAddress = startAddress, lastAddress = startAddress;

        while (scanner.hasNext()) {
            String intro = scanner.nextLine();
            intro = trim(intro);
            AddressedCode addressedCode;
            if (!currentAddress.equals(lastAddress)) {
                lastAddress =currentAddress;
            }

            if (isVariable(intro)) {
                currentAddress = HexCalculator.add(
                        lastAddress,
                        HexCalculator.decimalToHex(getTotalAddSize(intro))
                );
            } else if (!isCodeBlock(intro)){
                currentAddress = HexCalculator.add(lastAddress, "4");
            }

            addressedCode = new AddressedCode(lastAddress, intro);
            addressedCodes.add(addressedCode);
        }

        System.out.printf(
                "%-10s %-10s%n",
                "位址",
                "指令"
        );
        for (var i : addressedCodes) {
            System.out.printf(
                    "%-10s %-10s%n",
                    i.getAddress(),
                    i.getLineIntroduction()
            );
        }

        return "";
    }

    private static boolean isVariable(String intro) {
        return intro.matches(variableSyntax);
    }

    private static boolean isCodeBlock(String intro) {
        return intro.matches(codeBlockSyntax);
    }

    private static String trim(String intro) {
        return intro.replace("\t", " ");
    }

    private static Integer getTypeSize(String type) {
        HashMap<String, Integer> table = new HashMap<>();
        table.put("WORD", 4);
        table.put("BYTE", 1);
        table.put("RESW", 4);
        table.put("RESB", 1);
        return table.getOrDefault(type, null); // TODO: handle
    }

    private static int getTotalAddSize(String intro) {
        List<String> list = Stream.of(
                intro.split(" ")
        ).filter(
                e -> (!e.isEmpty() && !e.isBlank())
        ).toList();

        String type = list.get(1);
        Integer size = getTypeSize(type);
        int count = 0;

        if (type.toUpperCase().contains("RES")) {
            String number = list.get(2).replace(",", "");
            if (tryParseInt(number) != null) {
                count = tryParseInt(number);
            } else {
                throw new SyntaxException();
            }
        }

        for (var i : list) {
            i = i.replace(",", "");
            if (tryParseInt(i) == null) continue;
            count++;
        }

        if (size * count == 0) return size;

        return size * count;
    }

    public static Integer tryParseInt(String someText) {
        try {
            return Integer.parseInt(someText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

class SyntaxException extends RuntimeException {
}