package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.annotation.ResponseMessageProtocol;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

@HandleMessage(Type=HandleType.RECEIVE)
public class HandleConfirmMessage implements HandleMessageService {

	@Override
	@HandleMessageProtocol(id="1")
	@ResponseMessageProtocol(id="1")
	public ResponseResult handle(JSONObject message) {
		System.out.println("ACK->" + message);
		return null;
	}
}
