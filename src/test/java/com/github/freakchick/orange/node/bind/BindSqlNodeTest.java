package com.github.freakchick.orange.node.bind;

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
 * @create: 2022/6/24
 **/
public class BindSqlNodeTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testBind() {
        String sql = "<bind name=\"pattern\" value=\"'%' + _parameter.title + '%'\" />\n" +
                "  SELECT * FROM BLOG\n" +
                "  WHERE title LIKE #{pattern}";
        MapParam mapParam = new MapParam();
        MapParam _parameter = new MapParam();
        _parameter.put("title", "titleValue");
        mapParam.put("_parameter", _parameter.getMap());

        SqlMeta meta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(meta.getSql());
        //meta.getJdbcParamValues().forEach(System.out::println);
        assertThat(meta.getSql(), is("\n" +
                "  SELECT * FROM BLOG\n" +
                "  WHERE title LIKE ?"));
        assertThat(meta.getJdbcParamValues(), iterableWithSize(1));
    }
}
