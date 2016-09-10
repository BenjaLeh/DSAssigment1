package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.connection.CoordinateConnector;

public class CoordinateListener extends CommandListener {
	private String serverId = null;

	public CoordinateListener(CoordinateConnector connetor, Socket socket) {
		super(connetor, socket);
	}

	@Override
	protected void handleDisconnect() {
		// Do nothing, as assuming that servers won't crash.
	}

	private void catchServerId(JSONObject cmdObject) {
		if (Command.TYPE_SERVER_ON.equals((String) cmdObject.get(Command.TYPE))) {
			this.serverId = (String) cmdObject.get(Command.P_SERVER_ID);
		}
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println("Server: "+cmdLine);
		JSONObject cmdObject = getCmdObject(cmdLine);
		catchServerId(cmdObject);
		((CoordinateConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObject, serverId));
	}
}
