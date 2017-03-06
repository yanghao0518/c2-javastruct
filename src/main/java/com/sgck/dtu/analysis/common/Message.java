package com.sgck.dtu.analysis.common;

import java.util.ArrayList;
import java.util.List;

public class Message
{

	private String id;// 关健ID用于区别协议
	private String name;// 协议的NAME标识
	private String primaryKey;// 主键KEY，主要用于解析包头的协议类型
	private int protocolIndex;// 获取解析协议所在下标
	private Field[] fields;// 字段集合
	private int bufferLength;// buffer数组总长度
	private List<String> checkIds;

	public Message()
	{
		checkIds = new ArrayList<>();
		
	}

	public Message(int size)
	{
		checkIds = new ArrayList<>();
		fields = new Field[size];
	}

	public int getBufferLength()
	{
		return bufferLength;
	}

	public void setBufferLength(int bufferLength)
	{
		this.bufferLength = bufferLength;
	}

	
	
	
	public List<String> getCheckIds() {
		return checkIds;
	}

	public void setCheckIds(List<String> checkIds) {
		this.checkIds = checkIds;
	}
	
	public void addCheckId(String cid) {
		this.checkIds.add(cid);
	}

	public void addMessage(int index, Message node)
	{
		if (node.getPrimaryKey() != null) {
			setPrimaryKey(node.getPrimaryKey());
		}

		if (node.getProtocolIndex() != 0) {
			setProtocolIndex(node.getProtocolIndex());
		}

		Field[] nodeFields = node.getFields();

		if (null != nodeFields && nodeFields.length != 0) {

			Field[] newFields = new Field[getFields().length + nodeFields.length];

			if (index == 0) {
				System.arraycopy(nodeFields, 0, newFields, index, nodeFields.length);
			} else {
				System.arraycopy(getFields(), 0, newFields, 0, index - 1);
				System.arraycopy(nodeFields, 0, newFields, index, nodeFields.length);
			}

			this.setFields(newFields);

		}

	}

	public String getPrimaryKey()
	{
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	public int getProtocolIndex()
	{
		return protocolIndex;
	}

	public void setProtocolIndex(int protocolIndex)
	{
		this.protocolIndex = protocolIndex;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Field[] getFields()
	{
		return fields;
	}

	public void setFields(Field[] fields)
	{
		this.fields = fields;
	}

	public void addField(int index, String name, BaseDataType type)
	{
		this.fields[index] = new Field(name, type);
		this.bufferLength += type.BYTES;
	}

	public void addField(int index, String name, BaseDataType type, Object defaultValue)
	{
		this.getFields()[index] = new Field(name, type, defaultValue);
		if (type == BaseDataType.STRING) {
			this.bufferLength += defaultValue.toString().length();
		} else {
			this.bufferLength += type.BYTES;
			
		}
	}

	public void addField(int index, Field field)
	{
		this.getFields()[index] = field;
		if (field.getDefaultValue() == BaseDataType.STRING) {
			this.bufferLength += field.getVariableLength();
		} else {
			this.bufferLength += field.getType().BYTES;
			
		}

	}

}
