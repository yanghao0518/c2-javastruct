package com.sgck.dtu.analysis.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 待下发的信息表
 * 
 * @author DELL
 *
 */
public class PrDownSensorTableCache
{

	private Map<String, SensorVo> nodes = null;

	private PrDownSensorTableCache()
	{
		nodes = new ConcurrentHashMap<String, SensorVo>();
	}

	public void addorupdate(SensorVo vo)
	{
		if(this.nodes.containsKey(vo.getSensor_Id())){
			return;
		}
		this.nodes.put(vo.getSensor_Id(), vo);
	}
	
	//强制加入
	public void addorupdateforce(SensorVo vo)
	{
		this.nodes.put(vo.getSensor_Id(), vo);
	}

	public void remove(String Sensor_Id)
	{
		this.nodes.remove(Sensor_Id);
	}
	
	public boolean isContain(String Sensor_Id)
	{
		return this.nodes.containsKey(Sensor_Id);
	}

	public SensorVo selectById(String Sensor_Id)
	{
		return this.nodes.get(Sensor_Id);
	}
	
	public synchronized SensorVo selectByGatewayId(String GatewayId)
	{
		Iterator<Map.Entry<String, SensorVo>> entries = nodes.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, SensorVo> entry = entries.next();
			SensorVo tmp = entry.getValue();
			if (tmp.getGateway_Id().equals(GatewayId)) {
				return tmp;
			}
		}
		return null;
	}

	public static PrDownSensorTableCache getInstance()
	{
		return PrDownSensorTableCacheHolder.instance;
	}

	private static class PrDownSensorTableCacheHolder
	{
		private static PrDownSensorTableCache instance = new PrDownSensorTableCache();
	}
}
