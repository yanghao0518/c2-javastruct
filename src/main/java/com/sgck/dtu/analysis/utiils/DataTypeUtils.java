package com.sgck.dtu.analysis.utiils;

import java.util.HashMap;

import com.sgck.dtu.analysis.common.BaseDataType;

public class DataTypeUtils
{

	private static HashMap<String, BaseDataType> dataTypes = new HashMap<String, BaseDataType>();

	static {
		for (BaseDataType d : BaseDataType.values()) {
			dataTypes.put(d.CTYPE, d);
		}
	}

	public static final BaseDataType getDataType(String ctype)
	{
		BaseDataType d = dataTypes.get(ctype);
		return d;
	}
	
	public static Object getBaseDateValue(BaseDataType type, String value)
	{
		switch (type)
		{
		case BOOLEAN:
			return Boolean.valueOf(value);
		case BYTE:
			int radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Byte.parseByte(value, radix);
		case CHAR:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Integer.parseInt(value, radix);
		case SHORT:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Short.parseShort(value, radix);
		case USHORT:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Integer.parseInt(value, radix);
		case INT:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Integer.parseInt(value, radix);
		case UINT:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Long.parseLong(value, radix);
		case LONG:
			radix = getRadix(value);
			value = value.replace("0X", "").replace("0x", "");
			return Long.parseLong(value, radix);
		case FLOAT:
			return Float.parseFloat(value);
		case DOUBLE:
			return Double.parseDouble(value);
		default:
			return value;
		}
	}
	

	public static int getRadix(String value)
	{
		if (value.toUpperCase().startsWith("0X")) {
			return 16;
		}

		if (value.toUpperCase().startsWith("0")) {
			return 8;
		}
		return 10;

	}

}
