package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;
import com.github.freakchick.orange.token.TokenHandler;
import com.github.freakchick.orange.token.TokenParser;

import java.util.Set;


public class TextSqlNode implements SqlNode {

    private String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(Context context) {
        //解析常量值 ${xxx}
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                Object value = context.getOgnlValue(paramName);
                return value == null ? "" : value.toString();
            }
        });
        String s = tokenParser.parse(text);
        context.appendSql(s);
        return true;
    }

    @Override
    public boolean applyParameter(Set<String> set) {
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        String s = tokenParser.parse(text);

        TokenParser tokenParser2 = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        tokenParser2.parse(s);
        return true;
    }
}
