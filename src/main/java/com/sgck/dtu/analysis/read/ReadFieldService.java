package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.writer.ResponseMessageService;


/**
 * 针对于结构体具体每一项的解析服务
 * @author DELL
 *
 */
public interface ReadFieldService
{
	public Object read(C2DataInput is, Field field) throws IOException;

	public Object read(byte[] currentFieldByte, Field field) throws IOException;
	
	public void read(C2DataInput is, Field field, JSONObject newjson) throws IOException;
	
	public void read(InputStream is, Field field, final JSONObject newjson, final Object... params) throws Exception;
	
	public void setResponseMessageService(ResponseMessageService responseMessageService);
}
