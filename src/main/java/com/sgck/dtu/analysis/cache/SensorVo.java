package com.sgck.dtu.analysis.cache;

import com.alibaba.fastjson.JSONObject;

/**
 * 传感器信息类
 * 
 * @author DELL
 *
 */
public class SensorVo implements Comparable<SensorVo>
{

	private String Sensor_Id;// 传感器序号
	private String Gateway_Id;// 所属网关
	private int riss;// 信号强度
	private JSONObject config;// 配置信息

	public int getRiss()
	{
		return riss;
	}

	public void setRiss(int riss)
	{
		this.riss = riss;
	}

	public String getSensor_Id()
	{
		return Sensor_Id;
	}

	public void setSensor_Id(String sensor_Id)
	{
		Sensor_Id = sensor_Id;
	}

	public String getGateway_Id()
	{
		return Gateway_Id;
	}

	public void setGateway_Id(String gateway_Id)
	{
		Gateway_Id = gateway_Id;
	}

	public JSONObject getConfig()
	{
		if (null == config) {
			return new JSONObject();
		}
		return config;
	}

	public void setConfig(JSONObject config)
	{
		this.config = config;
	}

	@Override
	public int compareTo(SensorVo o)
	{
		if (this.getRiss() > o.getRiss()) {
			return -1;
		} else if (this.getRiss() < o.getRiss()) {
			return 1;
		} else {
			return 0;
		}

	}

}
