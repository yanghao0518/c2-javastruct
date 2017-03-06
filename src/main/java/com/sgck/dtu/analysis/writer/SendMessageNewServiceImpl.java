package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.LEDataOutputStream;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;

public class SendMessageNewServiceImpl implements SendMessageNewService
{
	private CheckRuleService checkRuleService = new CheckRuleServiceImpl();

	public CheckRuleService getCheckRuleService()
	{
		return checkRuleService;
	}

	@Override
	public List<Byte> send(LEDataOutputStream dos, String protocolid, JSONObject content) throws DtuMessageException, IOException
	{

		// 获取返回消息模板
		Message contentTemplat = TemplateMessageManager.getInstance().getTCMessage(protocolid);
		CheckUtils.checkNull(contentTemplat, "the protocol not defined and protocolid is " + protocolid);
		TemplateMessage template = TemplateMessageManager.getInstance().getTCMessageTemplate();
		// 校验接口
		checkRuleService.writeBCC(content, template, contentTemplat);
		// 写入head 如果有属性为空 则需要判断模板里面的默认值是否存在
		write(dos, template.getHead(), content);
		// 写入内容
		write(dos, contentTemplat, content);
		// 写入foot
		write(dos, template.getFoot(), content);
		
		//dos.flush();
		
		System.out.println("发出的数据:"+dos.getOrgList());
		
		return dos.getOrgList();

	}

	public void write(LEDataOutputStream dos, Message template, JSONObject content) throws DtuMessageException, IOException
	{
		Field[] fields = template.getFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (!content.containsKey(fieldName)) {
				Object defaultValue = field.getDefaultValue();
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
				write(dos, field, defaultValue);

			} else {
				Object defaultValue = content.get(fieldName);
				CheckUtils.checkNull(defaultValue, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
				write(dos, field, defaultValue);
			}
		}
	}

	private void write(LEDataOutputStream dos, Field field, Object defaultValue) throws IOException, DtuMessageException
	{
		switch (field.getType())
		{
		case BOOLEAN:
			dos.writeBoolean(Boolean.valueOf(defaultValue.toString()));
			break;
		case BYTE:
			dos.writeByte(Byte.parseByte(defaultValue.toString()));
			break;
		case CHAR:
			dos.writeByte(Byte.parseByte(defaultValue.toString()));
			break;
		case SHORT:
			dos.writeShort(Short.parseShort(defaultValue.toString()));
			break;
		case USHORT:
			dos.writeShort(Integer.parseInt(defaultValue.toString()));
			break;
		case INT:
			dos.writeInt(Integer.parseInt(defaultValue.toString()));
			break;
		case UINT:
			dos.writeInt(Integer.parseInt(defaultValue.toString()));
			break;
		case FLOAT:
			dos.writeFloat(Float.parseFloat(defaultValue.toString()));
			break;
		case DOUBLE:
			dos.writeDouble(Double.parseDouble(defaultValue.toString()));
			break;
		case STRING:
			dos.writeBytes(defaultValue.toString());
			break;
		case USHORTARRAY:
			List<Integer> datas = (List<Integer>) defaultValue;
			for (Integer i : datas) {
				dos.writeInt(i);
			}
		default:
			throw new DtuMessageException(DtuMessageException.UNKNOWN_EXCEPTION, "not undefined jtype:" + field.getType().JTYPE);
		}
	}

}
