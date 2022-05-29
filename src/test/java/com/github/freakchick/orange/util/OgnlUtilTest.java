package com.github.freakchick.orange.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/3/5
 **/
public class OgnlUtilTest {

    @Test
    void testGetValue(){
        Map<String, Object> root = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(22);
        list.add(32);
        list.add(42);
        root.put("ids", list);

        Object o = OgnlUtil.getValue("ids[3]", root);
        assertTrue(o instanceof Integer);
        assertEquals(42,o);
    }



    @Test
    void shouldCompareStringsReturnTrue() {
        String expression = "username == 'cbegin'";
        Map<String, Object> root = new HashMap<>();
        root.put("username","cbegin");
        boolean value = OgnlUtil.getBooleanValue(expression, root);
        assertTrue(value);
    }

    @Test
    void shouldCompareStringsReturnFalse() {
        String expression = "username == 'norm'";
        Map<String, Object> root = new HashMap<>();
        root.put("username","cbegin");
        boolean value = OgnlUtil.getBooleanValue(expression, root);
        assertFalse(value);
    }

    @Test
    void shouldReturnTrueIfNotNull() {
        // boolean value = evaluator.evaluateBoolean("username", new Author(1, "cbegin", "******", "cbegin@apache.org", "N/A", Section.NEWS));
        // assertTrue(value);
    }

    @Test
    void shouldReturnFalseIfNull() {
        // boolean value = evaluator.evaluateBoolean("password", new Author(1, "cbegin", null, "cbegin@apache.org", "N/A", Section.NEWS));
        // assertFalse(value);
    }

    @Test
    void shouldReturnTrueIfNotZero() {
        String expression = "id";
        Map<String, Object> root = new HashMap<>();
        root.put("id",1);
        boolean value = OgnlUtil.getBooleanValue(expression, root);
        assertTrue(value);
    }

    @Test
    void shouldReturnFalseIfZero() {
        String expression = "id";
        Map<String, Object> root = new HashMap<>();
        root.put("id",0);
        boolean value = OgnlUtil.getBooleanValue(expression, root);
        assertFalse(value);
    }

    @Test
    void shouldReturnFalseIfZeroWithScale() {
        String expression = "id";
        Map<String, Object> root = new HashMap<>();
        root.put("id",0.0d);
        boolean value = OgnlUtil.getBooleanValue(expression, root);
        assertFalse(value);
    }

    @Test
    void shouldIterateOverIterable() {
        final HashMap<String, Object> parameterObject = new HashMap<String, Object>() {{
            put("array", new String[]{"1", "2", "3"});
        }};
        final Iterable<?> iterable = OgnlUtil.getIterable("array",parameterObject);
        int i = 0;
        for (Object o : iterable) {
            assertEquals(String.valueOf(++i), o);
        }
    }
}
