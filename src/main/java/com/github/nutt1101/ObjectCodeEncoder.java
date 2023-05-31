package com.github.nutt1101;

import com.github.nutt1101.calculator.HexCalculator;
import com.github.nutt1101.introduction.AddressedIntroduction;
import com.github.nutt1101.introduction.CodeBlockIntroduction;
import com.github.nutt1101.introduction.NormalIntroduction;
import com.github.nutt1101.introduction.VariableIntroduction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.nutt1101.Main.currentPath;

// TODO: 程式碼優化
public class ObjectCodeEncoder {
    private static final String startAddress = "0000";
    private static final String variableSyntax = "^[A-Za-z]+:[ \t]+(WORD|RESW|RESB|BYTE)[ \t]+[^ ]+(,[ \t]*[^ ,]+)*[ \t]*$";
    private static final String codeBlockSyntax= "(^[A-Za-z]+:.*)|(^[A-Za-z]+:)";
    private static String registerSyntax = "^\\[?R(?:[0-9]|[1-9][0-9]|99)]?$";

    public static String encode(List<AddressedIntroduction> addressedCodes) throws FileNotFoundException {
        File readFile = new File(currentPath + "/06.txt");
        Scanner scanner = new Scanner(readFile);

        String currentAddress = startAddress,
                lastAddress = startAddress;

        while (scanner.hasNext()) {
            String intro = trim(scanner.nextLine());
            AddressedIntroduction addressedCode;
            if (!currentAddress.equals(lastAddress)) {
                lastAddress =currentAddress;
            }

            if (isVariable(intro)) {
                currentAddress = HexCalculator.add(
                        lastAddress,
                        HexCalculator.decimalToHex(getTotalAddSize(intro))
                );
                addressedCode = new VariableIntroduction(lastAddress, intro, currentAddress);
            } else if (!isCodeBlock(intro)){
                currentAddress = HexCalculator.add(lastAddress, "4");
                addressedCode = new NormalIntroduction(lastAddress, intro, currentAddress);
            } else {
                String[] intros;
                if ((intros = codeBlockStringBreaker(intro)) != null) {
                    currentAddress = HexCalculator.add(lastAddress, "4");
                    addressedCodes.add(
                            new CodeBlockIntroduction(lastAddress, intros[0], currentAddress)
                    );
                    addressedCodes.add(
                            new NormalIntroduction(lastAddress, intros[1], currentAddress)
                    );
                    continue;
                } else {
                    addressedCode = new CodeBlockIntroduction(lastAddress, intro, currentAddress);
                }
            }
            
            addressedCodes.add(addressedCode);
        }

        addressedCodes.get(addressedCodes.size() - 1).setProgrammingCounter(null);

        System.out.printf(
                "%-10s %-30s %-30s%n",
                "Address",
                "Introduction",
                "Object Code"
        );

        generateObjectCode(addressedCodes);

        addressedCodes.forEach(e -> System.out.printf("%-10s %-30s %-30s%n", e.getAddress(), e.getLineIntroduction(), e.getObjectCode()));
        return "";
    }

    private static String[] codeBlockStringBreaker(String str) {
        Pattern pattern = Pattern.compile("(^[A-Za-z]+:)(.*)");
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find() || matcher.group(2).isBlank()) return null;

        return new String[]{matcher.group(1), matcher.group(2)};
    }

    private static void generateObjectCode(List<AddressedIntroduction> addressedCodes) {
        addressedCodes.forEach(e -> {
            StringBuilder sb = new StringBuilder();
            if (e instanceof NormalIntroduction ni) {
                String absoluteAddressing = "";
                String introCode = IntroductionTable.getCode(ni.getFirst());
                sb.insert(0, introCode);
                for (var arg: ni.getArgs()) {
                    if (isRegister(arg)) {
                        sb.insert(sb.length(), arg.charAt(arg.length() - 1));
                    } else if (tryParseInt(arg) != null) {
                        String hex = HexCalculator.decimalToHex(tryParseInt(arg));
                        while (sb.length() < (8 - hex.length())) {
                            sb.insert(sb.length(), 0);
                        }
                        sb.insert(sb.length(), hex);
                        break;
                    } else {
                        for (var varIntro : addressedCodes) {
                            if (varIntro instanceof VariableIntroduction vi) {
                                if (vi.getVariableName().equals(arg)) {
                                    String pc = ni.getProgrammingCounter();
                                    String des = vi.getAddress();
                                    absoluteAddressing = HexCalculator.sub(des, pc);
                                    break;
                                }
                            } else if (varIntro instanceof CodeBlockIntroduction cbi) {
                                if (cbi.getLineIntroduction().contains(arg)) {
                                    String pc = ni.getProgrammingCounter();
                                    String des = cbi.getAddress();
                                    absoluteAddressing = HexCalculator.sub(des, pc);
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!absoluteAddressing.isBlank() || !absoluteAddressing.isEmpty()) {
                    sb.insert(sb.length(), "F");

                    if (absoluteAddressing.length() + sb.length() > 8) {
                        absoluteAddressing = absoluteAddressing.substring(
                                sb.length() - 1,
                                absoluteAddressing.length() - 1
                        );
                    }

                    while (sb.length() < (8 - absoluteAddressing.length())) {
                        sb.insert(sb.length(), "0");
                    }
                    sb.insert(sb.length(), absoluteAddressing);
                }

                while (sb.length() < 8) {
                    sb.insert(sb.length(), 0);
                }


            } else if (e instanceof VariableIntroduction vi) {
                vi.getNumbers().forEach(number -> {
                    switch (vi.getVariableType()) {
                        case WORD -> {
                            if (tryParseInt(number) != null) {
                                StringBuilder current = new StringBuilder();
                                String hexNumber = HexCalculator.decimalToHex(tryParseInt(number));
                                while (current.length() < (8 - hexNumber.length())) {
                                    current.append("0");
                                }
                                sb.insert(sb.length(), current + hexNumber);

                            } else {
                                addressedCodes.forEach(a -> {
                                    StringBuilder nsb = new StringBuilder();
                                    if (a instanceof VariableIntroduction vi2) {
                                        if (vi2.getVariableName().equals(number)) {
                                            String address = vi2.getAddress();
                                            while (nsb.length() < (8 - address.length())) {
                                                nsb.insert(nsb.length(), 0);
                                            }
                                            nsb.insert(nsb.length(), address);
                                        }
                                    }
                                    sb.insert(0,nsb);
                                });
                            }
                        }
                        case RESW -> {
                            Integer times = tryParseInt(number);

                            for (int i = 0; i < times; i++) {
                                sb.insert(sb.length(), "00000000");
                            }
                        }
                        case BYTE -> {
                            String hexNumber = HexCalculator.decimalToHex(tryParseInt(number));
                            sb.insert(sb.length(),  hexNumber.length() < 2 ? 0 + hexNumber : hexNumber);
                        }
                        case RESB -> {
                            Integer times = tryParseInt(number);

                            for (int i = 0; i < times; i++) {
                                sb.insert(sb.length(), "00");
                            }
                        }
                    }
                });
            }
            e.setObjectCode(sb.toString());
        });

    }

    private static boolean isVariable(String intro) {
        return intro.matches(variableSyntax);
    }

    private static boolean isCodeBlock(String intro) {
        return intro.matches(codeBlockSyntax);
    }

    private static boolean isRegister(String str) {
        return str.matches(registerSyntax);
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
        List<String> list = getPureIntroduction(intro);
        String type = list.get(1);
        Integer size = getTypeSize(type);
        int count = 0;

        if (type.toUpperCase().contains("RES")) {
            String number = list.get(2);
            if (tryParseInt(number) != null) {
                count = tryParseInt(number);
                return size * count;
            } else {
                throw new SyntaxException();
            }
        }

        for (var i : list) {
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

    public static List<String> getPureIntroduction(String intro) {
        return Stream.of(
                intro.split(" ")
        ).filter(
                e -> (!e.isEmpty() && !e.isBlank())
        ).map(
                e -> e.replace(",", "").
                        replace("[","").
                        replace("]","")
        ).toList();
    }

}

class SyntaxException extends RuntimeException {
}