package com.github.freakchick.orange.util;


public class RegexUtil {

    public static String replace(String content, String item, String newItem) {
        return content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", newItem);
    }
}
