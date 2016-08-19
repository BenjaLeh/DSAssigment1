package com.tamfign.main;

import org.kohsuke.args4j.Option;

public class ServerArguments {
	@Option(name = "-n", usage = "the name of the server")
	private String serverId = null;

	@Option(name = "-l", usage = "the path to the configuration of servers")
	private String serverConfigPath = null;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerConfigPath() {
		return serverConfigPath;
	}

	public void setServerConfigPath(String serverConfigPath) {
		this.serverConfigPath = serverConfigPath;
	}
}
