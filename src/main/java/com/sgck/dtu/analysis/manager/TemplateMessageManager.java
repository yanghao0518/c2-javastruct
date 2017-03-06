package com.sgck.dtu.analysis.manager;

import java.util.Map;

import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.TemplateMessage;
import com.sgck.dtu.analysis.utiils.XmlUtils;

/**
 * 模板管理类
 * 
 * @author DELL 为了
 */
public class TemplateMessageManager
{

	private TemplateMessage FCMessageTemplate = null;

	private TemplateMessage TCMessageTemplate = null;

	private TemplateMessageManager()
	{
		// 进行解析xml
		try {
			FCMessageTemplate = XmlUtils.parserFCXml();
			TCMessageTemplate = XmlUtils.parserTCXml();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(0);
		}

	}

	public Message getFCMessage(String id)
	{
		return FCMessageTemplate.getMessage(id);
	}

	public Message queryFCMessageBySuffix(String suffix)
	{
		Map<String, Message> ts = FCMessageTemplate.getTemplates();
		for (String key : ts.keySet()) {
			if (key.startsWith(suffix)) {
				return ts.get(key);
			}
		}
		return null;
	}

	public Message getTCMessage(String id)
	{
		return TCMessageTemplate.getMessage(id);
	}

	public TemplateMessage getFCMessageTemplate()
	{
		return FCMessageTemplate;
	}

	public TemplateMessage getTCMessageTemplate()
	{
		return TCMessageTemplate;
	}

	public static TemplateMessageManager getInstance()
	{
		return MessageTemplateManagerHolder.instance;
	}

	private static class MessageTemplateManagerHolder
	{
		private static TemplateMessageManager instance = new TemplateMessageManager();
	}
}
