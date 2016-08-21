package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;

import com.tamfign.configuration.Configuration;
import com.tamfign.model.ServerListController;

public class ClientListener extends Connector implements Runnable {
	public void run() {
		while (true) {
			if (!ServerListController.getInstance().isAllServerOn()) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			} else {
				try {
					keepListenPortAndAcceptMultiClient(Configuration.getClientPort());
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	@Override
	protected Handler getHandler(Socket socket) {
		return new ClientHandler(socket);
	}
}
