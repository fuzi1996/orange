package com.github.freakchick.orange.engine;

import com.github.freakchick.orange.SqlMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import com.github.freakchick.orange.util.OgnlUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author fuzi
 * date 3/4/2022
 */
public class DynamicSqlEngineTest {

    @Test
    void testParse(){
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

    @Test
    void concurrentAccess() throws Exception {
        int run = 1000;
        Map<String, Object> context = new HashMap<>();
        List<Future<Object>> futures = new ArrayList<>();
        context.put("data", new HashMap<String,Object>(){{
            put("id",0);
        }});
        ExecutorService executor = Executors.newCachedThreadPool();
        IntStream.range(0, run).forEach(i -> {
            futures.add(executor.submit(() -> OgnlUtil.getValue("data.id", context)));
        });
        for (int i = 0; i < run; i++) {
            assertNotNull(futures.get(i).get());
        }
        executor.shutdown();
    }

}
