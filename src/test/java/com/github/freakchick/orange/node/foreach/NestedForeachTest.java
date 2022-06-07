package com.github.freakchick.orange.node.foreach;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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
        name1.put("lastName","name1LastName");
        names.add(name1);
        Map<String, Object> name2 = new HashMap<>();
        name2.put("lastName","name2LastName");
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("    select *\n" +
                "    from names\n" +
                "     WHERE lastName = ?\n" +
                "      or\n" +
                "        lastName = ?",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(2,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(2,strings.size());
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
        Assert.assertEquals("    select *\n" +
                "    from names\n" +
                "     WHERE id in (\n" +
                "        1\n" +
                "      ,\n" +
                "        3\n" +
                "      ,\n" +
                "        5\n" +
                "      )",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(2,strings.size());
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
        Assert.assertEquals("    select *\n" +
                "    from names\n" +
                "     WHERE id in (\n" +
                "        ?\n" +
                "      ,\n" +
                "        ?\n" +
                "      ,\n" +
                "        ?\n" +
                "      )",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(3,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(2,strings.size());
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
        name2.put("lastName","name2LastName");
        List<String> firstNames2 = new ArrayList<>();
        firstNames2.add("name2FirstName1");
        firstNames2.add("name2FirstName2");
        name2.put("firstNames",firstNames2);
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("    select *\n" +
                "    from names\n" +
                "     WHERE (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        \n" +
                "      or\n" +
                "         \n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(8,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(4,strings.size());
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
        name2.put("lastName","name2LastName");
        List<String> firstNames2 = new ArrayList<>();
        firstNames2.add("name2FirstName1");
        name2.put("firstNames",firstNames2);
        names.add(name2);

        map.put("names", names);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("    select *\n" +
                "    from names\n" +
                "     WHERE (lastName = ? and firstName = ?)\n" +
                "        or\n" +
                "          (lastName = ? and firstName = ?)\n" +
                "        \n" +
                "      or\n" +
                "         \n" +
                "          (lastName = ? and firstName = ?)",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(6,sqlMeta.getJdbcParamValues().size());

        Set<String> strings = this.engine.parseParameter(sql);
        //strings.forEach(System.out::println);
        Assert.assertEquals(4,strings.size());
    }
}
