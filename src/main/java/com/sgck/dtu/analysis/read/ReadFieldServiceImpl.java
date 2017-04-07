package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.cache.WaveCache;
import com.sgck.dtu.analysis.cache.WaveReadConfig;
import com.sgck.dtu.analysis.common.BaseDataType;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CommonUtils;
import com.sgck.dtu.analysis.writer.ResponseMessageService;
import com.sgck.dtu.analysis.writer.ResponseMessageServiceImpl;

public class ReadFieldServiceImpl implements ReadFieldService
{

	Logger LOG = Logger.getLogger(this.getClass());

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();

	private ReadInput readInput = null;

	public ReadFieldServiceImpl()
	{
		if (SystemConsts.order == ByteOrder.BIG_ENDIAN) {
			readInput = new ReadInputBigger();
		} else {
			readInput = new ReadInputLitter();
		}
	}

	public ResponseMessageService getResponseMessageService()
	{
		return responseMessageService;
	}

	public void setResponseMessageService(ResponseMessageService responseMessageService)
	{
		this.responseMessageService = responseMessageService;
	}

	@Override
	public Object read(C2DataInput is, Field field) throws IOException
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
	public void read(C2DataInput is, Field field, JSONObject newjson) throws IOException
	{
		// 如果是string做特殊处理
		if (field.getType() == BaseDataType.STRING) {
			String value = null;
			int variableLength = field.getVariableLength();
			byte[] recvHead = new byte[variableLength];
			is.read(recvHead, 0, variableLength);

			if (SystemConsts.isDebug) {
				List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
				for (int i = 0; i < recvHead.length; i++) {
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

		if (SystemConsts.isDebug) {
			List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
			for (int i = 0; i < recvHead.length; i++) {
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
				newjson.put(field.getName(), cmd + Double.parseDouble(realValueRule.substring(1)));
				return;
			}
			if (realValueRule.startsWith("-")) {
				newjson.put(field.getName(), cmd - Double.parseDouble(realValueRule.substring(1)));
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

	@Override
	public void read(InputStream in, Field field, JSONObject newjson, Object... params) throws Exception
	{
		// TODO Auto-generated method stub
		List<Byte> list = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
		switch (field.getType())
		{
		case BOOLEAN:
			newjson.put(field.getName(), readInput.readBoolean(in, list));
			break;
		case BYTE:
			newjson.put(field.getName(), readInput.readByte(in, list));
			break;
		case CHAR:
			newjson.put(field.getName(), readInput.readCharC(in, list));
			break;
		case SHORT:
			newjson.put(field.getName(), readInput.readShort(in, list));
			break;
		case USHORT:
			newjson.put(field.getName(), readInput.readUnsignedShort(in, list));
			break;
		case INT:
			newjson.put(field.getName(), readInput.readInt(in, list));
			break;
		case UINT:
			newjson.put(field.getName(), readInput.readUnsignedInt(in, list));
			break;
		case FLOAT:
			newjson.put(field.getName(), readInput.readFloat(in, list));
			break;
		case DOUBLE:
			newjson.put(field.getName(), readInput.readDouble(in, list));
			break;
		case STRING:
			int len = field.getVariableLength();
			newjson.put(field.getName(), readInput.readUTF(in, len, list));
			break;
		case USHORTARRAY:
			// 注意此次可能是波形，暂时写死判断在此处
			int currentPackageNum = newjson.getIntValue("Package_Number");
			int defaultArrayLength = SystemConsts.transfer_wave_long;
			WaveReadConfig config = WaveCache.getInstance().getCurrentWaveReadConfig(currentPackageNum - 1);

			if (null != config) {
				// 剩余量小于1024个点
				if (config.getResidue() > 0 && config.getResidue() < SystemConsts.transfer_wave_long) {
					defaultArrayLength = config.getResidue();
				}
			}
			int[] datas = new int[defaultArrayLength];
			List<Number> wavePackage = (List<Number>)newjson.get("wavePackage");
			if(null == wavePackage){
				wavePackage = new ArrayList();
				newjson.put("wavePackage", wavePackage);
			}
			readInput.readUShortArray(in, datas, list,wavePackage);
			newjson.put(field.getName(), datas);
			return;
		default:
			throw new DtuMessageException(DtuMessageException.UNKNOWN_EXCEPTION, "not undefined jtype:" + field.getType().JTYPE);

		}

		String realValueRule = field.getRealValueRule();
		if (null != realValueRule && newjson.containsKey(field.getName())) {
			Object value = newjson.get(field.getName());
			if (value instanceof Number) {
				Number v = (Number) value;
				if (realValueRule.startsWith("*")) {
					newjson.put(field.getName(), v.doubleValue() * Double.parseDouble(realValueRule.substring(1)));
				}
				if (realValueRule.startsWith("+")) {
					newjson.put(field.getName(), v.doubleValue() + Double.parseDouble(realValueRule.substring(1)));
				}
				if (realValueRule.startsWith("-")) {
					newjson.put(field.getName(), v.doubleValue() - Double.parseDouble(realValueRule.substring(1)));
				}
				if (realValueRule.startsWith("/")) {
					newjson.put(field.getName(), v.doubleValue() / Double.parseDouble(realValueRule.substring(1)));
				}
			}
		}

		if (SystemConsts.isStartBCCCheck) {
			check(field, newjson, params);
		}
	}

	private void check(Field field, final JSONObject newjson, final Object... params)
	{
		Map<String, CheckField> checkmapping = TemplateMessageManager.getInstance().getFCMessageTemplate().getCheckFields();
		List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
		if (null != checkmapping && !checkmapping.isEmpty()) {

			for (String checkId : checkmapping.keySet()) {
				CheckField check = checkmapping.get(checkId);
				check.check(field, new CheckFail()
				{
					@Override
					public void doSomething()
					{
						boolean isResponse = (boolean) params[0];
						if (!isResponse) {
							LOG.error("校验 BCC 失败!");
							return;
						}
						String id = (String) params[1];
						OutputStream os = (OutputStream) params[2];
						// 做跳转
						JSONObject json = new JSONObject();
						// 网关ID
						json.put("Gateway_Id", newjson.get("Gateway_Id"));
						// 上行包
						json.put("Package_Number", newjson.get("Package_Number"));
						// 返回校验错误//如果是波形需要返回0X05终止传输
						if (id.equals("4-4") || id.equals("4-5")) {
							json.put("command_properties", 5);
						} else {
							json.put("command_properties", 4);
						}
						try {
							byte[] data = responseMessageService.resolve("1", json);
							os.write(data);
						} catch (DtuMessageException | IOException e) {
							e.printStackTrace();
						}

					}

				}, new Object[] { originalpackage, newjson.get("BCC") });
			}
		}
	}

}
