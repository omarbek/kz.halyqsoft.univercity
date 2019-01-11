package kz.halyqsoft.univercity.modules.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Gold {
    public static void main(String[] argv) throws IOException {
        new Gold().run();
    }

    public void run() throws IOException {
        Scanner scanner = new Scanner(new File("input.txt"));
        PrintWriter printWriter = new PrintWriter(new File("output.txt"));

        String first = scanner.next();
        String second = scanner.next();
        String third = scanner.next();
        if (first.length() < 100 && second.length() < 100 && third.length() < 100
                && check(first) && check(second) && check(third)
                && !first.startsWith("0") && !second.startsWith("0") && !third.startsWith("0")) {

            String max = first;
            max = getString(second, max);
            max = getString(third, max);

            printWriter.print(max);
        } else {
            printWriter.print("ERROR");
        }
        printWriter.close();
    }

    private boolean check(String first) {
        for (int i = 0; i < first.length(); i++) {
            try {
                int firstNumber = Integer.parseInt(first.substring(i, i + 1));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private String getString(String second, String max) {
        int compareStrings = second.compareTo(max);
        if (compareStrings > 0) {
            max = second;
        }
//        if (compareStrings == 0) {
//            max = getString(second, max, 0);
//        }
        return max;
    }

//    private String getString(String second, String max, int i) {
//        if (i < second.length()) {
//            int secondNumber = Integer.parseInt(second.substring(i, i + 1));
//            int maxNumber = Integer.parseInt(max.substring(i, i + 1));
//            if (secondNumber > maxNumber) {
//                max = second;
//            }
//            if (secondNumber == maxNumber) {
//                max = getString(second, max, ++i);
//            }
//        }
//        return max;
//    }
}