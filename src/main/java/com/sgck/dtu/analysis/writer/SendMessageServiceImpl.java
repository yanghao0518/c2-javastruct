package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.manager.ClientSocketManager;

public class SendMessageServiceImpl implements SendMessageService
{

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();


	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public void send(String protocolid, JSONObject content)
	{
		// 返回buffer给socket
		ClientSocketManager.getInstance().sendMessage(responseMessageService.resolve(protocolid, content));

	}

}
