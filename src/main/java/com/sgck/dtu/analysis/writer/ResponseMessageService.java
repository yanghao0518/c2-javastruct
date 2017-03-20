package com.sgck.dtu.analysis.writer;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.exception.DtuMessageException;

public interface ResponseMessageService {

	/**
	 * 
	 * @param protocolid
	 *            指定协议ID
	 * @param message
	 *            发送消息体（默认均为JSON格式）
	 */
	public byte[] resolve(String protocolid, JSONObject message) throws DtuMessageException, IOException;
}
