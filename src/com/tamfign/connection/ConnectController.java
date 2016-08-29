package com.tamfign.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.command.Command;
import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ServerListController;

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
		checkOtherServers();
	}

	public boolean requestServer(JSONObject obj) {
		return servers.runInternalRequest(obj);
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

		Socket another = null;
		try {
			another = new Socket(server.getHost(), server.getCoordinationPort());
			if (!another.isConnected()) {
				ret = false;
			} else {
				sendOutOwnId(another);
				servers.addBroadcastList(server.getId(), null);
				ChatRoomListController.getInstance().addRoom(ChatRoomListController.getMainHall(server.getId()),
						server.getId(), null);
			}
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;
		PrintWriter os = new PrintWriter(socket.getOutputStream());
		os.println(getServerOnCmd());
		os.flush();
		os.close();
	}

	@SuppressWarnings("unchecked")
	private String getServerOnCmd() {
		JSONObject obj = new JSONObject();
		obj.put(Command.TYPE, Command.TYPE_SERVER_ON);
		obj.put(Command.P_SERVER_ID, Configuration.getServerId());
		return obj.toJSONString();
	}
}
