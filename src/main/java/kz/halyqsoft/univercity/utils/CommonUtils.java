package kz.halyqsoft.univercity.utils;

import org.r3a.common.entity.ID;

import java.util.Random;

/**
 * @author Omarbek
 * @created on 15.03.2018
 */
public class CommonUtils {
    public static String getCode(Integer count) {
        String code = String.valueOf(count);
        if (count < 10) {
            code = "000" + code;
        } else if (count < 100) {
            code = "00" + code;
        } else if (count < 1000) {
            code = "0" + code;
        }
        return code;
    }

    public static String getCodeBuilder(Integer count) {
        String code = String.valueOf(count);
        StringBuilder codeSB = new StringBuilder();
        if (count < 10) {
            codeSB.append("000");
        } else if (count < 100) {
            codeSB.append("00");
        } else if (count < 1000) {
            codeSB.append("0");
        }
        codeSB.append(code);
        return codeSB.toString();
    }

    public static void main(String[] args) {
        int a = 1;
        String code = getCode(a);
        String codeBuilder = getCodeBuilder(a);
        System.out.println("code: "+code);
        System.out.println("builder: "+codeBuilder);
    }
}
