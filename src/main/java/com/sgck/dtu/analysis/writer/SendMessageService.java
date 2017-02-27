package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;

/**
 * 发送消息服务
 * 
 * @author DELL
 *
 */
public interface SendMessageService
{
	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public void send(String protocolid, JSONObject message);
}
