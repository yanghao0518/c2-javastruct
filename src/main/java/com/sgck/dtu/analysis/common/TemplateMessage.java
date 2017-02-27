package com.sgck.dtu.analysis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sgck.dtu.analysis.common.Message;

public class TemplateMessage
{

	private Map<String, Message> templates = new HashMap<String, Message>();
	
	//协议实现类包基本路径
	private List<String> basePackages = new ArrayList<String>();

	// 包头
	private Message head = null;

	// 包尾
	private Message foot = null;
	
	//默认字符串接收格式
	private String encoding = null;
	
	public TemplateMessage()
	{

	}
	
	public Message getMessage(String key){
		return templates.get(key);
	}
	
	public void addBasePackage(String path){
		if(!this.basePackages.contains(path)){
			this.basePackages.add(path);
		}
	}
	
	public List<String> getBasePackages(){
		return this.basePackages;
	}
	
	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public void addMessage(Message message)
	{
		templates.put(message.getId(), message);
	}

	public Map<String, Message> getTemplates()
	{
		return templates;
	}

	public void setTemplates(Map<String, Message> templates)
	{
		this.templates = templates;
	}

	public Message getHead()
	{
		return head;
	}

	public void setHead(Message head)
	{
		this.head = head;
	}

	public Message getFoot()
	{
		return foot;
	}

	public void setFoot(Message foot)
	{
		this.foot = foot;
	}

}
