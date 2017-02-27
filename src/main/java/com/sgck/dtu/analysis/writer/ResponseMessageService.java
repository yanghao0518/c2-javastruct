package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;

public interface ResponseMessageService {

	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public byte[] resolve(String protocolid, JSONObject message);
}
