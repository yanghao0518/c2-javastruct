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
 * 处理波形后续包数据
 * 
 * @author DELL
 *
 */
@HandleMessage(Type = HandleType.RECEIVE)
public class HandleWaveNextMessage implements HandleMessageService
{

	@Override
	@HandleMessageProtocol(id = "4-5", response = true)
	public ResponseResult handle(JSONObject message)
	{
		System.out.println("上传一包波形:" + message);

		try {
			// 包序号
			int Package_Number = message.getIntValue("Package_Number");
			if (!WaveCache.getInstance().isContainCurrentPackage(Package_Number - 1)) {
				// 返回错误
				JSONObject config = new JSONObject();

				config.put("Package_Number", message.get("Package_Number"));
				config.put("Gateway_Id", message.get("Gateway_Id"));
				config.put("Sensor_Id", message.get("Sensor_Id"));
				config.put("command_properties", 5);

				JSONObject data = new JSONObject();
				data.put("id", "1");// 传感器设置下发给网关
				data.put("data", config);
				ResponseResult result = new SuccessResponseResult(data);

				return result;
			}

			// 获取数据点
			int[] datas = (int[]) message.get("Wave_data");
			int size = datas.length;

			WaveReadConfig w1 = WaveCache.getInstance().getCurrentWaveReadConfig(Package_Number - 1);
			w1.addData(datas);
			w1.setResidue(w1.getResidue() - size);
			w1.setCurrentPackageNumber(Package_Number);
			WaveCache.getInstance().removeConfig(Package_Number - 1);
			WaveCache.getInstance().setConfig(w1);

			// 判断是否为最后一包数据
			byte command_properties = message.getByteValue("command_properties");
			if (command_properties == 6) {
				// 结束包
				WaveCache.getInstance().dealRealData(w1);
				WaveCache.getInstance().removeConfig(w1.getCurrentPackageNumber());
			}

		} catch (Exception e) {
			e.fillInStackTrace();
		}

		// 发送确认包

		JSONObject config = new JSONObject();

		config.put("Package_Number", message.get("Package_Number"));
		config.put("Gateway_Id", message.get("Gateway_Id"));
		config.put("Sensor_Id", message.get("Sensor_Id"));
		config.put("command_properties", 2);

		JSONObject data = new JSONObject();
		data.put("id", "1");// 传感器设置下发给网关
		data.put("data", config);
		ResponseResult result = new SuccessResponseResult(data);

		return result;
	}

}
