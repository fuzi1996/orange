package com.github.freakchick.orange.tag;

import com.github.freakchick.orange.node.SqlNode;
import com.github.freakchick.orange.node.VarDeclSqlNode;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import java.util.List;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/15
 **/
public class BindHandler implements TagHandler {
    public BindHandler() {
        // Prevent Synthetic Access
    }

    @Override
    public void handle(Element element, List<SqlNode> contents) {
        if (element instanceof DefaultElement) {
            DefaultElement el = ((DefaultElement) element);
            final String name = el.attributeValue("name");
            final String expression = el.attributeValue("value");
            final VarDeclSqlNode node = new VarDeclSqlNode(name, expression);
            contents.add(node);
        }
    }
}
