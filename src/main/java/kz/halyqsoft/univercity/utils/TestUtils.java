package kz.halyqsoft.univercity.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author Omarbek
 * @created on 26.04.2018
 */
public class TestUtils {
    public static void main(String[] args) {
        String a = "1";
        String b = "s";
        if (NumberUtils.isCreatable(a)) {
            System.out.println("a is number");
        }
        if (NumberUtils.isCreatable(b)) {
            System.out.println("b is number");
        }
    }
}
