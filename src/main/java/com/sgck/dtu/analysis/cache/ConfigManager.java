package com.sgck.dtu.analysis.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 配置管理类
 * 
 * @author DELL
 *
 */
public class ConfigManager
{
	// 当前下传的传感器
	Map<String, SensorVo> currentDownSensor = null;

	private ConfigManager()
	{
		currentDownSensor = new ConcurrentHashMap<String, SensorVo>();
	}

	// 刷新当前下传传感器配置
	public void refresh(String gatewayId)
	{
		synchronized (gatewayId) {
			if (currentDownSensor.containsKey(gatewayId)) {
				String oldSensor_Id = currentDownSensor.get(gatewayId).getSensor_Id();
				PrDownSensorTableCache.getInstance().remove(oldSensor_Id);
				currentDownSensor.remove(gatewayId);
			}
			SensorVo sensorVo = PrDownSensorTableCache.getInstance().selectByGatewayId(gatewayId);
			if (null != sensorVo) {
				currentDownSensor.put(gatewayId, sensorVo);
			}
		}

	}

	// 获取当前的带下发的配置
	public SensorVo getCurrentDownSensor(String gatewayId)
	{
		synchronized (gatewayId) {
			if (!currentDownSensor.containsKey(gatewayId)) {
				refresh(gatewayId);
			}
			return currentDownSensor.get(gatewayId);
		}

	}

	// 和接口对接程序

	// 处理保存
	// 批量提交上来的网关和传感器ID的关联关系
	public void dealSaveGatewayIdAndSensorIdRef(String gatewayId, List<String> Sensorids)
	{
		// 首先判断记录信息是否存在传感器信息
		for (String sensorid : Sensorids) {
			if (SensorTableCache.getInstance().isContain(sensorid)) {
				SensorVo vo = SensorTableCache.getInstance().selectById(sensorid);
				vo.setGateway_Id(gatewayId);
				SensorTableCache.getInstance().addorupdate(vo);
			} else {
				SensorVo vo = new SensorVo();
				vo.setSensor_Id(sensorid);
				vo.setGateway_Id(gatewayId);
				SensorTableCache.getInstance().addorupdate(vo);
			}

		}

	}
	
	
	// 接收网关上传的组网信息
	public void dealAcceptZwSensorInfoByWg(String gatewayId, String sensorid, int riss)
	{
		//先不处理，只做页面显示
		//DataCache.getInstance().refreshUploadZwInfo(sensorid, gatewayId, riss);

	}
	

	// 接收网关上传的组网信息
	public void dealAcceptZwSensorInfoByWgBak(String gatewayId, String sensorid, int riss)
	{
		// 判断记录信息是否存在
		if (SensorTableCache.getInstance().isContain(sensorid)) {
			SensorVo vo = SensorTableCache.getInstance().selectById(sensorid);
			vo.setRiss(riss);
		} else {
			SensorVo vo = new SensorVo();
			vo.setSensor_Id(sensorid);
			// vo.setGateway_Id(gatewayId);
			vo.setRiss(riss);
			SensorTableCache.getInstance().addorupdate(vo);
		}
		// 判断待下发列表是否存在，如果存在，先不处理

		// 判断组网列表是否存在
		if (PrZwSensorTableCache.getInstance().isContain(gatewayId, sensorid)) {
			SensorVo vo = PrZwSensorTableCache.getInstance().selectById(gatewayId, sensorid);
			vo.setRiss(riss);
			PrZwSensorTableCache.getInstance().addorupdate(vo);
		} else {
			SensorVo vo = new SensorVo();
			vo.setSensor_Id(sensorid);
			vo.setGateway_Id(gatewayId);
			vo.setRiss(riss);
			PrZwSensorTableCache.getInstance().addorupdate(vo);
		}

	}

	// 接收页面配置发送改变的信息
	public void dealSaveConfigByHtml(String sensorid, JSONObject config)
	{
		// 判断记录信息是否存在
		if (SensorTableCache.getInstance().isContain(sensorid)) {
			SensorVo vo = SensorTableCache.getInstance().selectById(sensorid);
			vo.setConfig(config);
		} else {
			SensorVo vo = new SensorVo();
			vo.setSensor_Id(sensorid);
			vo.setConfig(config);
			SensorTableCache.getInstance().addorupdate(vo);
		}

		// 判断组网列表是否存在,如果存在更新配置
		//PrZwSensorTableCache.getInstance().setConfig(sensorid, config);

		// 判断待下发列表是否存在
		if (PrDownSensorTableCache.getInstance().isContain(sensorid)) {
			SensorVo vo = PrDownSensorTableCache.getInstance().selectById(sensorid);
			if (null != vo) {
				vo.setConfig(config);
			}
		}

		// 当前下发列表是否存在
		setCurrentDownSensorInfo(sensorid,config);
	}
	
	private void setCurrentDownSensorInfo(String sensorid,JSONObject config){
		// 当前下发列表是否存在
				Iterator<Map.Entry<String, SensorVo>> entries = currentDownSensor.entrySet().iterator();
				while (entries.hasNext()) {
					SensorVo vo = entries.next().getValue();
					if( null != vo && vo.getSensor_Id().equals(sensorid)){
						vo.setConfig(config);
					}
				}
	}
	
	private boolean isContainFromCurrentDown(String sensorid){
		// 当前下发列表是否存在
				Iterator<Map.Entry<String, SensorVo>> entries = currentDownSensor.entrySet().iterator();
				while (entries.hasNext()) {
					SensorVo vo = entries.next().getValue();
					if( null != vo && vo.getSensor_Id().equals(sensorid)){
						return true;
					}
				}
				return false;
	}

	// 处理下发按钮
	public void dealSendConfigByHtml(String sensorid, JSONObject config)
	{
		// 下发列表存在或者当前下发存在，则判断组网列表是否存储，如果已经存在刷新配置
		// 判断待下发列表
		if (PrDownSensorTableCache.getInstance().isContain(sensorid)) {
			SensorVo vo = PrDownSensorTableCache.getInstance().selectById(sensorid);
			vo.setConfig(config);
			// 当前下发列表是否存在
			setCurrentDownSensorInfo(sensorid,config);
			return;
		}
		// 判断组网列表是否存在,如果存在更新配置
		//PrZwSensorTableCache.getInstance().setConfig(sensorid, config);
	}
	
	// 批量提交上来的网关和传感器ID的关联关系
		public void dealSendGatewayIdAndSensorIdRef(String gatewayId, List<String> Sensorids)
		{
			for (String sensorid : Sensorids) {
				
				// 其次判断待组网缓存，如果存在则直接删除原待组网缓存，直接扔到待下发
				//组网先不考虑
				/*if (PrZwSensorTableCache.getInstance().isContain(gatewayId, sensorid)) {
					SensorVo vo = PrZwSensorTableCache.getInstance().selectById(gatewayId, sensorid);
					// 放入待下发列表里面
					PrDownSensorTableCache.getInstance().addorupdate(vo);
					// 从待组网列表移除
					PrZwSensorTableCache.getInstance().remove(sensorid);

				}else{*/
					//如果在当前发送列表则不做处理
					if(isContainFromCurrentDown(sensorid)){
						continue;
					}
					//判断待下发列表是否存在
					SensorVo vo = null;
					if(PrDownSensorTableCache.getInstance().isContain(sensorid)){
						vo = PrDownSensorTableCache.getInstance().selectById(sensorid);
					}else{
						vo = new SensorVo();
						vo.setSensor_Id(sensorid);
					}
					vo.setGateway_Id(gatewayId);
					// 放入待下发列表里面
					PrDownSensorTableCache.getInstance().addorupdate(vo);
				}

			//}

		}
		
		public static ConfigManager getInstance()
		{
			return ConfigManagerHolder.instance;
		}

		private static class ConfigManagerHolder
		{
			private static ConfigManager instance = new ConfigManager();
		}

}
