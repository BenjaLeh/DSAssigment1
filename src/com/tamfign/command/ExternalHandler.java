package com.tamfign.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
				cmdAnalyse(cmd);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
	}

	protected Connector getConnector() {
		return this.connetor;
	}

	protected void certainSocket(String id) {
		connetor.certainSocket(id, socket);
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
		connetor.terminateSocket(id);
	}

	protected void response(String cmd) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cmdAnalyse(String cmd) {
		try {
			cmdAnalysis((JSONObject) new JSONParser().parse(cmd));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
	}

	protected abstract void cmdAnalysis(JSONObject obj);
}
