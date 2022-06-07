package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;
import com.github.freakchick.orange.token.TokenHandler;
import com.github.freakchick.orange.token.TokenParser;
import com.github.freakchick.orange.util.OgnlUtil;
import com.github.freakchick.orange.util.RegexUtil;

import java.util.*;


public class ForeachSqlNode implements SqlNode {

    String collection;
    String open;
    String close;
    String separator;
    String item;
    String index;
    SqlNode contents;
    String indexDataName;

    public ForeachSqlNode(String collection, String open, String close, String separator, String item, String index, SqlNode contents) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.item = item;
        this.index = index;
        this.contents = contents;
        this.indexDataName = String.format("__index_%s", collection);
    }

    @Override
    public void apply(Context context) {
        // 标签类SqlNode先拼接空格，和前面的内容隔开
        Iterable<?> iterable = OgnlUtil.getIterable(collection, context.getData());
        // issue 3
        if(null == iterable || !iterable.iterator().hasNext()){
            return;
        }

        context.appendSql(" ");
        // 当前循环索引
        int currentIndex = 0;

        context.appendSql(open);

        boolean hasOriginItemData = context.getData().containsKey(this.item);
        Object originItemData = context.getData().getOrDefault(this.item,null);

        boolean hasOriginIndexData = context.getData().containsKey(this.index);
        Object originIndexData = context.getData().getOrDefault(this.index,null);

        for (Object o : iterable) {
            boolean isValueMap = o instanceof Map.Entry;
            if(0 == currentIndex){
                if(isValueMap){
                    context.getData().put(indexDataName, new LinkedHashMap<>());
                }else{
                    context.getData().put(indexDataName, new LinkedList<>());
                }
            }


            Context proxy = new Context(context.getData());
            String mapKey = null;
            if(isValueMap){
                Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) o;
                mapKey = mapEntry.getKey();
                applyIndex(proxy,mapEntry.getKey());
                applyItem(proxy,mapEntry.getValue());
                ((Map) context.getData().get(indexDataName)).put(mapKey,mapKey);
            }else{
                // 每次把当前循环的当前索引值追加到参数变量中
                ((List) context.getData().get(indexDataName)).add(currentIndex);
                // issues/I4DF07
                applyItem(proxy,o);
                applyIndex(proxy,currentIndex);
            }
            String childSqlText = getChildText(proxy, currentIndex, mapKey);
            // foreach 里面存在if等可能最终语句为空因此需要判断一下
            boolean hasChildSqlText = null != childSqlText && childSqlText.trim().length() > 0;

            //不是第一次，需要拼接分隔符
            if (currentIndex != 0 && hasChildSqlText) {
                context.appendSql(separator);
            }

            context.appendSql(childSqlText);
            currentIndex++;
        }
        if(hasOriginItemData){
            // 原参数中含有`${item}`作为key的数据
            context.bind(this.item,originItemData);
        }else{
            // 原参数中不含有`${item}`作为key的数据
            context.getData().remove(item);
        }
        if(hasOriginIndexData){
            context.bind(this.index,originIndexData);
        }else{
            context.getData().remove(this.index);
        }
        context.appendSql(close);
    }

    private void applyItem(Context context, Object o) {
        if (this.item != null) {
            context.bind(this.item, o);
        }
    }

    private void applyIndex(Context context,Object indexValue) {
        if (this.index != null) {
            context.bind(this.index, indexValue);
        }
    }

    @Override
    public void applyParameter(Set<String> set) {
        set.add(collection);
        Set<String> temp = new HashSet<>();
        contents.applyParameter(set);
        for (String key: temp){
            if (key.matches(item + "[.,:\\s\\[]")){
                continue;
            }
            if (key.matches(index + "[.,:\\s\\[]")){
                continue;
            }
            set.add(key);
        }
    }

    /**
     * 替换foreach子node里面的变量名称
     * 例如: `#{item.name} == #{idx}   and id = #{id}`
     * => `#{list[0].name} == #{__index_list[0]}   and id = #{id}`
     *
     * @param proxy
     * @param currentIndex
     * @return
     */
    public String getChildText(Context proxy, int currentIndex, String mapKey) {
        String newItem = String.format("%s[%d]", collection, currentIndex);  //ognl可以直接获取  aaa[0]  形式的值
        String newIndex = String.format("%s[%d]", indexDataName, currentIndex);
        if(null != mapKey){
            newItem = String.format("%s.%s", collection, mapKey);
            newIndex = String.format("%s.%s", indexDataName, mapKey);
        }
        this.contents.apply(proxy);
        String sql = proxy.getSql();
        String finalNewItem = newItem;
        String finalNewIndex = newIndex;
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                //item替换成自己的变量名: collection[0]  collection[1] collection[2] ......
                String replace = RegexUtil.replace(content, item, finalNewItem);
                if (replace.equals(content)) {
                    //替换索引,index替换成自己的变量名: __index_xxx[0]  __index_xxx[1] __index_xxx[2] ......
                    replace = RegexUtil.replace(content, index, finalNewIndex);
                }
                return String.format("#{%s}",replace);
            }
        });
        return tokenParser.parse(sql);
    }

}
