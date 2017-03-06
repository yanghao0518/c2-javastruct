package com.sgck.dtu.analysis.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sgck.dtu.analysis.cache.WaveCache;
import com.sgck.dtu.analysis.cache.WaveReadConfig;
import com.sgck.dtu.analysis.common.ErrorResponseResult;
import com.sgck.dtu.analysis.common.Field;
import com.sgck.dtu.analysis.common.LEDataInputStream;
import com.sgck.dtu.analysis.common.LEDataOutputStream;
import com.sgck.dtu.analysis.common.Message;
import com.sgck.dtu.analysis.common.ResponseResult;
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.exception.DtuMessageException;
import com.sgck.dtu.analysis.manager.HandleMessageManager;
import com.sgck.dtu.analysis.manager.TemplateMessageManager;
import com.sgck.dtu.analysis.utiils.CheckUtils;
import com.sgck.dtu.analysis.writer.ResponseMessageService;
import com.sgck.dtu.analysis.writer.ResponseMessageServiceImpl;
import com.sgck.dtu.analysis.writer.SendMessageNewService;
import com.sgck.dtu.analysis.writer.SendMessageNewServiceImpl;

public class ReadMessageServerImpl implements ReadMessageServer
{

	private ReadFieldService analysisFieldService = new ReadFieldServiceImpl();

	private HandleMessageService defaultHandleFcMessageService = new DefaultHandleMessageServiceImpl();

	private ResponseMessageService responseMessageService = new ResponseMessageServiceImpl();

	private ReadWaveMessageService readWaveMessageService = new ReadWaveMessageServiceImpl();

	private SendMessageNewService sendMessageNewService = new SendMessageNewServiceImpl();

	public void setReadWaveMessageService(ReadWaveMessageService readWaveMessageService)
	{
		this.readWaveMessageService = readWaveMessageService;
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
	public void read(Socket socket) throws IOException, DtuMessageException
	{
		analysis(socket);
	}

	public void analysis(Socket socket) throws IOException, DtuMessageException
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

		// InputStream is = socket.getInputStream();
		LEDataInputStream is = new LEDataInputStream(socket.getInputStream());
		// 接收前缀包
		for (int i = startIndex; i < endIndex; i++) {
			// if (i == startIndex) {
			// readFirst(is, fields[i], newjson);
			// } else {
			read(is, fields[i], newjson, new Object[] { false });
			// }
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
			read(is, tmp, newjson, new Object[] { true, content.getId(), socket.getOutputStream() });
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
			analysis(start, content.getFields(), is, newjson, new Object[] { true, content.getId(), socket.getOutputStream() });
		}

		// 解析foot
		Message foot = TemplateMessageManager.getInstance().getFCMessageTemplate().getFoot();
		if (null != foot) {
			fields = foot.getFields();
			endIndex = fields.length;

			for (int i = 0; i < endIndex; i++) {
				read(is, fields[i], newjson, new Object[] { true, content.getId(), socket.getOutputStream() });
			}

		}

		//is.close();
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
				System.out.println(result.getMessage());
				return;
			}
			// result
			JSONObject json = (JSONObject) result.getData();
			LEDataOutputStream dos = new LEDataOutputStream(socket.getOutputStream());
			List<Byte> list = sendMessageNewService.send(dos, json.getString("id"), json.getJSONObject("data"));
			// byte[] responsedata =
			// responseMessageService.resolve(json.getString("id"),
			// json.getJSONObject("data"));
			if (SystemConsts.isDebug) {
				json.getJSONObject("data").put(SystemConsts.DATAPACKAGESIGN, list);
			}
			// socket.getOutputStream().write(responsedata);
		} else {
			ResponseResult result = null;

			if (null == handleService) {
				// 处理具体协议类未找到 默认处理类进行输出并打印日志
				result = defaultHandleMessage(newjson);
			} else {
				result = handleService.handle(newjson);
			}

			if (null != result && result.isReptSend()) {
				// 重发
				JSONObject json = (JSONObject) result.getData();
				LEDataOutputStream dos = new LEDataOutputStream(socket.getOutputStream());
				List<Byte> list = sendMessageNewService.send(dos, json.getString("id"), json.getJSONObject("data"));
				// byte[] responsedata =
				// responseMessageService.resolve(json.getString("id"),
				// json.getJSONObject("data"));
				if (SystemConsts.isDebug) {
					json.getJSONObject("data").put(SystemConsts.DATAPACKAGESIGN, list);
				}
				// socket.getOutputStream().write(responsedata);
			}
		}

	}

	private void analysis(int start, Field[] fields, LEDataInputStream is, JSONObject newjson, Object... objects) throws IOException
	{
		for (int i = start; i < fields.length; i++) {
			Field tmp = fields[i];
			read(is, tmp, newjson, objects);
		}
	}

	// 默认处理类
	private ResponseResult defaultHandleMessage(JSONObject json)
	{
		return defaultHandleFcMessageService.handle(json);
	}

	// 读第一个时判断read值是否为-1，如果为-1则表示客户下线需要做下线处理
	private void readFirst(InputStream is, Field field, JSONObject newjson) throws IOException, DtuMessageException
	{
		byte[] recvHead = new byte[field.getType().BYTES];
		int rlRead = is.read(recvHead, 0, field.getType().BYTES);
		if (-1 == rlRead) {
			// 表示已经下线
			throw new DtuMessageException(DtuMessageException.CLIENT_NOT_ONLINE, "this client tcp is not online~");
		}
		if (SystemConsts.isDebug) {
			List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
			for (int i = 0; i < recvHead.length; i++) {
				originalpackage.add(recvHead[i]);
			}
		}

		newjson.put(field.getName(), analysisFieldService.read(recvHead, field));

	}

	// 此方法需要抽象出去，影响读取方式
	private void read(LEDataInputStream is, Field field, final JSONObject newjson, final Object... params) throws IOException
	{

		switch (field.getType())
		{
		case BOOLEAN:
			newjson.put(field.getName(), is.readBoolean());
			break;
		case BYTE:
			newjson.put(field.getName(), is.readByte());
			break;
		case CHAR:
			newjson.put(field.getName(), is.readCharC());
			break;
		case SHORT:
			newjson.put(field.getName(), is.readShort());
			break;
		case USHORT:
			newjson.put(field.getName(), is.readUnsignedShort());
			break;
		case INT:
			newjson.put(field.getName(), is.readInt());
			break;
		case UINT:
			newjson.put(field.getName(), is.readUnsignedInt());
			break;
		case FLOAT:
			newjson.put(field.getName(), is.readFloat());
			break;
		case DOUBLE:
			newjson.put(field.getName(), is.readDouble());
			break;
		case STRING:
			newjson.put(field.getName(), is.readUTF());
			break;
		case USHORTARRAY:
			//注意此次可能是波形，暂时写死判断在此处
			int currentPackageNum = newjson.getIntValue("Package_ Number");
			int defaultArrayLength = WaveReadConfig.transferNumber;
			WaveReadConfig config = WaveCache.getInstance().getCurrentWaveReadConfig(currentPackageNum - 1);
			
			if(null != config){
                //剩余量小于1024个点
				if(config.getResidue()>0 && config.getResidue() < WaveReadConfig.transferNumber) {
					defaultArrayLength = config.getResidue();
				}
			}
			
			List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
			int[] datas = new int[defaultArrayLength];
			is.readUShortArray(datas, originalpackage);
			newjson.put(field.getName(), datas);
			return;
		default:
			throw new DtuMessageException(DtuMessageException.UNKNOWN_EXCEPTION, "not undefined jtype:" + field.getType().JTYPE);

		}
		// if (SystemConsts.isDebug) {
		List<Byte> originalpackage = (List<Byte>) newjson.get(SystemConsts.DATAPACKAGESIGN);
		is.readOrgList(originalpackage);
		// }

		Map<String, CheckField> checkmapping = TemplateMessageManager.getInstance().getFCMessageTemplate().getCheckFields();
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
							System.out.println("校验 BCC 失败!");
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
						//
						LEDataOutputStream dos = new LEDataOutputStream(os);
						try {
							sendMessageNewService.send(dos, "1", json);
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if(null != dos){
								try {
									dos.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}

				}, new Object[] { originalpackage, newjson.get("BCC") });
			}
		}
	}

	// 此方法需要抽象出去，影响读取方式
	private void readDebug(InputStream is, Field field, JSONObject newjson) throws IOException
	{
		analysisFieldService.read(is, field, newjson);
	}

	private void readBak(InputStream is, Field field, JSONObject newjson) throws IOException
	{
		if (SystemConsts.isDebug) {
			readDebug(is, field, newjson);
		} else {
			newjson.put(field.getName(), analysisFieldService.read(is, field));
		}
	}

}
