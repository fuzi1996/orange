package com.github.freakchick.orange.tag;

import com.github.freakchick.orange.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/20
 **/
public class OtherwiseHandler implements TagHandler {
    @Override
    public void handle(Element element, List<SqlNode> contents) {
        List<SqlNode> targetContents = XmlParser.parseElement(element);
        contents.addAll(targetContents);
    }
}
