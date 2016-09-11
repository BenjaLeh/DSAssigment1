package com.tamfign.model;

import java.util.ArrayList;

public class ChatRoom {
	private String name = null;
	private String owner = null;
	private String serverId = null;
	private ArrayList<String> members = null;

	public ChatRoom(String chatRoom, String serverId, String owner) {
		this.name = chatRoom;
		this.serverId = serverId;
		this.owner = owner;
		this.members = new ArrayList<String>();
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

	protected void addMember(String identity) {
		this.members.add(identity);
	}

	protected void addMembers(ArrayList<String> members) {
		this.members.addAll(members);
	}

	protected void removeMember(String identity) {
		this.members.remove(identity);
	}

	public ArrayList<String> getMemberList() {
		return this.members;
	}
}
