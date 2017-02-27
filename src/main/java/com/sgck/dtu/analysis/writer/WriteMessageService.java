package com.sgck.dtu.analysis.writer;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.Message;

public interface WriteMessageService
{
	public int write(Message template, JSONObject content, byte[] buffer, int startIndex);
}
