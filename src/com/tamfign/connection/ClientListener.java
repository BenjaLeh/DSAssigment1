package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.tamfign.command.ClientHandler;
import com.tamfign.command.Handler;
import com.tamfign.configuration.Configuration;
import com.tamfign.model.ChatRoomListController;
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

	// TODO Finetune
	public void broadcastWithinRoom(String former, String roomId, String cmd) {
		ArrayList<Socket> list = new ArrayList<Socket>();
		ArrayList<String> memberList = new ArrayList<String>();

		if (former != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(former));
		}
		if (roomId != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(roomId));
		}

		Iterator<Entry<String, Socket>> it = getLocalSocketListIt();
		while (it.hasNext()) {
			Entry<String, Socket> entry = it.next();
			for (String identity : memberList) {
				if (identity != null && identity.equals(entry.getKey())) {
					list.add(entry.getValue());
				}
			}
		}
		broadcast(list, cmd);
	}
}
