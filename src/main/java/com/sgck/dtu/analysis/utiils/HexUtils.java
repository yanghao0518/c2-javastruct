package com.sgck.dtu.analysis.utiils;

public final class HexUtils
{

	public static String getHexString(Integer i){
		return "0X"+ Integer.toHexString(i);
	}
	
	public static String getHexStringL(Long i){
		return "0X"+ Long.toHexString(i);
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
	
	 /** 
     * 把byte转化成2进制字符串 
     * @param b 
     * @return 
     */  
    public static String getBinaryStrFromByte(byte b){  
        String result ="";  
        byte a = b; ;  
        for (int i = 0; i < 8; i++){  
            byte c=a;  
            a=(byte)(a>>1);//每移一位如同将10进制数除以2并去掉余数。  
            a=(byte)(a<<1);  
            if(a==c){  
                result="0"+result;  
            }else{  
                result="1"+result;  
            }  
            a=(byte)(a>>1);  
        }  
        return result;  
    }  
}
