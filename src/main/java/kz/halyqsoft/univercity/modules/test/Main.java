package kz.halyqsoft.univercity.modules.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    public static void main(String[] argv) throws IOException {
        new Main().run();
    }

    private PrintWriter printWriter;

    public void run() throws IOException {
        Scanner scanner = new Scanner(new File("input.txt"));
        printWriter = new PrintWriter(new File("output.txt"));
        int n = scanner.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = scanner.nextInt();
        }
        int sum = 0;
        int max = array[0];
        int maxNumber = 0;
        int min = array[0];
        int minNumber = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                sum += array[i];
            }
            if (array[i] > max) {
                max = array[i];
                maxNumber = i;
            }
            if (array[i] < min) {
                min = array[i];
                minNumber = i;
            }
        }
        int mult = 1;
        if (maxNumber > minNumber) {
            for (int i = minNumber + 1; i < maxNumber; i++) {
                mult *= array[i];
            }
        } else {
            for (int i = maxNumber + 1; i < minNumber; i++) {
                mult *= array[i];
            }
        }
        printWriter.print(sum + " " + mult);
        printWriter.close();
    }
}
