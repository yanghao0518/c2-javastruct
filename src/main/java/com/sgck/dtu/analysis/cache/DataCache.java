package com.sgck.dtu.analysis.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据缓存
 * 
 * @author DELL
 *
 */
public class DataCache
{
	// 这个特征值随时会变，没有绑定具体的网关ID,是显示页面
	private JSONObject pubCharacter;

	// 按照传感器ID以及类型做为key显示特征值
	private final int maxPreCharacterLength = 1000;
	
	private Map<String, List<Number>> sensorTypeCharacters;

	//网关上传的组网信息，暂时只做显示
	private SensorVo uploadZwInfo;
	
	private DataCache()
	{
		sensorTypeCharacters = new ConcurrentHashMap<String, List<Number>>();
		uploadZwInfo = new SensorVo();
	}
	
	public void refreshUploadZwInfo(SensorVo vo){
		this.uploadZwInfo = vo;
	}
	
	public void refreshUploadZwInfo(String sensorId,String wid,int riss,JSONObject json){
		uploadZwInfo.setGateway_Id(wid);
		uploadZwInfo.setSensor_Id(sensorId);
		uploadZwInfo.setRiss(riss);
		uploadZwInfo.setConfig(json);
	}
	
	
	
	public SensorVo getUploadZwInfo(){
		return uploadZwInfo;
	}

	// 特征值显示类型
	public enum Charactertype {
		Temperature("1-1", "温度"), Battery("2-1", "电量"),

		PlusSpeed_X_RMS("3-1", "加速度x方向RMS值"), PlusSpeed_X_PP("3-2", "加速度x方向峰峰值"), PlusSpeed_X_P("3-3", "加速度x方向峰值"),

		PlusSpeed_Y_RMS("3-4", "加速度y方向RMS值"), PlusSpeed_Y_PP("3-5", "加速度y方向峰峰值"), PlusSpeed_Y_P("3-6", "加速度y方向峰值"),

		PlusSpeed_Z_RMS("3-7", "加速度z方向RMS值"), PlusSpeed_Z_PP("3-8", "加速度z方向峰峰值"), PlusSpeed_Z_P("3-9", "加速度z方向峰值"),

		Speed_X_RMS("4-1", "速度x方向RMS值"), Speed_X_PP("4-2", "速度x方向峰峰值"), Speed_X_P("4-3", "速度x方向峰值"),

		Speed_Y_RMS("4-4", "速度y方向RMS值"), Speed_Y_PP("4-5", "速度y方向峰峰值"), Speed_Y_P("4-6", "速度y方向峰值"),

		Speed_Z_RMS("4-7", "速度z方向RMS值"), Speed_Z_PP("4-8", "速度z方向峰峰值"), Speed_Z_P("4-9", "速度z方向峰值");

		public String id;
		public String desc;

		private Charactertype(String id, String desc)
		{
			this.id = id;
			this.desc = desc;
		}
	}

	// 安装传感器ID显示波形图

	public JSONObject getPubCharacter()
	{
		return pubCharacter;
	}

	public void setPubCharacter(JSONObject pubCharacter)
	{
		this.pubCharacter = pubCharacter;
	}

	private String getCharacterKey(String Sensor_Id, String type)
	{

		return type + "_" + Sensor_Id;
	}

	// 新增特征值
	public void addCharacter(String Sensor_Id, String type, Number value)
	{
		String key = getCharacterKey(Sensor_Id, type);
		synchronized (key) {

			List<Number> list = null;
			if (sensorTypeCharacters.containsKey(key)) {
				list = sensorTypeCharacters.get(key);
			}
			if (null == list) {
				list = new ArrayList<Number>();
			}

			list.add(value);
			sensorTypeCharacters.put(key, list);
		}
	}

	// 刷新
	public void refreshCharacter(String Sensor_Id, String type)
	{
		String key = getCharacterKey(Sensor_Id, type);
		synchronized (key) {

			List<Number> list = sensorTypeCharacters.get(key);
			if (null != list && list.size() == maxPreCharacterLength) {
				list = new ArrayList<Number>();
			}
			sensorTypeCharacters.put(key, list);
		}
	}

	// 获取特征值
	public List<Number> getCharacterList(String Sensor_Id, String type)
	{
		refreshCharacter(Sensor_Id, type);
		return sensorTypeCharacters.get(getCharacterKey(Sensor_Id, type));
	}

	public static DataCache getInstance()
	{
		return DataCacheHolder.instance;
	}

	private static class DataCacheHolder
	{
		private static DataCache instance = new DataCache();
	}

}
