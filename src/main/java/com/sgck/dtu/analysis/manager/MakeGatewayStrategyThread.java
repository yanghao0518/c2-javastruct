package com.sgck.dtu.analysis.manager;

import com.sgck.dtu.analysis.cache.PrDownSensorTableCache;
import com.sgck.dtu.analysis.cache.PrZwSensorTableCache;
import com.sgck.dtu.analysis.cache.SensorVo;

/**
 * 组网策略线程
 * @author DELL
 *
 */
public class MakeGatewayStrategyThread extends Thread
{

	private boolean isClose;
	@Override
	public void run()
	{
		//延迟启动
		try {
			Thread.sleep(4*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(!isClose){
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//从未组网缓存获取，需要组网信息，比较每个网关下的信号强度，并扔进带下发缓存中
			SensorVo vo = PrZwSensorTableCache.getInstance().voteOne();
			//判断是否已经组网成功
			
			//找到强度最大的值
			SensorVo compareAfter =  PrZwSensorTableCache.getInstance().compareRiss(vo.getSensor_Id(), vo.getRiss());
			//加入到带下发列表
			PrDownSensorTableCache.getInstance().addorupdate(compareAfter);
			//删除该传感器ID
			PrZwSensorTableCache.getInstance().remove(vo.getSensor_Id());
			
		}
	}

}
