package com.tamfign.connection;

import org.json.simple.JSONObject;

public class ConnectController {
	private ClientConnector clients = null;
	private CoordinateConnector servers = null;

	private ConnectController() {
		this.clients = new ClientConnector(this);
		this.servers = new CoordinateConnector(this);
	}

	public static ConnectController getInstance() {
		return new ConnectController();
	}

	public void run() {
		new Thread(this.servers).start();
		new Thread(this.clients).start();
		servers.checkOtherServers();
	}

	public boolean requestServer(JSONObject obj) {
		return servers.runInternalRequest(obj);
	}
}
