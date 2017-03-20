package com.sgck.dtu.analysis.common;

import com.alibaba.fastjson.JSONObject;

public class DefaultResponseResultImpl implements DefaultResponseResult
{

	private Object data;

	public DefaultResponseResultImpl(JSONObject message)
	{
		setRtnData(message);
	}

	public DefaultResponseResultImpl(JSONObject message, RtnCommandProperties commandProperties)
	{
		setRtnData(message,commandProperties);
	}

	@Override
	public int getCode()
	{
		return 200;
	}

	@Override
	public String getMessage()
	{
		return "处理正确";
	}

	@Override
	public Object getData()
	{
		return this.data;
	}

	@Override
	public boolean isReptSend()
	{
		return true;
	}

	@Override
	public void setRtnData(JSONObject message)
	{
		// 发送确认包
		setRtnData(message, RtnCommandProperties.SUCCESS);
	}

	public void setRtnData(JSONObject message, RtnCommandProperties commandProperties)
	{
		// 发送确认包
		JSONObject config = new JSONObject();
		if (!message.containsKey("Package_Number") || null == message.get("Package_Number")) {
			config.put("Package_Number", SystemConsts.DEFAULT_PACKAGE_NUM);
		} else {
			config.put("Package_Number", message.get("Package_Number"));
		}
		config.put("Gateway_Id", message.get("Gateway_Id"));
		config.put("command_properties", commandProperties.ID);
		JSONObject data = new JSONObject();
		data.put("id", RtnCodeType.Server_Gateway_ACK.ID);// 通讯确认（0x01）
		data.put("data", config);
		this.data = data;
		// ResponseResult result = new SuccessResponseResult(data);
	}

}
