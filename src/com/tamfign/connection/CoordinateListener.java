package com.tamfign.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.configuration.ServerListController;

public class CoordinateListener extends Connector implements Runnable {

	public void run() {
		checkOtherServers();
		try {
			keepListenPortAndAcceptMultiClient(Configuration.getCoordinationPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkOtherServers() {
		for (int i = 0; i < ServerListController.getInstance().size(); i++) {
			if (ServerListController.getInstance().get(i).isItselft()) {
				continue;
			}
			if (testConnection(ServerListController.getInstance().get(i))) {
				ServerListController.getInstance().get(i).setActived(true);
			}
		}
	}

	private boolean testConnection(ServerConfig server) {
		boolean ret = false;

		Socket client = null;
		try {
			client = new Socket(server.getHost(), server.getCoordinationPort());
			if (!client.isConnected()) {
				ret = false;
			} else {
				sendOutOwnId(client);
			}
		} catch (Exception e) {
			ret = false;
		} finally {
			close(client);
		}
		return ret;
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;
		PrintWriter os = new PrintWriter(socket.getOutputStream());
		os.println(Configuration.getServerId());// TODO format msg
		os.flush();
		os.close();
	}

	@Override
	protected Handler getHandler(Socket socket) {
		return new CoordinateHandler(socket);
	}
}