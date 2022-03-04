package com.github.freakchick.orange.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 刘政杉
 * date 3/4/2022
 */
public class OgnlUtilTest {

    @Test
    public void testGetValue(){
        Map<String, Object> root = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(22);
        list.add(32);
        list.add(42);
        root.put("ids", list);

        Object o = OgnlUtil.getValue("ids[3]", root);
        Assert.assertTrue(o instanceof Integer);
        Assert.assertEquals(42,o);
    }
}
