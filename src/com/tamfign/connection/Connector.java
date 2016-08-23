package com.tamfign.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.command.Command;
import com.tamfign.command.Handler;

public abstract class Connector {
	protected abstract Handler getHandler(Socket socket);

	private HashMap<String, Socket> localSocketsList = null;
	private ConnectController controller = null;

	protected Connector(ConnectController controller) {
		this.controller = controller;
		this.localSocketsList = new HashMap<String, Socket>();
	}

	public abstract boolean requestTheOther(String cmd, Object obj);

	protected ConnectController getController() {
		return this.controller;
	}

	public void certainSocket(String id, Socket socket) {
		localSocketsList.put(id, socket);
	}

	protected Iterator<Entry<String, Socket>> getLocalSocketListIt() {
		return this.localSocketsList.entrySet().iterator();
	}

	protected void keepListenPortAndAcceptMultiClient(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		Socket socket;
		try {
			while (true) {
				socket = server.accept();
				Thread handleThread = new Thread(getHandler(socket));
				handleThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	}

	public void broadcast(String cmd) {
		Iterator<Entry<String, Socket>> it = localSocketsList.entrySet().iterator();
		while (it.hasNext()) {
			Socket socket = it.next().getValue();
			write(socket, cmd);
		}
	}

	protected void broadcast(List<Socket> listenerList, String cmd) {
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				write(socket, cmd);
			}
		}
	}

	protected boolean broadcastAndGetResult(String cmd) {
		boolean ret = false;
		Iterator<Entry<String, Socket>> it = localSocketsList.entrySet().iterator();
		while (it.hasNext()) {
			Socket socket = it.next().getValue();
			try {
				write(socket, cmd);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				JSONObject root = (JSONObject) new JSONParser().parse(br.readLine());
				ret &= Boolean.parseBoolean((String) root.get(Command.P_APPROVED));
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

	private void write(Socket socket, String cmd) {
		BufferedWriter bw = null;
		try {
			// TODO Multi-thread?
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(cmd);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}
}