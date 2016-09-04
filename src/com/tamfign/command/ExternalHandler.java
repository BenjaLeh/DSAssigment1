package com.tamfign.command;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.connection.Connector;
import com.tamfign.connection.ConnectorInf;

public abstract class ExternalHandler implements Runnable {
	private Socket socket = null;
	private ConnectorInf connetor = null;

	public ExternalHandler(Connector connetor, Socket socket) {
		this.connetor = connetor;
		this.socket = socket;
	}

	@Override
	public void run() {
		String cmd;
		try {
			while (socket.isConnected()) {
				cmd = getConnector().readCmd(socket);
				if (cmd != null && !"".equals(cmd)) {
					cmdAnalyse(cmd);
				}
			}
		} catch (SocketException e) {
			handleDisconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getConnector().close(socket);
		}
	}

	protected abstract void handleDisconnect();

	protected ConnectorInf getConnector() {
		return this.connetor;
	}

	protected Socket getSocket() {
		return this.socket;
	}

	protected void terminate(String id) {
		getConnector().close(socket);
		connetor.removeBroadcastList(id);
	}

	protected void response(String cmd) {
		getConnector().write(this.socket, cmd);
	}

	private void cmdAnalyse(String cmd) {
		try {
			System.out.println(cmd);
			cmdAnalysis((JSONObject) new JSONParser().parse(cmd));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	protected abstract void cmdAnalysis(JSONObject obj);
}
