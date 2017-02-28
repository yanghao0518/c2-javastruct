package com.sgck.dtu.analysis.common;

public class Field
{
	private String name;// 字段名称
	private BaseDataType type;// 数据类型 需要包括该数据项具体占有多少个字节数
	private Object defaultValue;
	private String realValueRule;//计算实际值规则
	private int variableLength;//可变长度 
	
	
	public int getVariableLength()
	{
		return variableLength;
	}

	public void setVariableLength(int variableLength)
	{
		this.variableLength = variableLength;
	}

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
	

	public String getRealValueRule()
	{
		return realValueRule;
	}

	public void setRealValueRule(String realValueRule)
	{
		this.realValueRule = realValueRule;
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
