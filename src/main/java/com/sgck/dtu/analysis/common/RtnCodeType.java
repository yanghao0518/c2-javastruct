package com.sgck.dtu.analysis.common;

public enum RtnCodeType {
	
	Server_Gateway_ACK("1","通信确认"),
	Server_Gateway_Sensor_Setup("6","传感器设置下传给网关（0x06)"),
	Sensor_Gateway_Delete_Sensor("8","删除网关内传感器节点（0x08)");
	
	public String ID;// 对应返回ID
	public String DESC;// 描述

	private RtnCodeType(String ID, String DESC)
	{
		this.ID = ID;
		this.DESC = DESC;
	}
}
