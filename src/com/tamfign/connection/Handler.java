package com.tamfign.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public abstract class Handler implements Runnable {
	private Socket socket = null;

	public Handler(Socket socket) {
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

	protected void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	protected abstract void cmdAnalyse(String cmd);
}
