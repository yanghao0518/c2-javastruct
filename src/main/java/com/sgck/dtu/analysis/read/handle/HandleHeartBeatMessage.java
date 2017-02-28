package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.annotation.ResponseMessageProtocol;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理心跳包
 * @author DELL
 *
 */
@HandleMessage(Type=HandleType.RECEIVE)
public class HandleHeartBeatMessage implements HandleMessageService {

	@Override
	@HandleMessageProtocol(id="5",response=true)
	public ResponseResult handle(JSONObject message) {
		System.out.println("ACK->" + message);
		return null;
	}
	
}
