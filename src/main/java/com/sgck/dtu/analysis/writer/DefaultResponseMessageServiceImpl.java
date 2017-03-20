package com.sgck.dtu.analysis.writer;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;

public class DefaultResponseMessageServiceImpl implements ResponseMessageService {
	private WriteMessageService writeMessageService = new WriteMessageServiceImpl();
	
	private CheckRuleService checkRuleService = new CheckRuleServiceImpl();

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
	public byte[] resolve(String protocolid, JSONObject content) throws DtuMessageException, IOException{
		// 获取返回消息模板
				Message contentTemplat = TemplateMessageManager.getInstance().getTCMessage(protocolid);
				CheckUtils.checkNull(contentTemplat, "the protocol not defined and protocolid is " + protocolid);
				TemplateMessage template = TemplateMessageManager.getInstance().getTCMessageTemplate();
				// 初始化一个buffer
				byte[] buffer = new byte[template.getHead().getBufferLength() + contentTemplat.getBufferLength() + template.getFoot().getBufferLength()];
				// 校验接口
				checkRuleService.writeBCC(content, template, contentTemplat);
				int startIndex = 0;
				// 写入head 如果有属性为空 则需要判断模板里面的默认值是否存在
				startIndex = writeMessageService.write(template.getHead(), content, buffer, startIndex);
				// 写入内容
				startIndex = writeMessageService.write(contentTemplat, content, buffer, startIndex);
				// 写入foot
				startIndex = writeMessageService.write(template.getFoot(), content, buffer, startIndex);
				// 返回buffer给socket
				return buffer;
	}

}
