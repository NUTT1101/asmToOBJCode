package com.github.nutt1101;

import com.github.nutt1101.calculator.HexCalculator;
import com.github.nutt1101.introduction.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.nutt1101.Main.currentPath;

// TODO: 程式碼優化
public class ObjectCodeEncoder {
    private static final String startAddress = "0000";
    private static final String variableSyntax = "^[A-Za-z]+:[ \t]+(WORD|RESW|RESB|BYTE)[ \t]+[^ ]+(,[ \t]*[^ ,]+)*[ \t]*$";
    private static final String codeBlockSyntax= "(^[A-Za-z]+:.*)|(^[A-Za-z]+:)";
    private static final String  registerSyntax = "^\\[?R(?:[0-9]|[1-9][0-9]|99)]?$";

    public static List<AddressedIntroduction> encode(String fileName) throws FileNotFoundException {
        List<String> fileIntros = getAllIntroductionsFromFileName(fileName);
        List<AddressedIntroduction> addressedCodes = new ArrayList<>();
        String nextAddress = startAddress,
                currentAddress = startAddress;

        for (String sourceIntro : fileIntros) {
            String intro = trim(sourceIntro);
            AddressedIntroduction addressedCode;
            if (!nextAddress.equals(currentAddress)) {
                currentAddress = nextAddress;
            }

            if (isVariable(intro)) {
                nextAddress = HexCalculator.add(
                        currentAddress,
                        HexCalculator.decimalToHex(getTotalAddSize(intro))
                );
                addressedCode = new VariableIntroduction(currentAddress, intro, nextAddress);
            } else if (!isCodeBlock(intro)) {
                nextAddress = HexCalculator.add(currentAddress, "4");
                addressedCode = new NormalIntroduction(currentAddress, intro, nextAddress);
            } else {
                String[] intros;
                if ((intros = codeBlockStringBreaker(intro)) != null) {
                    nextAddress = HexCalculator.add(currentAddress, "4");
                    addressedCodes.add(
                            new CodeBlockIntroduction(currentAddress, intros[0], nextAddress)
                    );
                    addressedCodes.add(
                            new NormalIntroduction(currentAddress, intros[1], nextAddress)
                    );
                    continue;
                } else {
                    addressedCode = new CodeBlockIntroduction(currentAddress, intro, nextAddress);
                }
            }

            addressedCodes.add(addressedCode);
        }
        addressedCodes.get(addressedCodes.size() - 1).setProgrammingCounter(null); //last introduction doesn't have pc
        generateObjectCode(addressedCodes);
        return addressedCodes;
    }

    private static List<String> getAllIntroductionsFromFileName(String fileName) throws FileNotFoundException {
        List<String> intros = new ArrayList<>();
        File readFile = new File(currentPath + fileName);
        Scanner scanner = new Scanner(readFile);
        while (scanner.hasNext()) intros.add(scanner.nextLine());
        scanner.close();
        return intros;
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
                        absoluteAddressing = calculateRelativeAddress(
                                findDestinationAddress(addressedCodes, arg),
                                ni.getProgrammingCounter()
                        );
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
                                String address = findDestinationAddress(addressedCodes, number);
                                if (address == null) return;
                                while (sb.length() < (8 - address.length())) {
                                    sb.insert(sb.length(), 0);
                                }
                                sb.insert(sb.length(), address);
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

    private static String findDestinationAddress(List<AddressedIntroduction> introductions, String destination) {
        String foundAddress = null;
        for (AddressedIntroduction a : introductions) {
            if (a instanceof VariableIntroduction vi2) {
                if (vi2.getVariableName().equals(destination)) {
                    foundAddress = vi2.getAddress();
                    break;
                }
            } else if (a instanceof CodeBlockIntroduction cbi) {
                if (cbi.getLineIntroduction().contains(destination)) {
                    foundAddress = cbi.getAddress();
                    break;
                }
            }
        }

        return foundAddress;
    }


    private static String calculateRelativeAddress(String des, String pc) {
        return HexCalculator.sub(des, pc);
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

    private static Integer getTypeSize(VariableType type) {
        if (type == VariableType.RESW || type == VariableType.WORD) return 4;
        if (type == VariableType.RESB || type == VariableType.BYTE) return 1;
        return null;
    }

    private static int getTotalAddSize(String intro) {
        List<String> list = getPureIntroduction(intro);
        String type = list.get(1);
        Integer size = getTypeSize(VariableType.valueOf(type));
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