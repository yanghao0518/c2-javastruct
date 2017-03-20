package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;

public class ResponseMessageServiceImpl implements ResponseMessageService
{
	private WriteMessageService writeMessageService = new WriteMessageServiceImpl();

	private CheckRuleService checkRuleService = new CheckRuleServiceImpl();
	
	private WriteOut writeOut = null;
	
	public ResponseMessageServiceImpl(){
		if(SystemConsts.order == ByteOrder.BIG_ENDIAN){
			writeOut = new WriteOutBigger();
		}else{
			writeOut = new WriteOutLitter();
		}
	}

	public CheckRuleService getCheckRuleService()
	{
		return checkRuleService;
	}

	public void setCheckRuleService(CheckRuleService checkRuleService)
	{
		this.checkRuleService = checkRuleService;
	}

	public WriteMessageService getWriteMessageService()
	{
		return writeMessageService;
	}

	public void setWriteMessageService(WriteMessageService writeMessageService)
	{
		this.writeMessageService = writeMessageService;
	}

	@Override
	public byte[] resolve(String protocolid, JSONObject content) throws DtuMessageException, IOException
	{
		// 获取返回消息模板
		Message contentTemplat = TemplateMessageManager.getInstance().getTCMessage(protocolid);
		CheckUtils.checkNull(contentTemplat, "the protocol not defined and protocolid is " + protocolid);
		TemplateMessage template = TemplateMessageManager.getInstance().getTCMessageTemplate();
		// 校验接口
		checkRuleService.writeBCC(content, template, contentTemplat);
		List<Byte> list = new ArrayList<Byte>();
		// 写入head 如果有属性为空 则需要判断模板里面的默认值是否存在
		write(list, template.getHead(), content);
		// 写入内容
		write(list, contentTemplat, content);
		// 写入foot
		write(list, template.getFoot(), content);

		int size = list.size();

		byte[] data = new byte[size];

		for (int i = 0; i < size; i++) {
			data[i] = list.get(i);
		}

		return data;
	}

	public void write(List<Byte> list, Message template, JSONObject content) throws DtuMessageException, IOException
	{
		Field[] fields = template.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!content.containsKey(fieldName)) {
				Object defaultValue = field.getDefaultValue();
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
				write(list, field, defaultValue);

			} else {
				Object defaultValue = content.get(fieldName);
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
				write(list, field, defaultValue);
			}
		}
	}

	private void write(List<Byte> list, Field field, Object defaultValue) throws IOException, DtuMessageException
	{
		switch (field.getType())
		{
		case BOOLEAN:
			writeOut.writeBoolean(list, Boolean.valueOf(defaultValue.toString()));
			break;
		case BYTE:
			writeOut.writeByte(list, Byte.parseByte(defaultValue.toString()));
			break;
		case CHAR:
			writeOut.writeByte(list, Short.parseShort(defaultValue.toString()));
			break;
		case SHORT:
			writeOut.writeShort(list, Short.parseShort(defaultValue.toString()));
			break;
		case USHORT:
			writeOut.writeShort(list, Integer.parseInt(defaultValue.toString()));
			break;
		case INT:
			writeOut.writeInt(list, Integer.parseInt(defaultValue.toString()));
			break;
		case UINT:
			writeOut.writeUInt(list, Long.parseLong(defaultValue.toString()));
			break;
		case FLOAT:
			writeOut.writeFloat(list, Float.parseFloat(defaultValue.toString()));
			break;
		case DOUBLE:
			writeOut.writeDouble(list, Double.parseDouble(defaultValue.toString()));
			break;
		case STRING:
			writeOut.writeString(list, defaultValue.toString());
			break;
		case USHORTARRAY:
			List<Integer> datas = (List<Integer>) defaultValue;
			for (Integer i : datas) {
				writeOut.writeInt(list, i);
			}
		default:
			throw new DtuMessageException(DtuMessageException.UNKNOWN_EXCEPTION, "not undefined jtype:" + field.getType().JTYPE);
		}
	}

}
