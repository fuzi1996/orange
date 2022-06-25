package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;

import java.util.Set;


public class IfSqlNode implements SqlNode {

    private String test;

    private SqlNode contents;

    public IfSqlNode(String test, SqlNode contents) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public boolean apply(Context context) {
        Boolean value = context.getOgnlBooleanValue(test);
        if (value) {
            //标签类SqlNode先拼接空格，和前面的内容隔开
            context.appendSql(" ");
            contents.apply(context);
            return true;
        }
        return false;
    }

    @Override
    public boolean applyParameter(Set<String> set) {
        contents.applyParameter(set);
        return true;
    }
}
