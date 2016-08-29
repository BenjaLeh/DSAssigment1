package com.tamfign.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.command.Command;
import com.tamfign.command.CoordinateHandler;
import com.tamfign.command.ExternalHandler;
import com.tamfign.command.InternalHandler;
import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.model.ServerListController;

public class CoordinateConnector extends Connector implements Runnable {
	private InternalHandler internHandler = null;
	private static HashMap<String, Socket> broadcastList = null;

	protected CoordinateConnector(ConnectController controller) {
		super(controller);
		internHandler = new InternalHandler(this);
		broadcastList = new HashMap<String, Socket>();
	}

	public void run() {
		try {
			System.out.println("Start Listening Other Servers");
			keepListenPortAndAcceptMultiClient(Configuration.getCoordinationPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean broadcastAndGetResult(String cmd) {
		boolean ret = true;
		Iterator<Entry<String, Socket>> it = broadcastList.entrySet().iterator();
		while (it.hasNext()) {
			Socket socket = it.next().getValue();
			try {
				write(socket, cmd);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				JSONObject root = (JSONObject) new JSONParser().parse(br.readLine());
				ret &= getResult(root);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public void broadcast(String cmd) {
		Iterator<Entry<String, Socket>> it = broadcastList.entrySet().iterator();
		while (it.hasNext()) {
			Socket socket = it.next().getValue();
			write(socket, cmd);
		}
	}

	private boolean getResult(JSONObject obj) {
		boolean ret = false;

		switch ((String) obj.get(Command.TYPE)) {
		case (Command.TYPE_LOCK_ID):
		case (Command.TYPE_LOCK_ROOM):
			ret = Boolean.parseBoolean((String) obj.get(Command.P_LOCKED));
			break;
		default:
			ret = Boolean.parseBoolean((String) obj.get(Command.P_APPROVED));
		}
		return ret;
	}

	public void addBroadcastList(String serverId, Socket useless) {
		ServerConfig server = ServerListController.getInstance().get(serverId);
		Socket another = null;

		try {
			another = new Socket(server.getHost(), server.getCoordinationPort());
			if (another.isConnected()) {
				broadcastList.put(serverId, another);
				ServerListController.getInstance().get(serverId).setActived(true);
			}
		} catch (Exception e) {
			System.out.println("Fail to connect server-" + serverId);
		}
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