package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.read.HandleMessageService;

@HandleMessage(Type=HandleType.RECEIVE)
public class HandleConfirmMessage implements HandleMessageService {

	@Override
	@HandleMessageProtocol(id="1")
	public void handle(JSONObject message) {
		System.out.println("ACK->" + message);
	}
}
