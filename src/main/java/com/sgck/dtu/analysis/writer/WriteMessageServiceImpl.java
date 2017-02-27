package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.BaseDataType;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.Message.Field;
import com.sgck.dtu.analysis.utiils.CheckUtils;
import com.sgck.dtu.analysis.utiils.CommonUtils;

public class WriteMessageServiceImpl implements WriteMessageService
{

	@Override
	public int write(Message template, JSONObject content, byte[] buffer, int startIndex)
	{
		byte[] temp = null;
		int currentWriteIndex = 0;
		Field[] fields = template.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!content.containsKey(fieldName)) {
				Object defaultValue = field.getDefaultValue();
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
				// 如果是string特殊处理
				if (field.getType() == BaseDataType.STRING) {
					String defaultStr = defaultValue.toString();
					System.arraycopy(defaultStr.getBytes(), 0, buffer, currentWriteIndex + startIndex, defaultStr.length());
					currentWriteIndex = currentWriteIndex + defaultStr.length();
				} else {
					temp = CommonUtils.toLH((Integer) defaultValue,field.getType().BYTES);
					System.arraycopy(temp, 0, buffer, currentWriteIndex + startIndex, field.getType().BYTES);
					currentWriteIndex = currentWriteIndex + field.getType().BYTES;
				}
			} else {
				Object defaultValue = content.get(fieldName);
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");

				if (field.getType() == BaseDataType.STRING) {
					String defaultStr = defaultValue.toString();
					System.arraycopy(defaultStr.getBytes(), 0, buffer, currentWriteIndex + startIndex, defaultStr.length());
					currentWriteIndex = currentWriteIndex + defaultStr.length();
				} else {
					temp = CommonUtils.toLH((Integer) defaultValue,field.getType().BYTES);
					System.arraycopy(temp, 0, buffer, currentWriteIndex + startIndex, field.getType().BYTES);
					currentWriteIndex = currentWriteIndex + field.getType().BYTES;
				}
			}
		}
		return currentWriteIndex + startIndex;
	}

}
