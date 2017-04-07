package com.sgck.dtu.analysis.common;

import java.nio.ByteOrder;

public final class SystemConsts
{
	
	public static boolean isDebug = true;

	public static String DATAPACKAGESIGN = "datapackage";
	
	public static boolean isStartBCCCheck = false;
	
	public static final int DEFAULT_PACKAGE_NUM = 0;
	
	//大小端
	public static ByteOrder order = ByteOrder.BIG_ENDIAN;
	
	public static boolean isTest = false;
	
	//默认每包波形长度
	public static int transfer_wave_long = 1024;
}
