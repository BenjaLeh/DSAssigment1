package com.tamfign.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.tamfign.command.Command;
import com.tamfign.listener.CommandListener;

public abstract class Connector implements ConnectorInf {

	private ConnectController controller = null;

	protected Connector(ConnectController controller) {
		this.controller = controller;
	}

	protected ConnectController getController() {
		return this.controller;
	}

	protected abstract CommandListener getListener(Socket socket);

	protected void keepListenPortAndAcceptMultiClient(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		Socket socket;
		try {
			while (true) {
				socket = server.accept();
				Thread handleThread = new Thread(getListener(socket));
				handleThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	}

	protected boolean broadcastAndGetResult(List<Socket> listenerList, String cmd) {
		boolean ret = true;
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				try {
					write(socket, cmd);
					ret &= readResult(socket);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	protected void broadcast(List<Socket> listenerList, String cmd) {
		if (listenerList != null) {
			for (Socket socket : listenerList) {
				write(socket, cmd);
			}
		}
	}

	private boolean readResult(Socket socket) throws ParseException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return Command.getResult(Command.getCmdObject((br.readLine())));
	}

	public String readCmd(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return br.readLine();
	}

	public void write(Socket socket, String cmd) {
		try {
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			os.println(cmd);
			os.flush();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}