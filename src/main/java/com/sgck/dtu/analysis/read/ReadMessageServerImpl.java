package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.cache.AckLastCache;
import com.sgck.dtu.analysis.common.ErrorResponseResult;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.HandleMessageManager;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;
import com.sgck.dtu.analysis.utiils.HexUtils;
import com.sgck.dtu.analysis.writer.ResponseMessageService;
import com.sgck.dtu.analysis.writer.ResponseMessageServiceImpl;

public class ReadMessageServerImpl implements ReadMessageServer
{

	private HandleMessageService defaultHandleFcMessageService = new DefaultHandleMessageServiceImpl();

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();

	private ReadFieldService analysisFieldService = new ReadFieldServiceImpl();

	Logger LOG = Logger.getLogger(this.getClass());

	public ReadMessageServerImpl()
	{
		analysisFieldService.setResponseMessageService(responseMessageService);
	}

	public void setAnalysisFieldService(ReadFieldService analysisFieldService)
	{
		this.analysisFieldService = analysisFieldService;
	}

	public void setDefaultHandleFcMessageService(HandleMessageService defaultHandleFcMessageService)
	{
		this.defaultHandleFcMessageService = new DefaultHandleMessageServiceImpl(defaultHandleFcMessageService);

	}

	@Override
	public void read(InputStream in, OutputStream os) throws Exception
	{
		analysis(in, os);
	}

	public void analysis(InputStream in, OutputStream os) throws Exception
	{

		// 获取head模板
		Message head = TemplateMessageManager.getInstance().getFCMessageTemplate().getHead();

		Field[] fields = head.getFields();
		int startIndex = 0;
		int endIndex = fields.length;
		JSONObject newjson = new JSONObject();

		if (SystemConsts.isDebug) {
			// 记录原始的byte[]集合,用于调试
			List<Byte> lists = new ArrayList<Byte>();
			newjson.put(SystemConsts.DATAPACKAGESIGN, lists);
		}

		// 接收前缀包
		for (int i = startIndex; i < endIndex; i++) {
			this.analysisFieldService.read(in, fields[i], newjson, new Object[] { false });
		}
		// 判断是具体哪个协议
		Integer packageType = newjson.getInteger(head.getPrimaryKey());

		Message content = TemplateMessageManager.getInstance().getFCMessage(packageType.toString());
		if (content == null) {
			// 重新查找，以某个ID开头的
			content = TemplateMessageManager.getInstance().queryFCMessageBySuffix(packageType + "-");
			if (null == content) {
				throw new IllegalArgumentException("协议号:" + packageType + "，未在接收端定义!");
			}
			CheckUtils.checkNull(content.getPrimaryKey(), "协议号：" + content.getId() + "为合并协议，不能没有主键!");
		}

		fields = content.getFields();
		endIndex = fields.length;
		int start = -1;
		for (int i = 0; i < endIndex; i++) {
			Field tmp = fields[i];
			this.analysisFieldService.read(in, fields[i], newjson, new Object[] { true, content.getId(), os });
			if (null != content.getPrimaryKey() && tmp.getName().equals(content.getPrimaryKey())) {
				int secondPrimaryKey = newjson.getIntValue(tmp.getName());
				// 重新定位模板
				content = TemplateMessageManager.getInstance().getFCMessage(packageType + "-" + secondPrimaryKey);
				CheckUtils.checkNull(content, "协议ID：" + (packageType + "-" + secondPrimaryKey) + ",未定义!");
				start = i + 1;
				break;
			}
		}

		if (start != -1) {
			analysis(start, content.getFields(), in, newjson, new Object[] { true, content.getId(), os });
		}

		// 解析foot
		Message foot = TemplateMessageManager.getInstance().getFCMessageTemplate().getFoot();
		if (null != foot) {
			fields = foot.getFields();
			endIndex = fields.length;

			for (int i = 0; i < endIndex; i++) {
				this.analysisFieldService.read(in, fields[i], newjson, new Object[] { true, content.getId(), os });
			}

		}

		// 解析完成 需要根据不同的模板往不同的处理类下发
		HandleMessageService handleService = HandleMessageManager.getInstance().getHandleFcMessageService(content.getId());

		// 需要返回处理回复
		if (HandleMessageManager.getInstance().getResponseProtocolId(content.getId())) {
			ResponseResult result = null;
			if (null == handleService) {
				// 处理具体协议类未找到 默认处理类进行输出并打印日志
				result = defaultHandleMessage(newjson);
			} else {
				result = handleService.handle(newjson);
			}
			// 处理错误
			if (result instanceof ErrorResponseResult) {
				// 暂时做错误输出
				LOG.error(result.getMessage());
				return;
			}
			// 返回值
			JSONObject json = (JSONObject) result.getData();
			respone(os, json.getString("id"), json);
		} else {
			ResponseResult result = null;

			if (null == handleService) {
				// 处理具体协议类未找到 默认处理类进行输出并打印日志
				result = defaultHandleMessage(newjson);
			} else {
				result = handleService.handle(newjson);
			}

			if (null != result && result.isReptSend()) {
				// 返回值
				JSONObject json = (JSONObject) result.getData();
				respone(os, json.getString("id"), json);
			}
		}

	}

	private void response03(OutputStream os, String id, JSONObject message) throws DtuMessageException, IOException
	{
		JSONObject config = new JSONObject();
		config.put("Package_Number", null == message.get("Package_Number") ? SystemConsts.DEFAULT_PACKAGE_NUM : message.get("Package_Number"));
		config.put("Gateway_Id", message.get("Gateway_Id"));
		config.put("Sensor_Id", message.get("Sensor_Id"));
		config.put("command_properties", 3);
		respone(os, id, config);

	}

	private void response04(OutputStream os, String id, JSONObject message) throws DtuMessageException, IOException
	{
		JSONObject config = new JSONObject();
		config.put("Package_Number", null == message.get("Package_Number") ? SystemConsts.DEFAULT_PACKAGE_NUM : message.get("Package_Number"));
		config.put("Gateway_Id", message.get("Gateway_Id"));
		config.put("Sensor_Id", message.get("Sensor_Id"));
		config.put("command_properties", 4);
		respone(os, id, config);

	}

	private void respone(OutputStream os, String id, JSONObject content) throws DtuMessageException, IOException
	{
		// 重发
		byte[] responsedata = responseMessageService.resolve(content.getString("id"), content.getJSONObject("data"));
		os.write(responsedata);
		if (SystemConsts.isDebug) {
			JSONObject data = content.getJSONObject("data");
			if (data.containsKey("Sensor_Id") && null != data.get("Sensor_Id")) {
				Integer Sensor_Id = data.getInteger("Sensor_Id");
				List<Number> list = new ArrayList<>();
				for (byte b : responsedata) {
					list.add(b);
				}
				AckLastCache.getInstance().setPackages(HexUtils.getHexString(Sensor_Id), list);
			}
		}
	}

	private void analysis(int start, Field[] fields, InputStream is, JSONObject newjson, Object... objects) throws Exception
	{
		for (int i = start; i < fields.length; i++) {
			Field tmp = fields[i];
			this.analysisFieldService.read(is, tmp, newjson, objects);
		}
	}

	// 默认处理类
	private ResponseResult defaultHandleMessage(JSONObject json)
	{
		return defaultHandleFcMessageService.handle(json);
	}
}
