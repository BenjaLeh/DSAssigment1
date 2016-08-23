package com.tamfign.model;

import java.util.HashMap;

public class IndentityListController {
	private HashMap<String, String> indentityList = null;
	private static IndentityListController _instance = null;

	private IndentityListController() {
		this.indentityList = new HashMap<String, String>();
	}

	public static IndentityListController getInstance() {
		if (_instance == null) {
			_instance = new IndentityListController();
		}
		return _instance;
	}

	public void addIndentity(String serverId, String id) {
		indentityList.put(id, serverId);
	}

	public void removeIndentity(String id) {
		indentityList.remove(id);
	}

	public boolean isIndentityExist(String id) {
		return indentityList.containsKey(id);
	}
}
