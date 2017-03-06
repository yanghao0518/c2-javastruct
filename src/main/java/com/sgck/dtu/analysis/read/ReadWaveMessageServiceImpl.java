package com.sgck.dtu.analysis.read;

import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;

public class ReadWaveMessageServiceImpl implements ReadWaveMessageService
{

	@Override
	public Message getMessageContent(Integer packageType)
	{
		Message content = TemplateMessageManager.getInstance().getFCMessage(packageType + "-4");
		return content;
	}

}
