package com.yanhe.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import com.alibaba.fastjson.JSON;

/**
 * 业务服务器
 * 
 * @author wangbo
 *
 */
public class WorkServer {
	private ZkClient zkClient;
	private IZkDataListener dataListener;

	// 配置节点的路径
	private String configPath;

	// 存放服务器列表的节点路径
	private String serversPath;

	private ServerData serverData;
	private ServerConfig serverConfig;

	public WorkServer(String configPath, String serversPath, ServerData serverData, ZkClient zkClient,
			ServerConfig initConfig) {
		this.zkClient = zkClient;
		this.serversPath = serversPath;
		this.configPath = configPath;
		this.serverConfig = initConfig;
		this.serverData = serverData;

		this.dataListener = new IZkDataListener() {
			public void handleDataDeleted(String dataPath) throws Exception {
				//
			}

			public void handleDataChange(String dataPath, Object data) throws Exception {
				// 新的服务器配置信息下发，每个服务器需要更新
				String jsonStr = new String((byte[]) data);
				
				ServerConfig configLocal = (ServerConfig) JSON.parseObject(jsonStr, ServerConfig.class);
				updateConfig(configLocal);
				
				System.out.println("new Work server config is:" + serverConfig.toString());
			}
		};
	}

	public void start() {
		System.out.println("work server start...");
		initRunning();
	}

	public void stop() {
		System.out.println("work server stop...");
		zkClient.unsubscribeDataChanges(configPath, dataListener);
	}

	private void initRunning() {
		registMe();
		
		// 侦听数据配置节点，以便得到下发的配置数据
		zkClient.subscribeDataChanges(configPath, dataListener);
	}

	private void registMe() {
		String mePath = serversPath.concat("/").concat(serverData.getAddress());

		try {
			// 每个服务器对应着1个临时节点
			zkClient.createEphemeral(mePath, JSON.toJSONString(serverData).getBytes());
		} catch (ZkNoNodeException e) {
			// 如果服务器节点的父节点还没有创建则修复后重新注册
			// 第二个参数表示递归创建
			zkClient.createPersistent(serversPath, true);
			registMe();
		}
	}

	private void updateConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
}
