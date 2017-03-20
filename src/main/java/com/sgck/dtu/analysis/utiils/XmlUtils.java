package com.sgck.dtu.analysis.utiils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sgck.dtu.analysis.common.BaseDataType;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.read.CheckBCC;
import com.sgck.dtu.analysis.read.CheckField;

/**
 * xml解析类 采用dom4j
 * 
 * @author DELL
 *
 */
public class XmlUtils
{

	public static TemplateMessage parserFCXml() throws DocumentException, IllegalArgumentException, IOException
	{
		String xml = "/FC.xml";
		if(SystemConsts.isTest){
			xml = "/FC_T.xml";
		}
		InputStream is = XmlUtils.class.getResourceAsStream(xml);
		return parserXml(is);
	}

	public static TemplateMessage parserTCXml() throws DocumentException, IllegalArgumentException, IOException
	{
		String xml = "/TC.xml";
		if(SystemConsts.isTest){
			xml = "/TC_T.xml";
		}
		InputStream is = XmlUtils.class.getResourceAsStream(xml);
		return parserXml(is);
	}

	public static TemplateMessage parserXml(InputStream is) throws DocumentException, IllegalArgumentException, IOException
	{
		SAXReader saxReader = new SAXReader();
		TemplateMessage template = null;
		try {
			Document document = saxReader.read(is);
			// 获取ROOT节点
			Element root = document.getRootElement();
			template = new TemplateMessage();
			// 设置字符串coding
			String encoding = root.attributeValue("encoding");
			if (null != encoding) {
				template.setEncoding(encoding);
			}
			Element el = null;
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				// 获取具体的每个协议头节点
				el = (Element) i.next();
				parserNode(template, el);
			}
			return template;
		} catch (DocumentException e) {
			throw e;
		} finally {
			if (null != is) {
				is.close();
			}
		}
	}

	private static void parserNode(TemplateMessage template, Element node)
	{
		String name = node.getName();
		if (name.equals("head")) {
			parserHead(template, node);
		} else if (name.equals("foot")) {
			parserFoot(template, node);
		} else if (name.equals("base_package")) {
			// package模块
			String src = node.attributeValue("src");
			CheckUtils.checkNull(src, "the base_package src is not allow null or empty from FC.xml or TC.xml");
			template.addBasePackage(src);
		} else if (name.equals("check")) {
			String id = node.attributeValue("id");
			CheckUtils.checkNull(id, "the check id is not allow null or empty from FC.xml or TC.xml");
			String type = node.attributeValue("type");
			CheckUtils.checkNull(type, "the check type is not allow null or empty from FC.xml or TC.xml");
			// 检查模块
			if("field".equals(type) && node.elements() != null && node.elements().size() > 0){
				
				Element children = (Element) node.elements().get(0);
				
				if(children.getName().equals("BCC")){
					CheckBCC checkField = new CheckBCC();
					String fieldName = children.attributeValue("targetField");
					CheckUtils.checkNull(fieldName, "the check next node is not allow null or empty from FC.xml or TC.xml");
					checkField.setFieldName(fieldName);
					String method = children.attributeValue("method");
					if(method != null){
						checkField.setMethod(method);
					}
					template.addCheckFields(id, checkField);
				}
			}
		} else {
			String pname = node.getParent().getName();
			String cname = node.getName();
			if (!pname.contains(cname)) {
				// 如果父节点不包含子节点名称，则表示无效设置
				throw new IllegalArgumentException(cname + "该节点无效,不隶属于父节点" + pname);
			}
			parserContent(template, node);
		}

	}

	// 首先创建包头
	private static void parserHead(TemplateMessage template, Element head)
	{
		String name = head.getName();
		// 包头结构
		CheckUtils.checkEmpty(head.elements(), "head not allow not contain any elements");
		Message headMessage = new Message(head.elements().size());
		headMessage.setId(name);
		headMessage.setName(name);
		String primaryKey = head.attributeValue("primaryKey");
		CheckUtils.checkNull(primaryKey, "protocol:" + name + "'s primaryKey attribute not allow null or empty!from FC.xml or TC.xml");
		headMessage.setPrimaryKey(primaryKey);
		int index = 0;
		for (Iterator j = head.elementIterator(); j.hasNext();) {
			Element node = (Element) j.next();
			parserField(headMessage, node, index++);
		}
		template.setHead(headMessage);
	}

	// 解析具体包
	private static void parserContent(TemplateMessage template, Element content)
	{
		String id = content.attributeValue("id");
		CheckUtils.checkNull(id, "protocol:" + content.getName() + "'s id attribute not allow null or empty!from FC.xml or TC.xml");
		//checkEmpty(content.elements(), content.getName() + "/"+id+" content not allow not contain any elements");
		Message message = null;
		if (null == content.elements() || content.elements().isEmpty()) {
			message = new Message(0);
		}else{
			message = new Message(content.elements().size());
		}
		
		String check = content.attributeValue("check");
		if( null != check){
			message.addCheckId(check);
		}
		message.setId(id);
		String secondPrimaryKey = content.attributeValue("secondPrimaryKey");
		if(null != secondPrimaryKey){
			message.setPrimaryKey(secondPrimaryKey);
		}
		message.setName(content.attributeValue("name"));
		int index = 0;
		for (Iterator j = content.elementIterator(); j.hasNext();) {
			Element node = (Element) j.next();
			parserField(message, node, index++);
		}
		template.addMessage(message);
	}

	// 创建包尾
	private static void parserFoot(TemplateMessage template, Element foot)
	{

		// 包头结构
		Message message = null;
		if (null == foot.elements() || foot.elements().isEmpty()) {
			message = new Message(0);
		}else{
			message = new Message(foot.elements().size());
		}
		String name = foot.getName();
		message.setId(name);
		message.setName(name);
		int index = 0;
		for (Iterator j = foot.elementIterator(); j.hasNext();) {
			Element node = (Element) j.next();
			parserField(message, node, index++);
		}
		template.setFoot(message);
	}

	// 解析具体列
	private static void parserField(Message message, Element nodej, int index)
	{
		if (null != message.getPrimaryKey() && message.getPrimaryKey().equals(nodej.getName())) {
			// 设置当前协议的Index
			message.setProtocolIndex(index);
		}
		BaseDataType dataType = null;
		String parserType = nodej.attributeValue("parserType");
		if (null != parserType) {
			dataType = DataTypeUtils.getDataType(parserType);
		} else {
			CheckUtils.checkNull(nodej.attributeValue("type"), "protocol:" + message.getName() + "/" + nodej.getName() + " 's type attribute not allow null or empty!from FC.xml or TC.xml");
			dataType = DataTypeUtils.getDataType(nodej.attributeValue("type"));
		}
		CheckUtils.checkNull(dataType, "not found defined data type  or parserType please check enum BaseDataType");
		
		String secondPrimaryKey = nodej.attributeValue("secondPrimaryKey");
		if(null != secondPrimaryKey){
			//二次主键
			message.setPrimaryKey(secondPrimaryKey);
		}
		String nodejName = nodej.attributeValue("name");
		if (null == nodejName || "".equals(nodejName.trim())) {
			nodejName = nodej.getName();
		}
		Field field = new Field(nodejName,dataType);
		// 处理默认值
		String defaultValue = nodej.attributeValue("default");
		// 如果有默认值
		if (null != defaultValue && !"".equals(defaultValue.trim())) {
			Object def = DataTypeUtils.getBaseDateValue(dataType, defaultValue);
			if (def != null) {
				field.setDefaultValue(def);
			}
			
		}
		
		String realValueRule = nodej.attributeValue("realValueRule");
		if(null != realValueRule){
			field.setRealValueRule(realValueRule);
		}
		
		String variableLength = nodej.attributeValue("variableLength");
		if(null != variableLength){
			field.setVariableLength(Integer.parseInt(variableLength));
		}
		
		message.addField(index ++, field);
	}


}
