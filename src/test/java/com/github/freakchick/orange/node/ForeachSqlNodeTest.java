package com.github.freakchick.orange.node;

import com.github.freakchick.orange.SqlMeta;
import com.github.freakchick.orange.User;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/3/5
 **/
public class ForeachSqlNodeTest {

    private DynamicSqlEngine engine = new DynamicSqlEngine();

    @Test
    public void testEmptyIterableForeach() {
        String sql = (
                "select * from user " +
                        "where name in " +
                        "<foreach collection='list' index='idx' open='(' separator=',' close=')'>" +
                            "#{item.name} == #{idx}" +
                            "<if test='id != null'>  " +
                                "and id = #{id}" +
                            "</if>" +
                        "</foreach>"
        );
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        map.put("list", arrayList.toArray());
        map.put("id", 100);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select * from user where name in ",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testUnEmptyIterableForeach2() {
        String sql = (
                "select * from user " +
                        "where name in " +
                        "<foreach collection='list' index='idx' open='(' separator=',' close=')'>" +
                        "#{item.name} == #{idx}" +
                        "<if test='id != null'>  " +
                        "and id = #{id}" +
                        "</if>" +
                        "</foreach>"
        );
        Map<String, Object> map = new HashMap<>();

        User user = new User(10,"name");

        ArrayList<User> arrayList = new ArrayList<>();

        arrayList.add(user);
        map.put("list", arrayList.toArray());
        map.put("id", 100);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select * from user where name in  (? == ?   and id = ?)",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(3,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void testOrderForeach() {
        String sql = (
                "select * from author" +
                "<foreach item=\"item\" collection=\"orderConditions\" separator=\",\" open=\"order by\" close=\" \" index=\"index\">" +
                "    ${item}" +
                "</foreach>"
        );
        Map<String, Object> map = new HashMap<>();

        List<String> columns = new ArrayList<>();
        columns.add("rank");
        columns.add("age");
        columns.add("id");
        map.put("orderConditions", columns);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        Assert.assertEquals("select * from author order by    rank,    age,    id ",sqlMeta.getSql());
        Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void shouldGetAUser(){
        String sql = (
                "select * from users\n" +
                "         WHERE id in\n" +
                "        <foreach item=\"item\" index=\"index\" collection=\"friendList\"\n" +
                "          separator=\",\" open=\"(\" close=\")\">\n" +
                "          ${item.id}\n" +
                "        </foreach>"
        );
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> friend = new HashMap<>();
        friend.put("id",6);
        List<Map<String, Object>> friendList = new ArrayList<>();
        friendList.add(friend);
        map.put("friendList", friendList);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select * from users\n" +
                "         WHERE id in\n" +
                "         (\n" +
                "          6\n" +
                "        )",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());
    }
// https://gitee.com/freakchicken/orange/issues/I5APFR
//    @Test
//    public void shouldHandleComplexNullItem(){
//        String sql = (
//                "select count(*) from users\n" +
//                        "      <where>\n" +
//                        "        id in\n" +
//                        "        <foreach item=\"item\" collection=\"list\" separator=\",\" open=\"(\" close=\")\">\n" +
//                        "          #{item.id}\n" +
//                        "        </foreach>\n" +
//                        "      </where>"
//        );
//        Map<String, Object> map = new HashMap<>();
//        Map<String, Object> friend = new HashMap<>();
//        friend.put("id",6);
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(friend);
//        list.add(null);
//        map.put("list", list);
//
//        SqlMeta sqlMeta = this.engine.parse(sql, map);
//        System.out.println(sqlMeta.getSql());
//        //Assert.assertEquals("select * from users\n",sqlMeta.getSql());
//        sqlMeta.getJdbcParamValues().forEach(System.out::println);
//        //Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());
//    }

//    @Test
//    public void shouldHandleMoreComplexNullItem(){
//        String sql = (
//                "select count(*) from users\n" +
//                        "      <where>\n" +
//                        "        id in\n" +
//                        "        <foreach item=\"item\" collection=\"list\" separator=\",\" open=\"(\" close=\")\">\n" +
//                        "          #{item.bestFriend.id}\n" +
//                        "        </foreach>\n" +
//                        "      </where>"
//        );
//        Map<String, Object> map = new HashMap<>();
//        Map<String, Object> bestFriend = new HashMap<>();
//        bestFriend.put("id",5);
//        Map<String, Object> friend = new HashMap<>();
//        friend.put("bestFriend",bestFriend);
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(friend);
//        list.add(null);
//        map.put("list", list);
//
//        SqlMeta sqlMeta = this.engine.parse(sql, map);
//        System.out.println(sqlMeta.getSql());
//        //Assert.assertEquals("select * from users\n",sqlMeta.getSql());
//        sqlMeta.getJdbcParamValues().forEach(System.out::println);
//        //Assert.assertEquals(0,sqlMeta.getJdbcParamValues().size());
//    }

    @Test
    public void nullItemInContext(){
        String sql = (
                "select name from users\n" +
                        "      <where>\n" +
                        "        id in\n" +
                        "        <foreach item=\"item\" collection=\"list\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "          <if test=\"item != null\">\n" +
                        "            #{item.id}\n" +
                        "          </if>\n" +
                        "        </foreach>\n" +
                        "      </where>"
        );
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> friend = new HashMap<>();
        friend.put("id",3);
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(friend);
        list.add(null);
        map.put("list", list);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        //System.out.println(sqlMeta.getSql());
        Assert.assertEquals("select name from users\n" +
                "       WHERE id in\n" +
                "         (\n" +
                "           \n" +
                "            ?\n" +
                "          \n" +
                "        \n" +
                "          \n" +
                "        )",sqlMeta.getSql());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(1,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void shouldReportMissingPropertyName(){
        String sql = (
                "insert into users (id, name) values\n" +
                        "    <foreach item=\"item\" collection=\"list\" separator=\",\">\n" +
                        "        (#{item.idd}, #{item.name})\n" +
                        "    </foreach>"
        );
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> friend = new HashMap<>();
        friend.put("id",3);
        friend.put("name","aname");
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(friend);
        map.put("list", list);

        try {
            this.engine.parse(sql, map);
            Assert.assertTrue(false);
        }catch (Exception e){
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("could not found value : list[0].idd",e.getMessage());
        }
    }

    @Test
    public void shouldRemoveItemVariableInTheContext(){
        String sql = (
                "    select count(*) from users where id in\n" +
                "    <foreach collection=\"ids\" item=\"id\" open=\"(\" close=\")\" separator=\",\">\n" +
                "      <foreach collection=\"ids2\" item=\"id\">\n" +
                "        #{id},\n" +
                "      </foreach>\n" +
                "      #{id}\n" +
                "    </foreach>\n" +
                "    or id = #{id}"
        );
        Map<String, Object> map = new HashMap<>();

        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        ids.add(3);

        List<Integer> ids2 = new ArrayList<>();
        ids2.add(4);
        ids2.add(5);

        map.put("id", 1);
        map.put("ids", ids);
        map.put("ids2", ids2);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("    select count(*) from users where id in\n" +
                "     (\n" +
                "       \n" +
                "        ?,\n" +
                "      \n" +
                "        ?,\n" +
                "      \n" +
                "      ?\n" +
                "    ,\n" +
                "       \n" +
                "        ?,\n" +
                "      \n" +
                "        ?,\n" +
                "      \n" +
                "      ?\n" +
                "    )\n" +
                "    or id = ?",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(7,sqlMeta.getJdbcParamValues().size());
    }

    @Test
    public void shouldRemoveIndexVariableInTheContext(){
        String sql = (
                "    select count(*) from users where id in\n" +
                "    <foreach collection=\"idxs\" index=\"idx\" open=\"(\" close=\")\" separator=\",\">\n" +
                "      <foreach collection=\"idxs2\" index=\"idx\">\n" +
                "        #{idx},\n" +
                "      </foreach>\n" +
                "      #{idx} + 2\n" +
                "    </foreach>\n" +
                "    or id = #{idx}"
        );
        Map<String, Object> map = new HashMap<>();

        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        ids.add(3);

        List<Integer> ids2 = new ArrayList<>();
        ids2.add(4);
        ids2.add(5);

        map.put("idx", 1);
        map.put("idxs", ids);
        map.put("idxs2", ids2);

        SqlMeta sqlMeta = this.engine.parse(sql, map);
        // System.out.println(sqlMeta.getSql());
        Assert.assertEquals("    select count(*) from users where id in\n" +
                "     (\n" +
                "       \n" +
                "        ?,\n" +
                "      \n" +
                "        ?,\n" +
                "      \n" +
                "      ? + 2\n" +
                "    ,\n" +
                "       \n" +
                "        ?,\n" +
                "      \n" +
                "        ?,\n" +
                "      \n" +
                "      ? + 2\n" +
                "    )\n" +
                "    or id = ?",sqlMeta.getSql());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
        Assert.assertEquals(7,sqlMeta.getJdbcParamValues().size());
    }
}
