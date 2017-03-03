package com.sgck.dtu.analysis.utiils;

public final class HexUtils
{

	public static String getHexString(Integer i){
		return "0X"+ Integer.toHexString(i);
	}
	
	public static Integer getIntFromHex(String str){
		if(str.startsWith("0X") || str.startsWith("0x")){
			str = str.substring(2);
			return Integer.parseInt(str, 16);
		}
		return  Integer.parseInt(str);
	}
	
	public static String unStringFromHex(String str){
		if(str.startsWith("0X") || str.startsWith("0x")){
			str = str.substring(2);
			return str;
		}
		return  str;
	}
	
	public static String toStringFromHex(String str){
		if(null!=str && !(str.startsWith("0X")||str.startsWith("0x"))){
			return "0X" + str;
		}
		return str;
	}
}
