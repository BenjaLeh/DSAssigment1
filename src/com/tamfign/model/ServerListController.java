package com.tamfign.model;

import java.util.ArrayList;

public class ServerListController {
	private ArrayList<ServerConfig> serverList = null;
	private static ServerListController _instance = null;
	private boolean isAllServerActivated = false;

	private ServerListController() {
		this.serverList = new ArrayList<ServerConfig>();
	}

	public static ServerListController getInstance() {
		if (_instance == null) {
			_instance = new ServerListController();
		}
		return _instance;
	}

	public void addServer(ServerConfig server) {
		serverList.add(server);
	}

	public int size() {
		return serverList.size();
	}

	public ServerConfig get(int index) {
		return serverList.get(index);
	}

	public ServerConfig get(String serverId) {
		ServerConfig ret = null;
		for (ServerConfig config : serverList) {
			if (serverId != null && serverId.equals(config.getId())) {
				ret = config;
				break;
			}
		}
		return ret;
	}

	public boolean isAllServerOn() {
		synchronized (this) {
			if (!isAllServerActivated) {
				for (ServerConfig server : serverList) {
					if (!server.isItselft() && !server.isActived()) {
						return false;
					}
				}
				isAllServerActivated = true;
			}
			return isAllServerActivated;
		}
	}
}
