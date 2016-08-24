package com.tamfign.model;

public class Client {
	private String identity = null;
	private String serverId = null;
	private String roomId = null;
	private String ownRoom = null;

	public Client(String identity, String serverId, String roomId) {
		this.identity = identity;
		this.serverId = serverId;
		this.roomId = roomId;
	}

	public String getOwnRoom() {
		return ownRoom;
	}

	public void setOwnRoom(String ownRoom) {
		this.ownRoom = ownRoom;
	}

	public String getIdentity() {
		return identity;
	}

	public String getServerId() {
		return serverId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomId() {
		return roomId;
	}
}
