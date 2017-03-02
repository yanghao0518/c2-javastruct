package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.cache.DataCache;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理上传上来的传感器组网配置
 * 
 * @author DELL
 *
 */
@HandleMessage(Type = HandleType.RECEIVE)
public class HandleSensorMessage implements HandleMessageService
{

	@Override
	@HandleMessageProtocol(id = "3", response = false)
	public ResponseResult handle(JSONObject message)
	{
		System.out.println("接收传感器组网信息上传信息:" + message);
		// 刷新公共组网配置
		String Sensor_Id = Integer.toHexString(message.getIntValue("Sensor_Id"));
		String Gateway_Id = Integer.toHexString(message.getIntValue("Gateway_Id"));
		int riss = message.getIntValue("Recver_Riss");
		DataCache.getInstance().refreshUploadZwInfo(Sensor_Id, Gateway_Id, riss, message);
		return null;
	}

}
