package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;

import java.util.List;
import java.util.Set;


public class MixedSqlNode implements SqlNode {

    List<SqlNode> contents ;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(Context context) {
        for (SqlNode node : contents) {
            node.apply(context);
        }
        return true;
    }

    @Override
    public boolean applyParameter(Set<String> set) {
        for (SqlNode node : contents) {
            node.applyParameter(set);
        }
        return true;
    }
}
