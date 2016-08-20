package com.tamfign.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Connector {
	protected abstract Handler getHandler(Socket socket);

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