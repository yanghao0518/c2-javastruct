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
	Map<String, CurrentDownSensor> currentDownSensor = null;
	
	private Map<String,SensorVo> ts = null;

	public static final int DOWN_BEGIN = 0;

	public static final int DOWN_ING = 1;

	class CurrentDownSensor
	{

		private SensorVo sensorVo;
		private int status;// 状态 0未下发，1下发中，2已完成

		public CurrentDownSensor(SensorVo sensorVo)
		{
			this.sensorVo = sensorVo;
		}

		public SensorVo getSensorVo()
		{
			return sensorVo;
		}

		public void setSensorVo(SensorVo sensorVo)
		{
			this.sensorVo = sensorVo;
		}

		public int getStatus()
		{
			return status;
		}

		public void setStatus(int status)
		{
			this.status = status;
		}

	}

	private ConfigManager()
	{
		currentDownSensor = new ConcurrentHashMap<String, CurrentDownSensor>();
		
		ts = new ConcurrentHashMap<String,SensorVo>();
	}

	// 刷新当前下传传感器配置
	public void refresh(String gatewayId)
	{
		synchronized (gatewayId) {
			if (currentDownSensor.containsKey(gatewayId)) {
				String oldSensor_Id = currentDownSensor.get(gatewayId).getSensorVo().getSensor_Id();
				PrDownSensorTableCache.getInstance().remove(oldSensor_Id);
				currentDownSensor.remove(gatewayId);
			}
			
			//如果特殊列表有则从特殊列表获取
			if(ts.containsKey(gatewayId)){
				currentDownSensor.put(gatewayId, new CurrentDownSensor(ts.get(gatewayId)));
				ts.remove(gatewayId);
				return;
			}
			
			SensorVo sensorVo = PrDownSensorTableCache.getInstance().selectByGatewayId(gatewayId);
			if (null != sensorVo) {
				currentDownSensor.put(gatewayId, new CurrentDownSensor(sensorVo));
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
			return currentDownSensor.get(gatewayId).getSensorVo();
		}

	}

	// 设置下发状态
	public void setDownSensorStatus(String gatewayId, int status)
	{
		synchronized (gatewayId) {
			if (currentDownSensor.containsKey(gatewayId)) {
				currentDownSensor.get(gatewayId).setStatus(status);
			}
		}

	}


	private boolean isContainFromCurrentDown(String sensorid)
	{
		// 当前下发列表是否存在
		Iterator<Map.Entry<String, CurrentDownSensor>> entries = currentDownSensor.entrySet().iterator();
		while (entries.hasNext()) {
			SensorVo vo = entries.next().getValue().getSensorVo();
			if (null != vo && vo.getSensor_Id().equals(sensorid)) {
				return true;
			}
		}
		return false;
	}

	
	
	//加入特殊列表
	public void addTs(String sensorid, String  gatewayId,JSONObject config){
		
		SensorVo vo = new SensorVo();
		vo.setSensor_Id(sensorid);
		vo.setGateway_Id(gatewayId);
		vo.setConfig(config);
		
		ts.put(gatewayId, vo);
		
	}
	
	// 处理下发按钮
	public synchronized void dealSendConfigByHtml(String sensorid, String  gatewayId,JSONObject config)
	{
		// 判断当前下发是否为下发中，如果不是则直接替换掉，如果是则加入额外处理列表中，必定下次发
		if(this.currentDownSensor.containsKey(gatewayId)){
			CurrentDownSensor current = currentDownSensor.get(gatewayId);
			if(current.getStatus() == DOWN_ING){
				//下发中了
				addTs(sensorid,gatewayId,config);
				return;
			}
			if(current.getSensorVo().getSensor_Id().equals(sensorid)){
				current.getSensorVo().setConfig(config);
				return;
			}
			current.getSensorVo().setSensor_Id(sensorid);
			current.getSensorVo().setConfig(config);
			return;
		}
		//直接放入
		SensorVo vo = new SensorVo();
		vo.setSensor_Id(sensorid);
		vo.setGateway_Id(gatewayId);
		vo.setConfig(config);
		CurrentDownSensor current = new CurrentDownSensor(vo);
		currentDownSensor.put(gatewayId, current);
		
	}

	// 批量提交上来的网关和传感器ID的关联关系
	public void dealSendGatewayIdAndSensorIdRef(String gatewayId, List<String> Sensorids)
	{
		for (String sensorid : Sensorids) {

			// 其次判断待组网缓存，如果存在则直接删除原待组网缓存，直接扔到待下发
			// 组网先不考虑
			/*
			 * if (PrZwSensorTableCache.getInstance().isContain(gatewayId,
			 * sensorid)) { SensorVo vo =
			 * PrZwSensorTableCache.getInstance().selectById(gatewayId,
			 * sensorid); // 放入待下发列表里面
			 * PrDownSensorTableCache.getInstance().addorupdate(vo); // 从待组网列表移除
			 * PrZwSensorTableCache.getInstance().remove(sensorid);
			 * 
			 * }else{
			 */
			// 如果在当前发送列表则不做处理
			if (isContainFromCurrentDown(sensorid)) {
				continue;
			}
			// 判断待下发列表是否存在
			SensorVo vo = null;
			if (PrDownSensorTableCache.getInstance().isContain(sensorid)) {
				vo = PrDownSensorTableCache.getInstance().selectById(sensorid);
			} else {
				vo = new SensorVo();
				vo.setSensor_Id(sensorid);
			}
			vo.setGateway_Id(gatewayId);
			// 放入待下发列表里面
			PrDownSensorTableCache.getInstance().addorupdate(vo);
		}

		// }

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
