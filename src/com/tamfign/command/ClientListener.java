package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.connection.ClientConnector;

public class ClientListener extends CommandListener {
	private String clientId = null;

	public ClientListener(ClientConnector connector, Socket socket) {
		super(connector, socket);
	}

	@Override
	protected void handleDisconnect() {
		// In case of quit again.
		if (clientId != null) {
			handleQuit();
		}
	}

	private void handleQuit() {
		// TODO Auto-generated method stub
	}

	private void catchClientId(JSONObject cmd) {
		String cmdType = (String) cmd.get(Command.TYPE);
		if (Command.TYPE_NEW_ID.equals(cmdType) || Command.TYPE_MOVE_JOIN.equals(cmdType)) {
			this.clientId = (String) cmd.get(Command.P_IDENTITY);
		}
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println(cmdLine);
		JSONObject cmdObject = getCmdObject(cmdLine);
		catchClientId(cmdObject);
		((ClientConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObject, clientId));
	}
}
