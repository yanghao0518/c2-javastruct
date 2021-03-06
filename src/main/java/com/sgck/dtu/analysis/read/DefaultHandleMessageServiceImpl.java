package com.sgck.dtu.analysis.read;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.ResponseResult;

/**
 * 默认接收实现类的代理类
 * 
 * @author DELL 如需使用则需要用户自定义实现类，注入到DefaultHandleFcMessageServiceImpl或者set进来
 *
 */
public class DefaultHandleMessageServiceImpl implements HandleMessageService
{

	private HandleMessageService target;
	
	Logger LOG = Logger.getLogger(this.getClass());

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
	public ResponseResult handle(JSONObject message)
	{
		LOG.info("DefaultHandleFcMessageServiceImpl->" + message);
		if (null != this.target) {
			return this.target.handle(message);
		}
		return null;
	}

}
