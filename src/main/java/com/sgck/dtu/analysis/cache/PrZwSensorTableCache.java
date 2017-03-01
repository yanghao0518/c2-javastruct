package com.sgck.dtu.analysis.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 待组网的信息表
 * 
 * @author DELL
 *
 */
public class PrZwSensorTableCache
{

	// 按照网关ID分类
	private Map<String, Map<String, SensorVo>> nodes = null;

	// 单线程下运行
	private SensorVo currentVote = null;

	private PrZwSensorTableCache()
	{
		nodes = new ConcurrentHashMap<String, Map<String, SensorVo>>();
	}

	// 比较信号强度
	public SensorVo compareRiss(String Sensorid, int riss)
	{
		Iterator<Map.Entry<String, Map<String, SensorVo>>> entries = nodes.entrySet().iterator();
		List<SensorVo> matchs = new ArrayList<SensorVo>();
		while (entries.hasNext()) {
			Map.Entry<String, Map<String, SensorVo>> entry = entries.next();
			if (entry.getValue().containsKey(Sensorid)) {
				matchs.add(entry.getValue().get(Sensorid));
			}
		}
		Collections.sort(matchs);
		return matchs.get(0);

	}

	// 选举一个带比较对象
	public SensorVo voteOne()
	{
		if (null == currentVote) {
			revote();
		}
		return currentVote;
	}

	// 重新选举
	private void revote()
	{
		Iterator<Map.Entry<String, Map<String, SensorVo>>> entries = nodes.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Map<String, SensorVo>> entry = entries.next();
			Map<String, SensorVo> value = entry.getValue();
			if (null != value) {
				Iterator<Map.Entry<String, SensorVo>> entries1 = value.entrySet().iterator();
				while (entries1.hasNext()) {
					currentVote = entries1.next().getValue();
				}
			}

		}
	}

	public void addorupdate(SensorVo vo)
	{
		synchronized (vo.getGateway_Id()) {
			Map<String, SensorVo> map = nodes.get(vo.getGateway_Id());
			if (null == map) {
				map = new ConcurrentHashMap<String, SensorVo>();
				this.nodes.put(vo.getGateway_Id(), map);
			}
			map.put(vo.getSensor_Id(), vo);
		}
	}

	public void remove(String Gateway_Id, String Sensor_Id)
	{
		synchronized (Gateway_Id) {
			if (null != currentVote && currentVote.getGateway_Id().equals(Gateway_Id) && currentVote.getSensor_Id().equals(Sensor_Id)) {
				currentVote = null;
				revote();
			}
			Map<String, SensorVo> map = nodes.get(Gateway_Id);
			if (null != map) {
				map.remove(Sensor_Id);
			}
		}
	}

	public void remove(String Sensor_Id)
	{
		Iterator<Map.Entry<String, Map<String, SensorVo>>> entries = nodes.entrySet().iterator();
		if (null != currentVote && currentVote.getSensor_Id().equals(Sensor_Id)) {
			currentVote = null;
			revote();
		}
		while (entries.hasNext()) {
			Map.Entry<String, Map<String, SensorVo>> entry = entries.next();
			Map<String, SensorVo> mapping = entry.getValue();
			// String Gateway_Id = entry.getKey();
			if (mapping.containsKey(Sensor_Id)) {
				mapping.remove(Sensor_Id);
			}
		}
	}

	public SensorVo selectById(String Gateway_Id, String Sensor_Id)
	{
		synchronized (Gateway_Id) {
			Map<String, SensorVo> map = nodes.get(Gateway_Id);
			if (null != map) {
				return map.get(Sensor_Id);
			}
			return null;
		}
	}

	public boolean isContain(String Gateway_Id, String Sensor_Id)
	{
		Map<String, SensorVo> map = nodes.get(Gateway_Id);
		return (null != map && map.containsKey(Sensor_Id));
	}

	public boolean isContain(String Sensor_Id)
	{
		Iterator<Map.Entry<String, Map<String, SensorVo>>> entries = nodes.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Map<String, SensorVo>> entry = entries.next();
			Map<String, SensorVo> mapping = entry.getValue();
			return mapping.containsKey(Sensor_Id);
		}
		return false;
	}

	public void setConfig(String Sensor_Id, JSONObject json)
	{
		Iterator<Map.Entry<String, Map<String, SensorVo>>> entries = nodes.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Map<String, SensorVo>> entry = entries.next();
			Map<String, SensorVo> mapping = entry.getValue();
			if (mapping.containsKey(Sensor_Id)) {
				SensorVo vo = mapping.get(Sensor_Id);
				vo.setConfig(json);
			}
		}
	}

	public static PrZwSensorTableCache getInstance()
	{
		return PrZwSensorTableCacheHolder.instance;
	}

	private static class PrZwSensorTableCacheHolder
	{
		private static PrZwSensorTableCache instance = new PrZwSensorTableCache();
	}
}
