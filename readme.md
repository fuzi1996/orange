# 概述

- orange是一个动态sql引擎，类似mybatis的功能，解析带标签的动态sql，生成`?`占位符的sql和`?`对应的参数列表。
- 借鉴了mybatis源码，相当于mybatis中的动态sql解析功能的抽取，因此mybatis支持的标签与属性，orange也是同样支持的。
- 支持 `<if>` `<foreach>` `<where>` `<set>` `<trim>` `<choose>` `<when>` `<otherwise>` `<bind>`

# 使用教程

- 在自己的maven项目中引入maven坐标

```xml

<dependency>
  <groupId>io.github.freakchick</groupId>
  <artifactId>orange</artifactId>
  <version>1.0</version>
</dependency>
```

- 核心api

```java
DynamicSqlEngine engine=new DynamicSqlEngine();
        SqlMeta sqlMeta=engine.parse(sql,map);
```

- 示例

```java
@Test
public void testForeach(){
    DynamicSqlEngine engine=new DynamicSqlEngine();
    String sql=("select * from user where name in <foreach collection='list' open='(' separator=',' close=')'>#{item.name}</foreach>");
    Map<String, Object> map=new HashMap<>();


    ArrayList<User> arrayList=new ArrayList<>();
    arrayList.add(new User(10,"tom"));
    arrayList.add(new User(11,"jerry"));
    map.put("list",arrayList);

    SqlMeta sqlMeta=engine.parse(sql,map);
    System.out.println(sqlMeta.getSql());
    sqlMeta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```text
select * from user where name in  ( ? , ? ) 
tom
jerry
```

# 其他示例

## if

- 示例

```java
@Test
public void testForeach(){
    DynamicSqlEngine engine=new DynamicSqlEngine();
    String sql=("select * from user where name in <foreach collection='list' open='(' separator=',' close=')'>#{item.name}</foreach>");
    
    Map<String, Object> map=new HashMap<>();
    ArrayList<User> arrayList=new ArrayList<>();
    arrayList.add(new User(10,"tom"));
    arrayList.add(new User(11,"jerry"));
    map.put("list",arrayList);

    SqlMeta sqlMeta=engine.parse(sql,map);
    System.out.println(sqlMeta.getSql());
    sqlMeta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```text
select * from user where name in  ( ? , ? ) 
tom
jerry
```

## foreach

- List
  - 示例
    ```java
    @Test
    public void testForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("select * from user where name in <foreach collection='list' open='(' separator=',' close=')'>#{item.name}</foreach>");
        
        Map<String, Object> map = new HashMap<>();
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);
    
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
    ```

  - 示例执行结果：

    ```text
    select * from user where name in  ( ? , ? ) 
    tom
    jerry
    ```

- Map

  - 示例

    ```java
    @Test
    public void testForeachMap() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = (
                "select count(*) from key_cols where" +
                "  <foreach item=\"item\" index=\"key\" collection=\"map\"" +
                "     open=\"\" separator=\"AND\" close=\"\">"+
                "   ${key} = #{item} "+
                "  </foreach>"
        );
        
        Map<String,Object> mapParam = new HashMap<>();
        mapParam.getMap().put("col_a", 22);
        mapParam.getMap().put("col_b", 222);

        SqlMeta sqlMeta = engine.parse(sql, mapParam);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
    ```

  - 示例执行结果：

    ```text
    select count(*) from key_cols where
         col_a = ? AND col_b = ?
    22
    222
    ```

## set

- 示例

```java
@Test
public void testSet(){
    String sql="update Author\n"+
    "    <set>\n"+
    "      <if test=\"username != null\">username=#{username},</if>\n"+
    "      <if test=\"password != null\">password=#{password},</if>\n"+
    "      <if test=\"email != null\">email=#{email},</if>\n"+
    "      <if test=\"bio != null\">bio=#{bio}</if>\n"+
    "    </set>\n"+
    "  where id=#{id}";
    
    Map<String,Object> mapParam = new HashMap<>();
    mapParam.put("id",1);
    mapParam.put("username","Jensan");

    SqlMeta meta=this.engine.parse(sql,mapParam);
    System.out.println(meta.getSql());
    meta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```
update Author
     SET username=?
  where id=?
Jensan
1
```

## where

- 示例

```java
@Test
public void testWhere5(){
    String sql="SELECT * FROM BLOG\n"+
    "  <where>\n"+
    "    <if test=\"state != null\">\n"+
    "        and state = #{state}\n"+
    "    </if>\n"+
    "    <if test=\"title != null\">\n"+
    "        AND title like #{title}\n"+
    "    </if>\n"+
    "    <if test=\"name != null\">\n"+
    "        OR author_name like #{name}\n"+
    "    </if>\n"+
    "  </where>";
    
    Map<String,Object> mapParam = new HashMap<>();
    mapParam.put("state",1);
    mapParam.put("title","titleValue");
    mapParam.put("name","nameValue");

    SqlMeta meta=this.engine.parse(sql,mapParam);
    System.out.println(meta.getSql());
    meta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```
SELECT * FROM BLOG
   WHERE state = ?
    
     
        AND title like ?
    
     
        OR author_name like ?
1
titleValue
nameValue
```

## trim

- 示例

```java
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
    
    Map<String,Object> mapParam = new HashMap<>();
    mapParam.put("state", 1);

    SqlMeta meta = this.engine.parse(sql, mapParam);
    System.out.println(meta.getSql());
    meta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```
SELECT * FROM BLOG
   where state = ?
1
```

## choose

- 示例

```java
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
    
    Map<String,Object> mapParam = new HashMap<>();
    mapParam.put("title", "value 1");
    
    SqlMeta sqlMeta = this.engine.parse(sql, mapParam);
    System.out.println(sqlMeta.getSql());
    sqlMeta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```
SELECT * FROM BLOG WHERE state = ‘ACTIVE’
   
      AND title like ?
    
value 1
```

## bind

- 示例

```java
@Test
public void testBind() {
    String sql = "<bind name=\"pattern\" value=\"'%' + _parameter.title + '%'\" />\n" +
            "  SELECT * FROM BLOG\n" +
            "  WHERE title LIKE #{pattern}";
    Map<String,Object> mapParam = new HashMap<>();
    Map<String,Object> _parameter = new HashMap<>();
    _parameter.put("title", "titleValue");
    mapParam.put("_parameter", _parameter);

    SqlMeta meta = this.engine.parse(sql, mapParam);

    System.out.println(meta.getSql());
    meta.getJdbcParamValues().forEach(System.out::println);
}
```

- 示例执行结果：

```

  SELECT * FROM BLOG
  WHERE title LIKE ?
%titleValue%
```

# 联系作者：

## wechat：

- 提问前麻烦请先star支持一下

<div style="text-align: center"> 
<img src="https://freakchicken.gitee.io/images/kafkaui/wechat.jpg" width = "30%" />
</div>

## 捐赠：
如果您喜欢此项目，请给作者加鸡腿
<div style="text-align: center"> 
<img src="https://freakchicken.gitee.io/images/kafkaui/wechatpay.jpg" width = "30%" />
<img src="https://freakchicken.gitee.io/images/kafkaui/alipay.jpg" width = "29%" />
</div>
