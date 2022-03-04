package com.github.freakchick.orange;

import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @program: orange
 * @description:
 * @author: jiangqiang
 * @create: 2021-02-23 10:19
 **/
public class TestOrange {

    @Test
    public void testIf() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "id &lt;= #{maxId}";
        Map<String, Object> map = new HashMap<>();
        map.put("maxId", 10);

        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("id <= ?",sqlMeta.getSql());
        List<Object> jdbcParamValues = sqlMeta.getJdbcParamValues();
        // jdbcParamValues.forEach(System.out::println);
        Assert.assertTrue(null != jdbcParamValues);
        Assert.assertTrue(jdbcParamValues.size() == 1);
    }

    @Test
    public void testTrim() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql =
                "<trim prefix='(' suffix=')' suffixesToOverride=',' prefixesToOverride='and' >\n" +
                "  <foreach collection='list' index='idx' open='(' separator=',' close=')'>\n" +
                "    #{item.name} == #{idx}\n" +
                "  </foreach>\n" +
                "  <if test='id!=null'>  and xyz.,</if>\n" +
                "</trim>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        String expected = " ((\n" +
                "    ? == ?\n" +
                "  ,\n" +
                "    ? == ?\n" +
                "  )\n" +
                "     and xyz.)";

        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals(expected,sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(4,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testWhere() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<where><if test='id!=null'>  and id = #{id}</if><if test='id!=null'>  and id = #{id}</if></where>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals(" WHERE id = ?   and id = ?",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(2,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "select * from user where name in " +
                "<foreach collection='list' index='idx' open='(' separator=',' close=')'>" +
                "#{item.name}== #{idx}" +
                "</foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList.toArray());

        SqlMeta sqlMeta = engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select * from user where name in  (?== ?,?== ?)",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(4,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testForeachIF() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "select * from user where name in " +
                "<foreach collection='list' index='idx' open='(' separator=',' close=')'>" +
                        "#{item.name}== #{idx}" +
                        "<if test='id!=null'>  " +
                        "   and id = #{id}" +
                        "</if>" +
                "</foreach>"
        );
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList.toArray());
        map.put("id", 100);

        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select * from user where name in  (?== ?      and id = ?,?== ?      and id = ?)",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(6,sqlMeta.getJdbcParamValues().size());
    }

// TODO
//    @Test
//    public void testForeachMap() {
//        DynamicSqlEngine engine = new DynamicSqlEngine();
//        String sql = (
//                "<foreach collection='users' open='(' separator=',' close=')'>" +
//                        "#{item}" +
//                "</foreach>"
//        );
//        Map<String, Object> map = new HashMap<>();
//
//        Map<String, Object> users = new HashMap<String, Object>() {
//            {
//                put("aaa", "a1");
//                put("bbb", "b1");
//            }
//        };
//
//        map.put("users", users);
//
//        SqlMeta sqlMeta = engine.parse(sql, map);
//        System.out.println(sqlMeta.getSql());
//        sqlMeta.getJdbcParamValues().forEach(System.out::println);
//    }


    @Test
    public void testMultiForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "<foreach collection='list' open='(' separator=',' close=')'>" +
                    "#{item}" +
                "</foreach>" +
                "<foreach collection='list2' open='{' separator=',' close='}'>" +
                        "#{item}" +
                "</foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<String> list = new ArrayList<String>() {{
            add("a");
            add("b");
        }};

        map.put("list", list);

        ArrayList<String> list2 = new ArrayList<String>() {{
            add("c");
            add("d");
        }};

        map.put("list2", list2.toArray());

        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals(" (?,?) {?,?}",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(4,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testSet() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "update " +
                    "<set>" +
                        "<if test='id !=null'> " +
                            "id = #{id} ," +
                        "</if>" +
                        "<if test='id !=null'> " +
                            "id = #{id} , " +
                        "</if>" +
                    "</set>");
        Map<String, Object> map = new HashMap<>();
        map.put("id",10);
        SqlMeta sqlMeta = engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("update  SET id = ? ,  id = ? ",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(2,sqlMeta.getJdbcParamValues().size());

    }

    @Test
    public void testParseParam() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "<foreach collection='list' open='(' separator=',' close=')'>" +
                    "#{item.name} #{item} #{id} ${indexName} " +
                "</foreach>" +
                "<where>" +
                    "<if test='id!=null'>  " +
                        "and id = #{mid}" +
                    "</if> " +
                    "${name}" +
                "</where>");
        Set<String> set = engine.parseParameter(sql);
        // set.stream().forEach(System.out::println);
        Assert.assertEquals(7,set.size());
    }

}
