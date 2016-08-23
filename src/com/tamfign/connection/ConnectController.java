package com.tamfign.connection;

public class ConnectController {
	private ClientListener clients = null;
	private CoordinateListener servers = null;

	private ConnectController() {
		this.clients = new ClientListener(this);
		this.servers = new CoordinateListener(this);
	}

	public static ConnectController getInstance() {
		return new ConnectController();
	}

	public void run() {
		new Thread(this.servers).start();
		new Thread(this.clients).start();
	}

	public boolean requestServer(String cmd, Object obj) {
		return servers.runInternalRequest(cmd, obj);
	}

	public boolean requestClient(String cmd, Object obj) {
		return clients.runRequest(cmd, obj);
	}
}
