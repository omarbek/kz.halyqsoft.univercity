package kz.halyqsoft.univercity.utils;

import org.apache.commons.lang3.math.NumberUtils;

public class FieldValidator {

    public static boolean checkForEmpty(String value){
        if(value!=null){
            return !value.trim().equals("");
        }
        return false;
    }

    public static boolean isNumber(String value){
        if(value!=null){
            if(!value.trim().equals(""))
            {
                return NumberUtils.isDigits(value);
            }
        }
        return false;
    }
}
