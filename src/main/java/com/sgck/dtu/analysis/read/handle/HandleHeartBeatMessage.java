package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.cache.AckLastCache;
import com.sgck.dtu.analysis.cache.ConfigManager;
import com.sgck.dtu.analysis.cache.LastOptVo;
import com.sgck.dtu.analysis.cache.SensorVo;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.common.SuccessResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理心跳包
 * 
 * @author DELL
 *
 */
@HandleMessage(Type = HandleType.RECEIVE)
public class HandleHeartBeatMessage implements HandleMessageService
{

	@Override
	@HandleMessageProtocol(id = "5", response = true)
	public ResponseResult handle(JSONObject message)
	{
		// 首先获取当前要下发的信息
		String Gateway_Id = Integer.toHexString((Integer) message.get("Gateway_Id"));
		// JSONObject currentDownSensor =
		// ConfigTableCache.getInstance().getCurrentDownSensor(Gateway_Id);
		SensorVo currentDownSensor = ConfigManager.getInstance().getCurrentDownSensor(Gateway_Id);
		if (null == currentDownSensor) {
			JSONObject ACK = new JSONObject();
			ACK.put("Package_Number", message.get("Package_Number"));
			ACK.put("command_properties", 1);// 确认协议ID
			ACK.put("Gateway_Id", message.get("Gateway_Id"));
			JSONObject data = new JSONObject();
			data.put("id", "1");
			data.put("data", ACK);
			ResponseResult result = new SuccessResponseResult(data);
			return result;
		}

		JSONObject config = currentDownSensor.getConfig();

		config.put("Package_Number", message.get("Package_Number"));
		config.put("Gateway_Id", message.get("Gateway_Id"));
		// 需要强转16进制
		config.put("Sensor_Id", Integer.parseInt(currentDownSensor.getSensor_Id(), 16));

		JSONObject data = new JSONObject();
		data.put("id", "6");// 传感器设置下发给网关
		data.put("data", currentDownSensor);
		ResponseResult result = new SuccessResponseResult(data);
		LastOptVo vo = new LastOptVo((Integer) message.get("Gateway_Id"), message.getIntValue("Package_Number"), "6", config);
		AckLastCache.getInstance().setLastOpt(Gateway_Id, vo);
		return result;
	}

}
