package com.sgck.dtu.analysis.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sgck.dtu.analysis.utiils.HexUtils;

/**
 * 波形缓存
 * 
 * @author DELL
 *
 */
public class WaveCache
{

	// key 为包的起始序号
	private Map<Integer, WaveReadConfig> configMapping;

	// 保存真正的数据
	// 传感器ID -> 加速度/速度 +XYZ联合类型 - >真实数据
	private Map<String, Map<String, List<Number>>> data;
	
	Logger LOG = Logger.getLogger(this.getClass());

	private WaveCache()
	{
		configMapping = new ConcurrentHashMap<>();
		data = new ConcurrentHashMap<>();
	}

	public List<Number> getData(String sensorId, int t1, int t2)
	{
		if (data.containsKey(sensorId)) {

			Map<String, List<Number>> values = data.get(sensorId);
			String key = getKey(t1, t2);
			return values.get(key);

		}
		return null;
	}

	private String getKey(int t1, int t2)
	{
		return t1 + "_" + t2;
	}

	public void dealRealData(WaveReadConfig readConfig)
	{
		Map<String, List<Number>> value = new ConcurrentHashMap<>();
		value.put(getType(readConfig.getWave_attribute()), readConfig.getRealDatas());
		// 判断类型
		data.put(HexUtils.getHexString(readConfig.getSensorId()), value);
	}

	public String getType(byte Wave_attribute)
	{
		// 转换成二进制字符串
		String str = HexUtils.getBinaryStrFromByte(Wave_attribute);
		// 判断字符串
		// 截取后三位
		String last = str.substring(str.length() - 3, str.length());
		int type1 = 0;
		// 判断加速度和速度
		if (last.equals("000")) {
			// 0
			type1 = 0;
		} else if (last.equals("001")) {
			// 加速度
			type1 = 1;
		} else if (last.equals("010")) {
			// 速度
			type1 = 2;
		}
		// 截取倒数五位和四位
		String last1 = str.substring(str.length() - 5, str.length()).substring(0, 2);
		int type2 = 0;
		if (last1.equals("00")) {
			// X
			type2 = 0;
		} else if (last1.equals("01")) {
			// Y
			type2 = 1;
		} else if (last1.equals("10")) {
			// Z
			type2 = 2;
		}
		return getKey(type1, type2);
	}

	public boolean isContainCurrentPackage(Integer currentPackageNum)
	{
		return configMapping.containsKey(currentPackageNum);
	}

	public WaveReadConfig getCurrentWaveReadConfig(Integer currentPackageNum)
	{
		return configMapping.get(currentPackageNum);
	}

	public void setConfig(WaveReadConfig config)
	{
		if (null != config) {
			configMapping.put(config.getCurrentPackageNumber(), config);
		}
	}

	public void removeConfig(Integer currentPackageNumber)
	{
		configMapping.remove(currentPackageNumber);
	}

	public boolean check(WaveReadConfig old, WaveReadConfig news)
	{

		if (null != old && null != news) {

			if (old.getGateway_Id() == news.getGateway_Id() && old.getSensorId() == news.getSensorId()) {
				if (old.getCurrentPackageNumber() + 1 == news.getCurrentPackageNumber()) {
					return true;
				} else {
					LOG.error("检查波形连续传包出问题：包序号不连续！currentPackageNumber:" + news.getCurrentPackageNumber() + ",lastPackageNumber:" + old.getCurrentPackageNumber());
				}
			} else {
				LOG.error("检查波形连续传包出问题：网关ID与传感器ID有不一致！currentPackageNumber:" + news.getCurrentPackageNumber());
			}
		}

		return false;
	}

	public static WaveCache getInstance()
	{
		return WaveCacheHolder.instance;
	}

	private static class WaveCacheHolder
	{
		private static WaveCache instance = new WaveCache();
	}

}
