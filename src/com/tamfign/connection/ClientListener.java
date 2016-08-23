package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import com.tamfign.command.ClientHandler;
import com.tamfign.command.Handler;
import com.tamfign.configuration.Configuration;
import com.tamfign.model.ServerListController;

public class ClientListener extends Connector implements Runnable {

	protected ClientListener(ConnectController controller) {
		super(controller);
	}

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
		return new ClientHandler(this, socket);
	}

	@Override
	public boolean requestTheOther(String cmd, Object obj) {
		return getController().requestServer(cmd, obj);
	}

	public boolean runRequest(String cmd, Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
}
