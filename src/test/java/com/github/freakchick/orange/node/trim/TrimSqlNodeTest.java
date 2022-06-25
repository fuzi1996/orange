package com.github.freakchick.orange.node.trim;

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
public class TrimSqlNodeTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testTrim() {
        String sql = "SELECT * FROM BLOG\n" +
                "  <trim prefix=\"where\" prefixesToOverride=\"AND |OR\">\n" +
                "    <if test=\"state != null\">\n" +
                "        OR state = #{state}\n" +
                "    </if>\n" +
                "    <if test=\"title != null\">\n" +
                "        AND title like #{title}\n" +
                "    </if>\n" +
                "    <if test=\"name != null\">\n" +
                "        OR author_name like #{name}\n" +
                "    </if>\n" +
                "  </trim>";
        MapParam mapParam = new MapParam();
        mapParam.put("state", 1);

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("SELECT * FROM BLOG\n" +
                "   where state = ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(1));
    }
}
