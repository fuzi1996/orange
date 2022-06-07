package com.github.freakchick.orange.node.foreach;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.domain.MapParam;
import com.github.freakchick.orange.domain.User;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/6
 **/
public class ForeachSqlNodeMapTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void shouldGetStringKeyStringValueEntries() {
        String sql = (
                "        insert into string_string (key, value) values\n" +
                "        <foreach item=\"item\" index=\"key\" collection=\"map\"\n" +
                "            open=\"\" separator=\",\" close=\"\">(#{key}, #{item})</foreach>"
        );
        MapParam mapParam = new MapParam();
        mapParam.getMap().put("key_1", "value 1");
        mapParam.getMap().put("key_2", "value 2");

        SqlMeta sqlMeta = this.engine.parse(sql, mapParam.getParam());
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("        insert into string_string (key, value) values\n" +
                "         (?, ?),(?, ?)",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(4,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(3,strings.size());
    }

    @Test
    public void shouldGetNestedBeanKeyValueEntries() {
        String sql = (
                "select count(*) from key_cols where\n" +
                        "        <foreach item=\"item\" index=\"key\" collection=\"map\"\n" +
                        "            open=\"\" separator=\"AND\" close=\"\">${key} = #{item}</foreach>"
        );
        MapParam mapParam = new MapParam();
        mapParam.getMap().put("col_a", 22);
        mapParam.getMap().put("col_b", 222);

        SqlMeta sqlMeta = this.engine.parse(sql, mapParam.getParam());
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select count(*) from key_cols where\n" +
                // 要的就是连在一起的效果
                "         col_a = ?ANDcol_b = ?",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(2,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(3,strings.size());
    }
}
