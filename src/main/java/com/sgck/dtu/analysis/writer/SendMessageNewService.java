package com.sgck.dtu.analysis.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.LEDataOutputStream;
import com.sgck.dtu.analysis.exception.DtuMessageException;

public interface SendMessageNewService
{

	public List<Byte>  send(LEDataOutputStream os,String protocolid, JSONObject content) throws DtuMessageException, IOException;
}
