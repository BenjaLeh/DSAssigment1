package com.tamfign.command;

import java.net.Socket;

import com.tamfign.connection.ConnectorInf;

public abstract class CmdHandler {
	protected ConnectorInf connector = null;

	public CmdHandler(ConnectorInf connector) {
		this.connector = connector;
	}

	protected void response(Socket socket, String cmd) {
		connector.write(socket, cmd);
	}
}
