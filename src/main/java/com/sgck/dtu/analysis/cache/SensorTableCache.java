package com.sgck.dtu.analysis.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟传感器表
 * 包括已经组网和未组网的数据
 * @author DELL
 *
 */
public class SensorTableCache
{

	private Map<String, SensorVo> nodes = null;

	private SensorTableCache()
	{
		nodes = new ConcurrentHashMap<String, SensorVo>();
	}

	public void addorupdate(SensorVo vo)
	{
		this.nodes.put(vo.getSensor_Id(), vo);
	}

	public void remove(String Sensor_Id)
	{
		this.nodes.remove(Sensor_Id);
	}

	public SensorVo selectById(String Sensor_Id)
	{
		return this.nodes.get(Sensor_Id);
	}
	
	public boolean isContain(String Sensor_Id){
		return this.nodes.containsKey(Sensor_Id);
	}

	public static SensorTableCache getInstance()
	{
		return SensorTableCacheHolder.instance;
	}

	private static class SensorTableCacheHolder
	{
		private static SensorTableCache instance = new SensorTableCache();
	}
}
