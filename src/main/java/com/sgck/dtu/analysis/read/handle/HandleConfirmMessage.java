package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.cache.AckLastCache;
import com.sgck.dtu.analysis.cache.ConfigManager;
import com.sgck.dtu.analysis.cache.LastOptVo;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.common.SuccessResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理消息确认
 * @author DELL
 *
 */
@HandleMessage(Type=HandleType.RECEIVE)
public class HandleConfirmMessage implements HandleMessageService {

	@Override
	@HandleMessageProtocol(id="1",response=false)
	public ResponseResult handle(JSONObject message) {
		System.out.println("ACK->" + message);
		int command_properties = message.getIntValue("command_properties");
		if(command_properties == 2){
			int Package_Number = message.getIntValue("Package_Number");
			//表示网关收到下传配置信息并生效
			if(Package_Number - AckLastCache.getInstance().getLastConfigDownPackageNumber() == 1){
				String Gateway_Id = Integer.toHexString((Integer) message.get("Gateway_Id"));
				//刷新当前网关下待下发的配置
				ConfigManager.getInstance().refresh(Gateway_Id);
				//ConfigTableCache.getInstance().refreshPrepareDownSensor(Gateway_Id);
				return null;
			}
		}
		if(command_properties == 1){
			//需要重发上一次包协议
			String Gateway_Id = Integer.toHexString((Integer) message.get("Gateway_Id"));
			LastOptVo vo = AckLastCache.getInstance().getLastOptVo(Gateway_Id);
			if(null == vo){
				System.out.println("上一次网关发送信息为空，请检查!");
				return null;
			}
			JSONObject data = new JSONObject();
			int Package_Number = message.getIntValue("Package_Number");
			vo.getData().put("Package_Number", Package_Number);
			data.put("id", vo.getId());
			data.put("data", vo.getData());
			ResponseResult result = new SuccessResponseResult(data, true);
			return result;
		}
		
		return null;
	}
	
}
