package com.github.freakchick.orange.node.choose;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.domain.MapParam;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/20
 **/
public class ChooseSqlNodeTest {
    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testParseChoose() {
        String sql = (
                "SELECT * FROM BLOG WHERE state = ‘ACTIVE’\n" +
                        "  <choose>\n" +
                        "    <when test=\"title != null\">\n" +
                        "      AND title like #{title}\n" +
                        "    </when>\n" +
                        "    <when test=\"author != null and author.name != null\">\n" +
                        "      AND author_name like #{author.name}\n" +
                        "    </when>\n" +
                        "    <otherwise>\n" +
                        "      AND featured = 1\n" +
                        "    </otherwise>\n" +
                        "  </choose>"
        );
        MapParam mapParam = new MapParam();

        SqlMeta sqlMeta = this.engine.parse(sql, mapParam.getParam());
        assertThat(sqlMeta.getSql(), is("SELECT * FROM BLOG WHERE state = ‘ACTIVE’\n" +
                "  \n" +
                "      AND featured = 1\n" +
                "    "));


        mapParam.put("title", "value 1");
        mapParam.put("key_2", "value 2");

        sqlMeta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("SELECT * FROM BLOG WHERE state = ‘ACTIVE’\n" +
                "   \n" +
                "      AND title like ?\n" +
                "    "));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(1));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(2));

        mapParam.clear();
        MapParam author = new MapParam();
        author.put("name", "authorName");

        mapParam.put("author", author.getMap());
        sqlMeta = this.engine.parse(sql, mapParam.getMap());
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("SELECT * FROM BLOG WHERE state = ‘ACTIVE’\n" +
                "   \n" +
                "      AND author_name like ?\n" +
                "    "));
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(1));

    }
}
