package com.github.freakchick.orange.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.sun.corba.se.spi.orb.PropertyParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/3/5
 **/
public class RegexUtilTest {

    @Test
    public void testReplace(){
        String replace = RegexUtil.replace("item:name", "item", "aa");
        assertEquals("aa:name",replace);
        String replace1 = RegexUtil.replace("a:b", "c", "aa");
        assertEquals("a:b",replace1);
    }
}
