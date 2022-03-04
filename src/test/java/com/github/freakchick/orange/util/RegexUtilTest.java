package com.github.freakchick.orange.util;

import org.junit.Test;

/**
 *
 * @author 刘政杉
 * date 3/4/2022
 */
public class RegexUtilTest {

    @Test
    public void testReplace(){
        String replace = RegexUtil.replace("item[0].name", "item", "aa");
    }

}
