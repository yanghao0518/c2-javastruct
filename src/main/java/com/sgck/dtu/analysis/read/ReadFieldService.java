package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;

import com.sgck.dtu.analysis.common.Message.Field;

/**
 * 针对于结构体具体每一项的解析服务
 * @author DELL
 *
 */
public interface ReadFieldService
{
	public Object read(InputStream is, Field field) throws IOException;

	public Object read(byte[] currentFieldByte, Field field) throws IOException;
}
