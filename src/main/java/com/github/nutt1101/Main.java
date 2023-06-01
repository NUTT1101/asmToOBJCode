package com.github.nutt1101;

import com.github.nutt1101.introduction.AddressedIntroduction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final String currentPath;
    static List<AddressedIntroduction> encodedIntroduction;
    static final String printFormat;

    static {
        currentPath = System.getProperty("user.dir") + "/";
        printFormat = "%-10s %-30s %-30s%n";
    }

    public static void main(String[] args)  {
        if (args.length == 0) {
            System.out.println("Please enter the file name. Usage: java -jar <file.jar> <filename.txt>");
            return;
        }

        try {
            encodedIntroduction = ObjectCodeEncoder.encode(args[0]);
            printDescription();
            printObjectCode();
            outputToFile(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("The system cannot find the specified file.。");
        }
    }

    private static void printDescription() {
        System.out.println("-----Description-----");
        System.out.printf(printFormat, "Address", "Introduction", "Object Code");
        encodedIntroduction.forEach(e -> System.out.printf(
                printFormat, e.getAddress(), e.getLineIntroduction(), e.getObjectCode())
        );
    }

    private static void printObjectCode() {
        System.out.println("-----Object Code-----");
        for (AddressedIntroduction e : encodedIntroduction) {
            if (e.getObjectCode().isEmpty() ||
                    e.getObjectCode().isBlank() ||
                    e.getObjectCode() == null) continue;
            System.out.printf("%s ", e.getObjectCode());
        }
        System.out.println();
    }

    private static void outputToFile(String fileName) throws FileNotFoundException {
        File file = new File(currentPath + getOutputFileName(fileName) + ".txt");
        PrintStream printStream = new PrintStream(file);
        encodedIntroduction.stream().filter(
                e -> !e.getObjectCode().isEmpty() && !e.getObjectCode().isBlank() && e.getObjectCode() != null
        ).forEach(e -> {
            printStream.print(e.getObjectCode());
            if (encodedIntroduction.indexOf(e) == (encodedIntroduction.size() - 1)) {
                printStream.println();
            } else {
                printStream.print(" ");
            }
        });
        printStream.close();
    }

    private static String getOutputFileName(String fileName) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find() ? matcher.group() :("OUTPUT-" + fileName);
    }
}