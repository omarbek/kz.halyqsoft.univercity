package kz.halyqsoft.univercity.utils;

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
}
