package com.yanhe.zkclient;

/**
 * 描述服务器的信息数据，序列化后存储在节点对应的数据内容中
 * @author wangbo
 *
 */
public class ServerData {
	// 地址
	private String address;
	// 编号
	private Integer id;
	// 名称
	private String name;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ServerData [address=" + address + ", id=" + id + ", name=" + name + "]";
	}
}
