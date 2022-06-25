package com.github.freakchick.orange.node.set;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.domain.MapParam;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/25
 **/
public class SetSqlNodeTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testSet() {
        String sql = "update Author\n" +
                "    <set>\n" +
                "      <if test=\"username != null\">username=#{username},</if>\n" +
                "      <if test=\"password != null\">password=#{password},</if>\n" +
                "      <if test=\"email != null\">email=#{email},</if>\n" +
                "      <if test=\"bio != null\">bio=#{bio}</if>\n" +
                "    </set>\n" +
                "  where id=#{id}";
        MapParam mapParam = new MapParam();
        mapParam.put("id", 1);
        mapParam.put("username", "Jensan");

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        System.out.println(meta.getSql());
        meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("update Author\n" +
                "     SET username=?\n" +
                "  where id=?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(2));

        mapParam.clear();
        mapParam.put("id", 1);
        mapParam.put("password", "passw0rd");

        meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("update Author\n" +
                "     SET password=?\n" +
                "  where id=?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(2));
    }
}
