package org.solmix.commons.xml;

import org.junit.Assert;
import org.junit.Test;

public class XmlParserTest {

	@Test
	public void test() {
		StringBuilder sb  = new StringBuilder();
//		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
		sb.append("<OBJ_DATASET a=\"1\">");
		sb.append("<C N=\"a\"></C>");
		sb.append("</OBJ_DATASET>");
		XMLParser parser  = new XMLParser(sb.toString());
		XMLNode node =parser.evalNode("/OBJ_DATASET");
		String code = node.getStringAttribute("a");
		Assert.assertEquals("1", code);
	}

}
