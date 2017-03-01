package com.sgck.dtu.analysis.cache;

import com.alibaba.fastjson.JSONObject;

public class LastOptVo
{
	private int gatewayId;
	private int packageNumber;
	private String id;
	private JSONObject data;

	public LastOptVo(){
		
	}
	
	public LastOptVo(int gatewayId,String id,JSONObject data){
		this.gatewayId = gatewayId;
		this.id = id;
		this.data = data;
	}
	
	
	public LastOptVo(int gatewayId,int packageNumber,String id,JSONObject data){
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

	public int getGatewayId()
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
		return data;
	}

	public void setData(JSONObject data)
	{
		this.data = data;
	}

}
