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
        int sumOfFifthCourse=0;
        int sumOfThirdCourse=0;
        int sumOfFirstCourse=0;
        int currentSum;
        for(int i=0;i<array.length;i++){
            currentSum=sumOfFifthCourse+array[i];
            sumOfFifthCourse+=currentSum;
        }

        printWriter.print(sumOfFifthCourse);
        printWriter.close();
    }
}
