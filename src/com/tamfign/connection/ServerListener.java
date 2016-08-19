package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;

import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.Server;

public class ServerListener {
	private Configuration config = null;

	public ServerListener(Configuration config) {
		this.config = config;
	}

	public void run() {
		checkOtherServers();
	}

	private void checkOtherServers() {
		for (int i = 0; i < config.getServerList().size(); i++) {
			if (config.getServerList().get(i).isItselft()) {
				continue;
			}
			if (testConnection(config.getServerList().get(i))) {
				config.getServerList().get(i).setActived(true);
			}
		}
	}

	private boolean testConnection(Server server) {
		boolean ret = false;

		Socket client = null;
		try {
			client = new Socket(server.getHost(), server.getCoordinationPort());
			if (!client.isConnected()) {
				ret = false;
			}
		} catch (Exception e) {
			ret = false;
		} finally {
			close(client);
		}
		return ret;
	}

	// TODO
	private void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}
}