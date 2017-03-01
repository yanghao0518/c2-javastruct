package com.sgck.dtu.analysis.read.handle;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.annotation.HandleMessage;
import com.sgck.dtu.analysis.annotation.HandleMessageProtocol;
import com.sgck.dtu.analysis.annotation.HandleMessage.HandleType;
import com.sgck.dtu.analysis.cache.DataCache;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.read.HandleMessageService;

/**
 * 处理特征值显示
 * 
 * @author DELL
 *
 */
@HandleMessage(Type = HandleType.RECEIVE)
public class HandleCharacterMessage implements HandleMessageService
{

	@Override
	@HandleMessageProtocol(id = "5", response = false)
	public ResponseResult handle(JSONObject message)
	{
		System.out.println("接收特征值信息:" + message);
		// 更新特征值公共缓存
		DataCache.getInstance().setPubCharacter(message);
		// 往趋势图加入数据
		try {
			String Sensor_Id = Integer.toHexString(message.getIntValue("Sensor_Id"));

			// 获取温度
			double Temperature = message.getDoubleValue("Temperature");
			DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Temperature.id, Temperature);
			// 获取电量
			int Battery = message.getIntValue("Battery");
			DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Battery.id, Battery);

			// 接收端数据= 数据/Data_coefficient
			double Data_coefficient = message.getIntValue("Data_coefficient");
			double Data_x_Rms = message.getIntValue("Data_x_Rms") / Data_coefficient;
			double Data_x_PP = message.getIntValue("Data_x_PP") / Data_coefficient;
			double Data_x_P = message.getIntValue("Data_x_P") / Data_coefficient;

			double Data_y_Rms = message.getIntValue("Data_y_Rms") / Data_coefficient;
			double Data_y_PP = message.getIntValue("Data_y_PP") / Data_coefficient;
			double Data_y_P = message.getIntValue("Data_y_P") / Data_coefficient;

			double Data_z_Rms = message.getIntValue("Data_z_Rms") / Data_coefficient;
			double Data_z_PP = message.getIntValue("Data_z_PP") / Data_coefficient;
			double Data_z_P = message.getIntValue("Data_z_P") / Data_coefficient;

			// 用于判断是否为速度、加速度
			int type = message.getIntValue("Character_Attribute");
			if (type == 1) {
				// 加速度
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_X_RMS.id, Data_x_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_X_PP.id, Data_x_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_X_P.id, Data_x_P);

				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Y_RMS.id, Data_y_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Y_PP.id, Data_y_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Y_P.id, Data_y_P);

				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Z_RMS.id, Data_z_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Z_PP.id, Data_z_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.PlusSpeed_Z_P.id, Data_z_P);

			} else {
				// 速度

				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_X_RMS.id, Data_x_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_X_PP.id, Data_x_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_X_P.id, Data_x_P);

				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Y_RMS.id, Data_y_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Y_PP.id, Data_y_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Y_P.id, Data_y_P);

				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Z_RMS.id, Data_z_Rms);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Z_PP.id, Data_z_PP);
				DataCache.getInstance().addCharacter(Sensor_Id, DataCache.Charactertype.Speed_Z_P.id, Data_z_P);
			}

		} catch (Exception e) {
			e.fillInStackTrace();
		}

		return null;
	}

}
