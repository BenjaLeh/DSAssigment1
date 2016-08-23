package com.tamfign.model;

import java.util.HashMap;

public class IdentityListController {
	private HashMap<String, String> identityList = null;
	private static IdentityListController _instance = null;

	private IdentityListController() {
		this.identityList = new HashMap<String, String>();
	}

	public static IdentityListController getInstance() {
		if (_instance == null) {
			_instance = new IdentityListController();
		}
		return _instance;
	}

	public void addIndentity(String serverId, String id) {
		synchronized (this) {
			identityList.put(id, serverId);
		}
	}

	public void removeIndentity(String id) {
		synchronized (this) {
			identityList.remove(id);
		}
	}

	public boolean isIdentityExist(String id) {
		synchronized (this) {
			return identityList.containsKey(id);
		}
	}

	public void releaseId(String serverId, String id) {
		synchronized (this) {
			// Release only if it's the same as the lock one
			if (serverId != null && serverId.equals(identityList.get(id))) {
				identityList.remove(id);
			}
		}
	}
}
