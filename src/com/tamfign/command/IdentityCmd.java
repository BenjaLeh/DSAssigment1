package com.tamfign.command;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class IdentityCmd extends Command {
	public String newIdentityRq(String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_NEW_ID);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public String newIdentityRs(String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_NEW_ID);
		root.put(P_APPROVED, result);
		return root.toJSONString();
	}

	public String lockIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public String lockIdentityRs(String serverId, String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		root.put(P_LOCKED, result);
		return root.toJSONString();
	}

	public String releaseIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}
}
