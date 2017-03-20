package com.sgck.dtu.analysis.cache;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.SystemConsts;

public class LastOptVo
{
	private long gatewayId;
	private int packageNumber;
	private String id;
	private JSONObject data;

	public LastOptVo(){
		
	}
	
	public LastOptVo(long gatewayId,String id,JSONObject data){
		this.gatewayId = gatewayId;
		this.id = id;
		this.data = data;
	}
	
	
	public LastOptVo(long gatewayId,int packageNumber,String id,JSONObject data){
		this.gatewayId = gatewayId;
		this.packageNumber = packageNumber;
		this.id = id;
		this.data = data;
	}
	
	
	
	public int getPackageNumber()
	{
		return packageNumber;
	}

	public void setPackageNumber(int packageNumber)
	{
		this.packageNumber = packageNumber;
	}

	public long getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId(int gatewayId)
	{
		this.gatewayId = gatewayId;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public JSONObject getData()
	{
		if (SystemConsts.isDebug && null != data) {
			data.remove(SystemConsts.DATAPACKAGESIGN);
		}
		return data;
	}

	public void setData(JSONObject data)
	{
		this.data = data;
	}

}
