package com.IGsystem.utils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class IntegerListConvert {
    public static String convertIntegerListToString(List<Integer> integerList) {
        StringJoiner joiner = new StringJoiner(",");
        for (Integer num : integerList) {
            joiner.add(num.toString());
        }
        return joiner.toString();
    }

    public static List<Integer> convertStringToIntegerList(String input) {
        String[] stringArray = input.split(",");
        Integer[] intArray = new Integer[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }
        return Arrays.asList(intArray);
    }
}
