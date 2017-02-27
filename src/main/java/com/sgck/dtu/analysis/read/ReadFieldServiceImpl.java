package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;

import com.sgck.dtu.analysis.common.BaseDataType;
import com.sgck.dtu.analysis.common.Message.Field;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CommonUtils;

public class ReadFieldServiceImpl implements ReadFieldService
{

	@Override
	public Object read(InputStream is, Field field) throws IOException
	{
		byte[] recvHead = new byte[field.getType().BYTES];
		int tmp;
		int cmd;
		is.read(recvHead, 0, field.getType().BYTES);
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
