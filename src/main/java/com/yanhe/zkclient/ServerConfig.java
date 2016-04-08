package com.yanhe.zkclient;

/**
 * 服务器的配置信息，本例假设是数据库连接信息
 * 
 * @author wangbo
 *
 */
public class ServerConfig {
	private String dbUrl;
	private String dbPwd;
	private String dbUser;

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	@Override
	public String toString() {
		return "ServerConfig [dbUrl=" + dbUrl + ", dbPwd=" + dbPwd + ", dbUser=" + dbUser + "]";
	}
}
