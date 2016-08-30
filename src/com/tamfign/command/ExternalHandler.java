package com.tamfign.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.connection.Connector;

public abstract class ExternalHandler implements Runnable {
	private Socket socket = null;
	private Connector connetor = null;

	public ExternalHandler(Connector connetor, Socket socket) {
		this.connetor = connetor;
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		String cmd;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (socket.isConnected()) {
				cmd = br.readLine();
				if (cmd != null && !"".equals(cmd)) {
					cmdAnalyse(cmd);
				}
			}
		} catch (SocketException e) {
			// That's normal is socket is closed
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	protected Connector getConnector() {
		return this.connetor;
	}

	// TODO refractory
	protected void certainClientSocket(String id) {
		connetor.addBroadcastList(id, socket);
	}

	protected void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	protected void terminate(String id) {
		close();
		connetor.removeBroadcastList(id);
	}

	protected void response(String cmd) {
		try {
			System.out.println("Response :" + cmd);
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(cmd);
			os.flush();// TODO refractory
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cmdAnalyse(String cmd) {
		try {
			System.out.println(cmd);
			cmdAnalysis((JSONObject) new JSONParser().parse(cmd));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
	}

	protected abstract void cmdAnalysis(JSONObject obj);
}
