package com.github.freakchick.orange.util;

import com.github.freakchick.orange.node.SqlNode;
import com.github.freakchick.orange.tag.TagHandler;
import com.github.freakchick.orange.tag.XmlParser;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/6/20
 **/
public class XmlUtil {
    public static void parseElementNode(Element node, List<SqlNode> nodes) {
        String tagName = node.getName();
        TagHandler handler = XmlParser.getTagHandler(tagName);
        //内部递归调用此方法
        handler.handle((Element) node, nodes);
    }
}
