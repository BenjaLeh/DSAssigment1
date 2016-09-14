package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.tamfign.command.ClientCmdHandler;
import com.tamfign.command.Command;
import com.tamfign.configuration.Configuration;
import com.tamfign.listener.ClientListener;
import com.tamfign.listener.CommandListener;
import com.tamfign.messagequeue.MessageQueue;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ServerListController;

public class ClientConnector extends Connector implements Runnable {
	private HashMap<String, Socket> clientSocketsList = null;
	private MessageQueue clientMQ = new MessageQueue(new ClientCmdHandler(this));

	protected ClientConnector(ConnectController controller) {
		super(controller);
		this.clientSocketsList = new HashMap<String, Socket>();
	}

	public void run() {
		new Thread(clientMQ).start();

		while (true) {
			if (!ServerListController.getInstance().isAllServerOn()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			} else {
				System.out.println("All Servers On");
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
	protected CommandListener getListener(Socket socket) {
		return new ClientListener(this, socket);
	}

	public void requestTheOther(Command command) {
		getController().requestServer(command);
	}

	public void broadcast(String cmd) {
		broadcast(new ArrayList<Socket>(clientSocketsList.values()), cmd);
	}

	public boolean broadcastAndGetResult(String cmd) {
		return broadcastAndGetResult(new ArrayList<Socket>(clientSocketsList.values()), cmd);
	}

	public void broadcastWithinRoom(String former, String roomId, String cmd) {
		ArrayList<Socket> list = new ArrayList<Socket>();
		ArrayList<String> memberList = new ArrayList<String>();

		if (former != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(former));
		}
		if (roomId != null) {
			memberList.addAll(ChatRoomListController.getInstance().getMemberList(roomId));
		}

		Iterator<Entry<String, Socket>> it = clientSocketsList.entrySet().iterator();
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

	public void addBroadcastList(String id, Socket socket) {
		clientSocketsList.put(id, socket);
	}

	public void removeBroadcastList(String id) {
		clientSocketsList.remove(id);
	}

	public MessageQueue getMQ() {
		return this.clientMQ;
	}

	public void runInternalRequest(Command cmd) {
		this.clientMQ.addCmd(cmd);
	}
}
