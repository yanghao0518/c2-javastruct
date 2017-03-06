package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.cache.WaveCache;
import com.sgck.dtu.analysis.cache.WaveReadConfig;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.common.SuccessResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理波形前徐包数据
 * 
 * @author DELL
 *
 */
@HandleMessage(Type = HandleType.RECEIVE)
public class HandleWaveMessage implements HandleMessageService
{

	@Override
	@HandleMessageProtocol(id = "4-4", response = true)
	public ResponseResult handle(JSONObject message)
	{
		System.out.println("申请上传波形请求:" + message);
		
		try {
			//包序号
			int Package_Number = message.getIntValue("Package_Number");
			WaveReadConfig waveConfig = new WaveReadConfig();
			
			waveConfig.setCommand_properties(message.getByteValue("command_properties"));
			waveConfig.setCurrentPackageNumber(Package_Number);
			byte Wave_long = message.getByteValue("Wave_long");
			switch (Wave_long)
			{
			case 0:
				waveConfig.setCountPackageNums(512);
				break;
			case 1:
				waveConfig.setCountPackageNums(1024);
				break;
			case 2:
				waveConfig.setCountPackageNums(2048);
				break;	
			case 3:
				waveConfig.setCountPackageNums(4096);
				break;	
			case 4:
				waveConfig.setCountPackageNums(8192);
				break;	
				
			default:
				waveConfig.setCountPackageNums(1024);
				break;
			}
			waveConfig.setGateway_Id(message.getIntValue("Gateway_Id"));
			waveConfig.setSensorId(message.getIntValue("Sensor_Id"));
			waveConfig.setWave_attribute(message.getByteValue("Wave_attribute"));
			waveConfig.setWave_coefficient(message.getIntValue("Wave_coefficient"));
			waveConfig.setResidue(waveConfig.getCountPackageNums());

			WaveCache.getInstance().setConfig(waveConfig);
			
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		
		//发送确认包

		JSONObject config = new JSONObject();

		config.put("Package_Number", message.get("Package_Number"));
		//config.put("Gateway_Id", message.get("Gateway_Id"));
		config.put("Gateway_Id", 0x12345678);
		config.put("Sensor_Id", message.get("Sensor_Id"));
		config.put("command_properties", 2);

		JSONObject data = new JSONObject();
		data.put("id", "1");// 传感器设置下发给网关
		data.put("data", config);
		ResponseResult result = new SuccessResponseResult(data);
		
		return result;
	}

}
