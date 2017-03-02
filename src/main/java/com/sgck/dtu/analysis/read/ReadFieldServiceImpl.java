package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.common.BaseDataType;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CommonUtils;

public class ReadFieldServiceImpl implements ReadFieldService
{

	@Override
	public Object read(InputStream is, Field field) throws IOException
	{
		// 如果是string做特殊处理
		if (field.getType() == BaseDataType.STRING) {
			String value = null;
			int variableLength = field.getVariableLength();
			byte[] recvHead = new byte[variableLength];
			is.read(recvHead, 0, variableLength);
			if (null != TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding()) {
				value = new String(recvHead);
			} else {
				value = new String(recvHead, TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding());
			}
			
			return value;
		}
		byte[] recvHead = new byte[field.getType().BYTES];
		int tmp;
		int cmd;
		is.read(recvHead, 0, field.getType().BYTES);
		tmp = CommonUtils.bytes2Integer(recvHead);
		cmd = CommonUtils.bytes2Integer(CommonUtils.toLH(tmp));

		String realValueRule = field.getRealValueRule();
		if (null != realValueRule) {
			if (realValueRule.startsWith("*")) {
				return cmd * Double.parseDouble(realValueRule.substring(1));
			}
			if (realValueRule.startsWith("+")) {
				return cmd + Double.parseDouble(realValueRule.substring(1));
			}
			if (realValueRule.startsWith("-")) {
				return cmd - Double.parseDouble(realValueRule.substring(1));
			}
			if (realValueRule.startsWith("/")) {
				return cmd / Double.parseDouble(realValueRule.substring(1));
			}
		}
		return cmd;
	}
	
	@Override
	public void read(InputStream is, Field field, JSONObject newjson) throws IOException
	{
		// 如果是string做特殊处理
		if (field.getType() == BaseDataType.STRING) {
			String value = null;
			int variableLength = field.getVariableLength();
			byte[] recvHead = new byte[variableLength];
			is.read(recvHead, 0, variableLength);
			
			if(SystemConsts.isDebug){
				List<Byte> originalpackage = (List<Byte> )newjson.get(SystemConsts.DATAPACKAGESIGN);
				for(int i=0;i<recvHead.length;i++){
					originalpackage.add(recvHead[i]);
				}
			}
			
			if (null != TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding()) {
				value = new String(recvHead);
			} else {
				value = new String(recvHead, TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding());
			}
			newjson.put(field.getName(), value);
			return;
		}
		byte[] recvHead = new byte[field.getType().BYTES];
		int tmp;
		int cmd;
		is.read(recvHead, 0, field.getType().BYTES);
		
		if(SystemConsts.isDebug){
			List<Byte> originalpackage = (List<Byte> )newjson.get(SystemConsts.DATAPACKAGESIGN);
			for(int i=0;i<recvHead.length;i++){
				originalpackage.add(recvHead[i]);
			}
		}
		
		tmp = CommonUtils.bytes2Integer(recvHead);
		cmd = CommonUtils.bytes2Integer(CommonUtils.toLH(tmp));

		String realValueRule = field.getRealValueRule();
		if (null != realValueRule) {
			if (realValueRule.startsWith("*")) {
				newjson.put(field.getName(), cmd * Double.parseDouble(realValueRule.substring(1)));
				return;
			}
			if (realValueRule.startsWith("+")) {
				newjson.put(field.getName(),cmd + Double.parseDouble(realValueRule.substring(1)));
				 return;
			}
			if (realValueRule.startsWith("-")) {
				newjson.put(field.getName(),cmd - Double.parseDouble(realValueRule.substring(1)));
				return;
			}
			if (realValueRule.startsWith("/")) {
				newjson.put(field.getName(), cmd / Double.parseDouble(realValueRule.substring(1)));
				return;
			}
		}
		newjson.put(field.getName(), cmd);
		return;
	}


	@Override
	public Object read(byte[] recvHead, Field field) throws IOException
	{
		int tmp;
		int cmd;

		// 如果是string做特殊处理
		if (field.getType() == BaseDataType.STRING) {
			String value = null;
			if (null != TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding()) {
				value = new String(recvHead);
			} else {
				value = new String(recvHead, TemplateMessageManager.getInstance().getFCMessageTemplate().getEncoding());
			}
			return value;
		}
		tmp = CommonUtils.bytes2Integer(recvHead);
		cmd = CommonUtils.bytes2Integer(CommonUtils.toLH(tmp));

		return cmd;
	}

}
