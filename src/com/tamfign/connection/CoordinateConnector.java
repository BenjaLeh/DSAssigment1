package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.tamfign.command.CoordinateHandler;
import com.tamfign.command.ExternalHandler;
import com.tamfign.command.InternalHandler;
import com.tamfign.command.ServerServerCmd;
import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ServerListController;

public class CoordinateConnector extends Connector implements Runnable {
	private InternalHandler internHandler = null;
	private static HashMap<String, Socket> serverList = null;

	protected CoordinateConnector(ConnectController controller) {
		super(controller);
		internHandler = new InternalHandler(this);
		serverList = new HashMap<String, Socket>();
	}

	public void run() {
		try {
			System.out.println("Start Listening Other Servers");
			keepListenPortAndAcceptMultiClient(Configuration.getCoordinationPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean broadcastAndGetResult(String cmd) {
		return broadcastAndGetResult(new ArrayList<Socket>(serverList.values()), cmd);
	}

	public void broadcast(String cmd) {
		broadcast(new ArrayList<Socket>(serverList.values()), cmd);
	}

	public void connectServer(String serverId) {
		ServerConfig server = ServerListController.getInstance().get(serverId);
		Socket another = null;

		try {
			another = new Socket(server.getHost(), server.getCoordinationPort());
			if (another.isConnected()) {
				addBroadcastList(serverId, another);
				ServerListController.getInstance().get(serverId).setActived(true);
				ChatRoomListController.getInstance().addRoom(ChatRoomListController.getMainHall(serverId), serverId,
						null);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect server-" + serverId);
		}
	}

	public void checkOtherServers() {
		for (int i = 0; i < ServerListController.getInstance().size(); i++) {
			if (ServerListController.getInstance().get(i).isItselft()) {
				continue;
			}
			if (testConnection(ServerListController.getInstance().get(i))) {
				ServerListController.getInstance().get(i).setActived(true);
				connectServer(ServerListController.getInstance().get(i).getId());
			}
		}
	}

	private boolean testConnection(ServerConfig server) {
		boolean ret = true;

		Socket another = null;
		try {
			another = new Socket(server.getHost(), server.getCoordinationPort());
			if (!another.isConnected()) {
				ret = false;
			} else {
				sendOutOwnId(another);
			}
		} catch (Exception e) {
			ret = false;
		} finally {
			close(another);
		}
		return ret;
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;
		write(socket, ServerServerCmd.getServerOnCmd());
	}

	public void addBroadcastList(String serverId, Socket socket) {
		synchronized (serverList) {
			serverList.put(serverId, socket);
		}
	}

	public void removeBroadcastList(String id) {
		synchronized (serverList) {
			serverList.remove(id);
		}
	}

	@Override
	protected ExternalHandler getHandler(Socket socket) {
		return new CoordinateHandler(this, socket);
	}

	public boolean runInternalRequest(JSONObject obj) {
		return internHandler.cmdAnalysis(obj);
	}
}