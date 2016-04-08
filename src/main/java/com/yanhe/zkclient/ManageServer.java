package com.yanhe.zkclient;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import com.alibaba.fastjson.JSON;

/**
 * 控制服务器
 * @author wangbo
 *
 */
public class ManageServer {
	// 服务器节点的路径
	private String serversPath;
	
	// 命令的存储路径
	private String commandPath;
	
	// 最新的服务器配置的节点路径
	private String configPath;
	
	private ZkClient zkClient;
	private ServerConfig serverConfig;
	
	private IZkChildListener childListener;
	private IZkDataListener dataListener;
	
	// 当前有效的服务器集合
	private List<String> workServerList;

	public ManageServer(String serversPath, String commandPath, String configPath, ZkClient zkClient,
			ServerConfig serverConfig) {
		this.serversPath = serversPath;
		this.commandPath = commandPath;
		this.zkClient = zkClient;
		this.serverConfig = serverConfig;
		this.configPath = configPath;
		
		this.childListener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				workServerList = currentChilds;

				System.out.println("work server list changed, new list is:");
				execList();
			}
		};
		
		this.dataListener = new IZkDataListener() {
			public void handleDataDeleted(String dataPath) throws Exception {
				// ignore
			}

			public void handleDataChange(String dataPath, Object data) throws Exception {
				String cmd = new String((byte[]) data);
				System.out.println("cmd:" + cmd);
				
				exeCmd(cmd);
			}
		};
	}

	private void initRunning() {
		zkClient.subscribeDataChanges(commandPath, dataListener);
		zkClient.subscribeChildChanges(serversPath, childListener);
	}

	private void exeCmd(String cmdType) {
		if ("list".equals(cmdType)) {
			execList();
		} else if ("create".equals(cmdType)) {
			execCreate();
		} else if ("modify".equals(cmdType)) {
			execModify();
		} else {
			System.out.println("error command!" + cmdType);
		}
	}

	private void execList() {
		System.out.println(workServerList.toString());
	}

	private void execCreate() {
		if (!zkClient.exists(configPath)) {
			try {
				zkClient.createPersistent(configPath, JSON.toJSONString(serverConfig).getBytes());
			} catch (ZkNodeExistsException e) {
				zkClient.writeData(configPath, JSON.toJSONString(serverConfig).getBytes());
			} catch (ZkNoNodeException e) {
				String parentDir = configPath.substring(0, configPath.lastIndexOf('/'));
				zkClient.createPersistent(parentDir, true);
				execCreate();
			}
		}
	}

	private void execModify() {
		serverConfig.setDbUser(serverConfig.getDbUser() + "_modify");

		try {
			zkClient.writeData(configPath, JSON.toJSONString(serverConfig).getBytes());
		} catch (ZkNoNodeException e) {
			execCreate();
		}
	}

	public void start() {
		initRunning();
	}

	public void stop() {
		zkClient.unsubscribeChildChanges(serversPath, childListener);
		zkClient.unsubscribeDataChanges(commandPath, dataListener);
	}
}