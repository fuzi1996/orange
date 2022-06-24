package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/20
 **/
public class ChooseSqlNode implements SqlNode {

    private final SqlNode defaultSqlNode;
    private final List<SqlNode> ifSqlNodes;

    public ChooseSqlNode(List<SqlNode> ifSqlNodes, SqlNode defaultSqlNode) {
        this.ifSqlNodes = ifSqlNodes;
        this.defaultSqlNode = defaultSqlNode;
    }

    @Override
    public boolean apply(Context context) {
        for (SqlNode sqlNode : this.ifSqlNodes) {
            if (sqlNode.apply(context)) {
                return true;
            }
        }
        if (Objects.nonNull(this.defaultSqlNode)) {
            this.defaultSqlNode.apply(context);
            return true;
        }
        return false;
    }

    @Override
    public boolean applyParameter(Set<String> set) {
        if (Objects.nonNull(this.ifSqlNodes)) {
            this.ifSqlNodes.forEach(ifSqlNode -> ifSqlNode.applyParameter(set));
        }
        if (Objects.nonNull(this.defaultSqlNode)) {
            this.defaultSqlNode.applyParameter(set);
        }
        return true;
    }
}
