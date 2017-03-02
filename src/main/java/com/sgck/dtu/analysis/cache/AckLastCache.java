package com.sgck.dtu.analysis.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.SystemConsts;

/**
 * 通信确认上一次包号，用于判断是否是连续的包序号
 * 
 * @author DELL
 *
 */
public class AckLastCache
{

	private int lastConfigDownPackageNumber = 0;

	private Map<String, LastOptVo> lastopts;

	private AckLastCache()
	{
		lastopts = new ConcurrentHashMap<String, LastOptVo>();
	}

	public void setLastOpt(String gatewayId, LastOptVo vo)
	{
		if (SystemConsts.isDebug) {
			JSONObject json = vo.getData();
			if (null != json) {
				json.remove(SystemConsts.DATAPACKAGESIGN);
			}
		}
		lastopts.put(gatewayId, vo);
	}

	public LastOptVo getLastOptVo(String gatewayId)
	{
		return lastopts.get(gatewayId);
	}

	public void setLastConfigDownPackageNumber(int num)
	{
		this.lastConfigDownPackageNumber = num;
	}

	public int getLastConfigDownPackageNumber()
	{
		return this.lastConfigDownPackageNumber;
	}

	public static AckLastCache getInstance()
	{
		return AckLastCacheHolder.instance;
	}

	private static class AckLastCacheHolder
	{
		private static AckLastCache instance = new AckLastCache();
	}
}
