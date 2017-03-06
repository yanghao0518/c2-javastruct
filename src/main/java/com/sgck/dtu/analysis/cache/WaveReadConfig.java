package com.sgck.dtu.analysis.cache;

import java.util.ArrayList;
import java.util.List;

public class WaveReadConfig
{
	public final static int transferNumber = 1024;
	private int Package_Number;
	private byte command_properties;
	private int Gateway_Id;
	private int sensorId;
	private int Wave_long;
	private int Wave_coefficient;
	private byte Wave_attribute;
	private int countPackageNums;// 总包数
	private int currentPackageNumber;// 当前包序号,记得是递增
	private int residue;// 当前剩余多少点
	private List<Integer> datas = new ArrayList<>();
	private List<Number> realDatas = new ArrayList<>();

	public void addData(int[] data)
	{
		for (int b : data) {
			datas.add(b);
			realDatas.add(b / (double) Wave_coefficient);
		}
	}
	

	public List<Number> getRealDatas()
	{
		return realDatas;
	}


	public void setRealDatas(List<Number> realDatas)
	{
		this.realDatas = realDatas;
	}



	public int getResidue()
	{
		return residue;
	}

	public void setResidue(int residue)
	{
		this.residue = residue;
	}

	public int getCountPackageNums()
	{
		return countPackageNums;
	}

	public void setCountPackageNums(int countPackageNums)
	{
		this.countPackageNums = countPackageNums;
	}

	public int getCurrentPackageNumber()
	{
		return currentPackageNumber;
	}

	public void setCurrentPackageNumber(int currentPackageNumber)
	{
		this.currentPackageNumber = currentPackageNumber;
	}

	public int getPackage_Number()
	{
		return Package_Number;
	}

	public void setPackage_Number(int package_Number)
	{
		Package_Number = package_Number;
	}

	public byte getCommand_properties()
	{
		return command_properties;
	}

	public void setCommand_properties(byte command_properties)
	{
		this.command_properties = command_properties;
	}

	public int getGateway_Id()
	{
		return Gateway_Id;
	}

	public void setGateway_Id(int gateway_Id)
	{
		Gateway_Id = gateway_Id;
	}

	public int getSensorId()
	{
		return sensorId;
	}

	public void setSensorId(int sensorId)
	{
		this.sensorId = sensorId;
	}

	public int getWave_long()
	{
		return Wave_long;
	}

	public void setWave_long(int wave_long)
	{
		Wave_long = wave_long;
	}

	public int getWave_coefficient()
	{
		return Wave_coefficient;
	}

	public void setWave_coefficient(int wave_coefficient)
	{
		Wave_coefficient = wave_coefficient;
	}

	public byte getWave_attribute()
	{
		return Wave_attribute;
	}

	public void setWave_attribute(byte wave_attribute)
	{
		Wave_attribute = wave_attribute;
	}

}
