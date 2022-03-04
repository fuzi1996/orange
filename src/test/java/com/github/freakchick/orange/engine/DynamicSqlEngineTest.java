package com.github.freakchick.orange.engine;

import com.github.freakchick.orange.SqlMeta;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author fuzi
 * date 3/4/2022
 */
public class DynamicSqlEngineTest {

    @Test
    public void testParse(){
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("select <if test='minId != null'>id > ${minId} #{minId} <if test='maxId != null'> and id &lt; ${maxId} #{maxId}</if> </if>");
        Map<String, Object> map = new HashMap<>();
        map.put("minId", 100);
        map.put("maxId", 500);
        SqlMeta parse = engine.parse(sql, map);
        Assert.assertTrue(null != parse);
        Assert.assertEquals("select  id > 100 ?   and id < 500 ? ",parse.getSql());
        Assert.assertEquals(2,parse.getJdbcParamValues().size());
    }

}
