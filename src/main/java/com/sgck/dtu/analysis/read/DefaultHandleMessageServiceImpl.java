package com.sgck.dtu.analysis.read;

import com.alibaba.fastjson.JSONObject;

/**
 * 默认接收实现类的代理类
 * 
 * @author DELL 如需使用则需要用户自定义实现类，注入到DefaultHandleFcMessageServiceImpl或者set进来
 *
 */
public class DefaultHandleMessageServiceImpl implements HandleMessageService
{

	private HandleMessageService target;

	public DefaultHandleMessageServiceImpl()
	{

	}

	public DefaultHandleMessageServiceImpl(HandleMessageService target)
	{
		this.target = target;
	}

	public void setHandleFcMessageService(HandleMessageService target)
	{
		this.target = target;
	}

	@Override
	public void handle(JSONObject message)
	{
		System.out.println("DefaultHandleFcMessageServiceImpl->" + message);
		if (null != this.target) {
			this.target.handle(message);
		}
	}

}
