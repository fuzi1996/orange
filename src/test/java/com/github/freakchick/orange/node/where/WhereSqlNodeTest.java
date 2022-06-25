package com.github.freakchick.orange.node.where;

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
public class WhereSqlNodeTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testWhere() {
        String sql = "SELECT * FROM BLOG\n" +
                "  <where>\n" +
                "    <if test=\"state != null\">\n" +
                "         state = #{state}\n" +
                "    </if>\n" +
                "    <if test=\"title != null\">\n" +
                "        AND title like #{title}\n" +
                "    </if>\n" +
                "    <if test=\"name != null\">\n" +
                "        AND author_name like #{name}\n" +
                "    </if>\n" +
                "  </where>";
        MapParam mapParam = new MapParam();
        mapParam.put("state", 1);

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("SELECT * FROM BLOG\n" +
                "   WHERE state = ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(1));
    }

    @Test
    public void testWhere2() {
        String sql = "SELECT * FROM BLOG\n" +
                "  <where>\n" +
                "    <if test=\"state != null\">\n" +
                "         state = #{state}\n" +
                "    </if>\n" +
                "    <if test=\"title != null\">\n" +
                "        AND title like #{title}\n" +
                "    </if>\n" +
                "    <if test=\"name != null\">\n" +
                "        OR author_name like #{name}\n" +
                "    </if>\n" +
                "  </where>";
        MapParam mapParam = new MapParam();
        mapParam.put("title", "titleValue");

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("SELECT * FROM BLOG\n" +
                "   WHERE title like ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(1));
    }

    @Test
    public void testWhere3() {
        String sql = "SELECT * FROM BLOG\n" +
                "  <where>\n" +
                "    <if test=\"state != null\">\n" +
                "         state = #{state}\n" +
                "    </if>\n" +
                "    <if test=\"title != null\">\n" +
                "        AND title like #{title}\n" +
                "    </if>\n" +
                "    <if test=\"name != null\">\n" +
                "        OR author_name like #{name}\n" +
                "    </if>\n" +
                "  </where>";
        MapParam mapParam = new MapParam();
        mapParam.put("name", "nameValue");

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("SELECT * FROM BLOG\n" +
                "   WHERE author_name like ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(1));
    }

    @Test
    public void testWhere5() {
        String sql = "SELECT * FROM BLOG\n" +
                "  <where>\n" +
                "    <if test=\"state != null\">\n" +
                "        and state = #{state}\n" +
                "    </if>\n" +
                "    <if test=\"title != null\">\n" +
                "        AND title like #{title}\n" +
                "    </if>\n" +
                "    <if test=\"name != null\">\n" +
                "        OR author_name like #{name}\n" +
                "    </if>\n" +
                "  </where>";
        MapParam mapParam = new MapParam();
        mapParam.put("state", 1);
        mapParam.put("title", "titleValue");
        mapParam.put("name", "nameValue");

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        System.out.println(meta.getSql());
        meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("SELECT * FROM BLOG\n" +
                "   WHERE state = ?\n" +
                "    \n" +
                "     \n" +
                "        AND title like ?\n" +
                "    \n" +
                "     \n" +
                "        OR author_name like ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(3));
    }
}
