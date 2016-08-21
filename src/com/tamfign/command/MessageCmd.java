package com.tamfign.command;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class MessageCmd extends Command {
	public String messageCmd(String id, String content) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MESSAGE);
		root.put(P_IDENTITY, id);
		root.put(P_CONTENT, content);
		return root.toJSONString();
	}

	public String quitCmd() {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_QUIT);
		return root.toJSONString();
	}
}
