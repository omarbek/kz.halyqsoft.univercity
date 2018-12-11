package kz.halyqsoft.univercity.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created on 26.04.2018
 */
public class TestUtils {
    public static void main(String[] args) {
        List<Integer> a=new ArrayList<>();
        a.add(1);
        a.add(2);
        List<Integer> b=new ArrayList<>();
        b.add(3);
        b.add(4);
        b.add(2);
        a.addAll(b);
        for(Integer i:a){
            System.out.println(i);
        }
        System.out.println();
        for (Integer i:b){
            System.out.println(i);
        }
    }
}
