package com.github.freakchick.orange.node.foreach;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/7
 **/
public class NestedForeachTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testSimpleSelect(){
        String sql = (
                "    select *\n" +
                        "    from names\n" +
                        "    <where>\n" +
                        "      <foreach collection=\"names\" item=\"name\" separator=\"or\">\n" +
                        "        lastName = #{name.lastName}\n" +
                        "      </foreach>\n" +
                        "    </where>"
        );
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> names = new ArrayList<>();
        Map<String, Object> name1 = new HashMap<>();
        name1.put("lastName", "name1LastName");
        names.add(name1);
        Map<String, Object> name2 = new HashMap<>();
        name2.put("lastName", "name2LastName");
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("    select *\n" +
                "    from names\n" +
                "     WHERE lastName = ?\n" +
                "      or\n" +
                "        lastName = ?"));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(2));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(2));
    }

    @Test
    public void testSimpleSelectWithPrimitives(){
        String sql = (
                "    select *\n" +
                        "    from names\n" +
                        "    <where>\n" +
                        "      <foreach collection=\"ids\" item=\"id\" separator=\",\" open=\"id in (\" close=\")\">\n" +
                        "        ${id}\n" +
                        "      </foreach>\n" +
                        "    </where>"
        );
        Map<String, Object> map = new HashMap<>();

        int[] ids = new int[] { 1, 3, 5 };
        map.put("ids", ids);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("    select *\n" +
                "    from names\n" +
                "     WHERE id in (\n" +
                "        1\n" +
                "      ,\n" +
                "        3\n" +
                "      ,\n" +
                "        5\n" +
                "      )"));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(0));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(2));
    }

    @Test
    public void testSimpleSelectWithMapperAndPrimitives(){
        String sql = (
                "    select *\n" +
                        "    from names\n" +
                        "    <where>\n" +
                        "      <foreach collection=\"ids\" item=\"id\" separator=\",\" open=\"id in (\" close=\")\">\n" +
                        "        #{id}\n" +
                        "      </foreach>\n" +
                        "    </where>"
        );
        Map<String, Object> map = new HashMap<>();

        int[] ids = new int[] { 1, 3, 5 };
        map.put("ids", ids);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("    select *\n" +
                "    from names\n" +
                "     WHERE id in (\n" +
                "        ?\n" +
                "      ,\n" +
                "        ?\n" +
                "      ,\n" +
                "        ?\n" +
                "      )"));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(3));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(2));
    }

    @Test
    public void testNestedSelect(){
        String sql = (
                "    select *\n" +
                        "    from names\n" +
                        "    <where>\n" +
                        "      <foreach collection=\"names\" item=\"name\" separator=\"or\">\n" +
                        "        <foreach collection=\"name.firstNames\" item=\"firstName\" separator=\"or\">\n" +
                        "          (lastName = #{name.lastName} and firstName = #{firstName})\n" +
                        "        </foreach>\n" +
                        "      </foreach>\n" +
                        "    </where>"
        );
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> names = new ArrayList<>();
        Map<String, Object> name1 = new HashMap<>();
        name1.put("lastName","name1LastName");
        List<String> firstNames1 = new ArrayList<>();
        firstNames1.add("name1FirstName1");
        firstNames1.add("name1FirstName2");
        name1.put("firstNames",firstNames1);
        names.add(name1);
        Map<String, Object> name2 = new HashMap<>();
        name2.put("lastName", "name2LastName");
        List<String> firstNames2 = new ArrayList<>();
        firstNames2.add("name2FirstName1");
        firstNames2.add("name2FirstName2");
        name2.put("firstNames", firstNames2);
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("    select *\n" +
                "    from names\n" +
                "     WHERE (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        \n" +
                "      or\n" +
                "         \n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)"));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(8));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(4));
    }

    @Test
    public void testNestedSelect2(){
        String sql = (
                "    select *\n" +
                        "    from names\n" +
                        "    <where>\n" +
                        "      <foreach collection=\"names\" item=\"name\" separator=\"or\">\n" +
                        "        <foreach collection=\"name.firstNames\" item=\"firstName\" separator=\"or\">\n" +
                        "          (lastName = #{name.lastName} and firstName = #{firstName})\n" +
                        "        </foreach>\n" +
                        "      </foreach>\n" +
                        "    </where>"
        );
        Map<String, Object> map = new HashMap<>();

        List<Map<String, Object>> names = new ArrayList<>();
        Map<String, Object> name1 = new HashMap<>();
        name1.put("lastName","name1LastName");
        List<String> firstNames1 = new ArrayList<>();
        firstNames1.add("name1FirstName1");
        firstNames1.add("name1FirstName2");
        name1.put("firstNames",firstNames1);
        names.add(name1);
        Map<String, Object> name2 = new HashMap<>();
        name2.put("lastName", "name2LastName");
        List<String> firstNames2 = new ArrayList<>();
        firstNames2.add("name2FirstName1");
        name2.put("firstNames", firstNames2);
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        assertThat(sqlMeta.getSql(), is("    select *\n" +
                "    from names\n" +
                "     WHERE (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        \n" +
                "      or\n" +
                "         \n" +
                "          (lastName = ? and firstName = ?)"));
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        assertThat(sqlMeta.getJdbcParamValues(), iterableWithSize(6));

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        assertThat(strings, iterableWithSize(4));
    }
}
