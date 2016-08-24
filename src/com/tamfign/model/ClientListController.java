package com.tamfign.model;

import java.util.HashMap;

public class ClientListController {
	private HashMap<String, Client> clientList = null;
	private static ClientListController _instance = null;

	private ClientListController() {
		this.clientList = new HashMap<String, Client>();
	}

	public static ClientListController getInstance() {
		if (_instance == null) {
			_instance = new ClientListController();
		}
		return _instance;
	}

	public void addIndentity(String id, String serverId, String roomId) {
		synchronized (this) {
			clientList.put(id, new Client(id, serverId, roomId));
		}
	}

	public void removeIndentity(String id) {
		synchronized (this) {
			clientList.remove(id);
		}
	}

	public boolean isIdentityExist(String id) {
		synchronized (this) {
			return clientList.containsKey(id);
		}
	}

	public Client getClient(String id) {
		synchronized (this) {
			return clientList.get(id);
		}
	}

	public void releaseId(String serverId, String id) {
		synchronized (this) {
			// Release only if it's the same as the lock one
			if (serverId != null && serverId.equals(clientList.get(id).getServerId())) {
				clientList.remove(id);
			}
		}
	}
}
