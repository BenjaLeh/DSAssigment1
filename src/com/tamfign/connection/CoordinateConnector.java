package com.tamfign.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.command.CoordinateHandler;
import com.tamfign.command.ExternalHandler;
import com.tamfign.command.InternalHandler;
import com.tamfign.configuration.Configuration;
import com.tamfign.model.ServerConfig;
import com.tamfign.model.ServerListController;

public class CoordinateConnector extends Connector implements Runnable {
	private InternalHandler internHandler = null;

	protected CoordinateConnector(ConnectController controller) {
		super(controller);
		internHandler = new InternalHandler(this);
	}

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
	protected ExternalHandler getHandler(Socket socket) {
		return new CoordinateHandler(this, socket);
	}

	public boolean runInternalRequest(JSONObject obj) {
		return internHandler.cmdAnalysis(obj);
	}

	@Override
	public boolean requestTheOther(JSONObject obj) {
		return false;
	}
}