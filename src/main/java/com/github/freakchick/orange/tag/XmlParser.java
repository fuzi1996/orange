package com.github.freakchick.orange.tag;

import com.github.freakchick.orange.node.MixedSqlNode;
import com.github.freakchick.orange.node.SqlNode;
import com.github.freakchick.orange.node.TextSqlNode;
import com.github.freakchick.orange.util.XmlUtil;
import org.dom4j.*;

import java.util.*;


public class XmlParser {

    private static Map<String, TagHandler> nodeHandlerMap = new HashMap<String, TagHandler>();

    static {
        nodeHandlerMap.put("trim", new TrimHandler());
        nodeHandlerMap.put("where", new WhereHandler());
        nodeHandlerMap.put("set", new SetHandler());
        nodeHandlerMap.put("foreach", new ForeachHandler());
        nodeHandlerMap.put("if", new IfHandler());
        nodeHandlerMap.put("choose", new ChooseHandler());
        nodeHandlerMap.put("when", new IfHandler());
        nodeHandlerMap.put("otherwise", new OtherwiseHandler());
//        nodeHandlerMap.put("bind", new BindHandler());
    }

    public static TagHandler getTagHandler(String tagName) {
        TagHandler tagHandler = nodeHandlerMap.get(tagName);
        if (Objects.isNull(tagHandler)) {
            throw new RuntimeException(String.format("tag [%s] not supported", tagName));
        }
        return tagHandler;
    }

    //将xml内容解析成sqlNode类型
    public static SqlNode parseXml2SqlNode(String text) {
        Document document;
        try {
            document = DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        Element rootElement = document.getRootElement();
        List<SqlNode> contents = parseElement(rootElement);
        SqlNode sqlNode = new MixedSqlNode(contents);
        return sqlNode;
    }

    //解析单个标签的子内容，转化成sqlNode list
    public static List<SqlNode> parseElement(Element element) {
        List<SqlNode> nodes = new ArrayList<>();

        List<Object> children = element.content();
        for (Object node : children) {
            if (node instanceof Text) {
                TextSqlNode textSqlNode = new TextSqlNode(((Text) node).getText());
                nodes.add(textSqlNode);
            } else if (node instanceof Element) {
                XmlUtil.parseElementNode((Element) node, nodes);
            }
        }
        return nodes;
    }
}
