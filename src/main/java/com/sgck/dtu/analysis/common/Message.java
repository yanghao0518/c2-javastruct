package com.sgck.dtu.analysis.common;

public class Message
{

	private String id;// 关健ID用于区别协议
	private String name;// 协议的NAME标识
	private String primaryKey;// 主键KEY，主要用于解析包头的协议类型
	private int protocolIndex;// 获取解析协议所在下标
	private Field[] fields;// 字段集合
	private int bufferLength;//buffer数组总长度

	public Message()
	{

	}

	public Message(int size)
	{
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
		this.bufferLength += type.BYTES;
	}


	// 协议具体的某个字段信息
	public class Field
	{
		private String name;// 字段名称
		private BaseDataType type;// 数据类型 需要包括该数据项具体占有多少个字节数
		private Object defaultValue;
		

		public Object getDefaultValue()
		{
			return defaultValue;
		}

		public void setDefaultValue(Object defaultValue)
		{
			this.defaultValue = defaultValue;
		}

		public Field(String name, BaseDataType type)
		{
			this.name = name;
			this.type = type;
		}

		public Field()
		{

		}

		public Field(String name, BaseDataType type, Object defaultValue)
		{
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public BaseDataType getType()
		{
			return type;
		}

		public void setType(BaseDataType type)
		{
			this.type = type;
		}

	}

}