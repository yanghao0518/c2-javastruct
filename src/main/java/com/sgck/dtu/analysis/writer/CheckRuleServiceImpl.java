package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;

public class CheckRuleServiceImpl implements CheckRuleService
{

	@Override
	public void writeBCC(JSONObject content, TemplateMessage template, Message contentTemplat)
	{
		//bbc校验位
		int bcc = 0;
		if(content.containsKey("BCC")){
			bcc = content.getIntValue("BCC");
		}
		for (Field field : template.getHead().getFields()) {
			String fieldName = field.getName();
			if(!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")){
				int value = content.getIntValue(fieldName);
				bcc = bcc ^ value;
			}
		}
		
		for (Field field : contentTemplat.getFields()) {
			String fieldName = field.getName();
			if(!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")){
				int value = content.getIntValue(fieldName);
				bcc = bcc ^ value;
			}
		}
		
		for (Field field : template.getFoot().getFields()) {
			String fieldName = field.getName();
			if(!fieldName.equals("BCC") && !fieldName.equals("Constant_Up_Stop")){
				int value = content.getIntValue(fieldName);
				bcc = bcc ^ value;
			}
		}
		content.put("BCC", bcc);
		
	}

}
