package com.github.freakchick.orange.tag;

import com.github.freakchick.orange.node.ChooseSqlNode;
import com.github.freakchick.orange.node.MixedSqlNode;
import com.github.freakchick.orange.node.SqlNode;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/15
 **/
public class ChooseHandler implements TagHandler {
    public ChooseHandler() {
        // Prevent Synthetic Access
    }

    @Override
    public void handle(Element element, List<SqlNode> contents) {
        List<SqlNode> whenSqlNodes = new ArrayList<>();
        List<SqlNode> otherwiseSqlNodes = new ArrayList<>();
        handleWhenOtherwiseNodes(element, whenSqlNodes, otherwiseSqlNodes);
        SqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
        ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNode);
        contents.add(chooseSqlNode);
    }

    private void handleWhenOtherwiseNodes(Element element, List<SqlNode> ifSqlNodes, List<SqlNode> defaultSqlNodes) {
        List content = element.content();
        for (Object item : content) {
            if (item instanceof DefaultElement) {
                DefaultElement defaultElement = (DefaultElement) item;
                String defaultElementName = defaultElement.getName();
                TagHandler tagHandler = XmlParser.getTagHandler(defaultElementName);
                if (tagHandler instanceof IfHandler) {
                    tagHandler.handle((Element) item, ifSqlNodes);
                } else if (tagHandler instanceof OtherwiseHandler) {
                    tagHandler.handle((Element) item, defaultSqlNodes);
                }
            }
        }
    }

    private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
        SqlNode defaultSqlNode = null;
        if (defaultSqlNodes.size() == 1) {
            defaultSqlNode = defaultSqlNodes.get(0);
        } else if (defaultSqlNodes.size() > 1) {
            throw new IllegalArgumentException("Too many default (otherwise) elements in choose statement.");
        }
        return defaultSqlNode;
    }
}
