package com.sgck.dtu.analysis.writer;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.exception.DtuMessageException;

public class CheckRuleServiceImpl implements CheckRuleService
{

	@Override
	public void writeBCC(JSONObject content, TemplateMessage template, Message contentTemplat)
	{
		// bbc校验位
		int bcc = 0;
		if (content.containsKey("BCC")) {
			bcc = content.getIntValue("BCC");
		}
		for (Field field : template.getHead().getFields()) {
			String fieldName = field.getName();
			if (!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")) {
				checkRule(bcc,field,content);
			}
		}

		for (Field field : contentTemplat.getFields()) {
			String fieldName = field.getName();
			if (!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")) {
				checkRule(bcc,field,content);
			}
		}

		for (Field field : template.getFoot().getFields()) {
			String fieldName = field.getName();
			if (!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")) {
				checkRule(bcc,field,content);
			}
		}
		
		content.put("BCC", bcc);

	}

	private void checkRule(int bcc, Field field, JSONObject content)
	{
		switch (field.getType())
		{
		case BOOLEAN:
			bcc = bcc ^ (content.getBooleanValue(field.getName()) ? 1 : 0);
			break;
		case BYTE:
			bcc = bcc ^ content.getByteValue(field.getName());
			break;
		case CHAR:
			bcc = bcc ^ content.getByteValue(field.getName());
			break;
		case SHORT:
			bcc = dealShort(bcc, content.getShortValue(field.getName()));
			break;
		case USHORT:
			bcc = dealUShort(bcc, content.getIntValue(field.getName()));
			break;
		case INT:
			bcc = dealInt(bcc, content.getIntValue(field.getName()));
			break;
		case UINT:
			bcc = dealInt(bcc, content.getIntValue(field.getName()));
			break;
		case FLOAT:
			bcc = dealFloat(bcc, content.getFloatValue(field.getName()));
			break;
		case DOUBLE:
			bcc = dealDouble(bcc, content.getDoubleValue(field.getName()));
			break;
		case STRING:
			byte[] bytes = content.getString(field.getName()).getBytes();
			for(byte b:bytes){
				bcc = bcc ^ b;
			}
			break;
		case USHORTARRAY:
			List<Integer> datas = (List<Integer>) content.get(field.getName());
			for (Integer i : datas) {
				dealInt(bcc, i);
			}
		default:
			throw new DtuMessageException(DtuMessageException.UNKNOWN_EXCEPTION, "not undefined jtype:" + field.getType().JTYPE);
		}

	}
	
	
	private int dealDouble(int bcc, double v)
	{
		return dealLong(bcc,Double.doubleToLongBits(v));
	}
	
	
	private int dealFloat(int bcc, float v)
	{
		return dealInt(bcc,Float.floatToIntBits(v));
	}

	private int dealLong(int bcc, long v)
	{

		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		bcc = bcc ^ (byte) (v >> 16);
		bcc = bcc ^ (byte) (v >> 24);

		bcc = bcc ^ (byte) (v >> 32);
		bcc = bcc ^ (byte) (v >> 40);
		bcc = bcc ^ (byte) (v >> 48);
		bcc = bcc ^ (byte) (v >> 56);

		return bcc;
	}

	private int dealInt(int bcc, int v)
	{

		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		bcc = bcc ^ (byte) (v >> 16);
		bcc = bcc ^ (byte) (v >> 24);

		return bcc;
	}

	private int dealShort(int bcc, short v)
	{
		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		return bcc;
	}

	private int dealUShort(int bcc, int v)
	{
		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		return bcc;
	}

}
