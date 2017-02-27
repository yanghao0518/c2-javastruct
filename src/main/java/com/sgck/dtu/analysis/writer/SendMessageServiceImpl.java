package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.manager.ClientSocketManager;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;

public class SendMessageServiceImpl implements SendMessageService
{

	private WriteMessageService writeMessageService = new WriteMessageServiceImpl();

	public WriteMessageService getWriteMessageService()
	{
		return writeMessageService;
	}

	public void setWriteMessageService(WriteMessageService writeMessageService)
	{
		this.writeMessageService = writeMessageService;
	}

	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public void send(String protocolid, JSONObject content)
	{
		// 获取返回消息模板
		Message contentTemplat = TemplateMessageManager.getInstance().getTCMessage(protocolid);
		CheckUtils.checkNull(contentTemplat, "the protocol not defined and protocolid is " + protocolid);
		TemplateMessage template = TemplateMessageManager.getInstance().getTCMessageTemplate();
		// 初始化一个buffer
		byte[] buffer = new byte[template.getHead().getBufferLength() + contentTemplat.getBufferLength() + template.getFoot().getBufferLength()];
		int startIndex = 0;
		// 写入head 如果有属性为空 则需要判断模板里面的默认值是否存在
		startIndex = writeMessageService.write(template.getHead(), content, buffer, startIndex);
		// 写入内容
		startIndex = writeMessageService.write(contentTemplat, content, buffer, startIndex);
		// 写入foot
		startIndex = writeMessageService.write(template.getFoot(), content, buffer, startIndex);
		// 返回buffer给socket
		ClientSocketManager.getInstance().sendMessage(buffer);

	}

}
