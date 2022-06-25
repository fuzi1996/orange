package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;
import com.github.freakchick.orange.util.OgnlUtil;

import java.util.Set;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/24
 **/
public class VarDeclSqlNode implements SqlNode {

    private final String name;
    private final String expression;

    public VarDeclSqlNode(String var, String exp) {
        name = var;
        expression = exp;
    }

    @Override
    public boolean apply(Context context) {
        Object value = OgnlUtil.getValue(this.expression, context.getData());
        context.bind(this.name, value);
        return true;
    }

    @Override
    public boolean applyParameter(Set<String> set) {
        return true;
    }
}
