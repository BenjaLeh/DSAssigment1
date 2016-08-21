package com.tamfign.model;

public class ChatRoom {
	private String name = null;
	private String owner = null;
	private String serverId = null;

	public ChatRoom(String chatRoom, String serverId, String owner) {
		this.name = chatRoom;
		this.serverId = serverId;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public String getServerId() {
		return serverId;
	}

	public String getOwner() {
		return owner;
	}
}
