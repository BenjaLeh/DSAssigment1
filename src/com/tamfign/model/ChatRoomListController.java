package com.tamfign.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.tamfign.configuration.Configuration;

public class ChatRoomListController {
	private HashMap<String, ChatRoom> roomList = null;
	private static ChatRoomListController _instance = null;
	private static final String MAIN_HALL = "MainHall-";

	private ChatRoomListController() {
		this.roomList = new HashMap<String, ChatRoom>();
		addRoom(getLocalMainHall(), Configuration.getServerId(), null);
	}

	public static ChatRoomListController getInstance() {
		if (_instance == null) {
			_instance = new ChatRoomListController();
		}
		return _instance;
	}

	public void addRoom(String roomId, String serverId, String owner) {
		synchronized (this) {
			ChatRoom newRoom = new ChatRoom(roomId, serverId, owner);
			newRoom.addMember(owner);
			roomList.put(roomId, newRoom);
		}
	}

	public void changeRoom(String former, String newRoom, String identity) {
		synchronized (this) {
			roomList.get(former).removeMember(identity);
			roomList.get(newRoom).addMember(identity);
		}
	}

	public void deleteRoom(String roomId) {
		synchronized (this) {
			roomList.get(getLocalMainHall()).addMembers(roomList.get(roomId).getMemberList());
			roomList.remove(roomId);
		}
	}

	public void removeRoom(String roomId) {
		synchronized (this) {
			roomList.remove(roomId);
		}
	}

	public boolean isRoomExists(String roomId) {
		synchronized (this) {
			return roomList.containsKey(roomId);
		}
	}

	public ArrayList<String> getList() {
		synchronized (this) {
			ArrayList<String> ret = new ArrayList<String>();
			ret.addAll(roomList.keySet());
			return ret;
		}
	}

	public ChatRoom getChatRoom(String roomId) {
		synchronized (this) {
			return roomList.get(roomId);
		}
	}

	public void addMember(String roomId, String identity) {
		synchronized (this) {
			if (roomList.get(roomId) != null) {
				roomList.get(roomId).addMember(identity);
			}
		}
	}

	public void removeMember(String roomId, String identity) {
		synchronized (this) {
			if (roomList.get(roomId) != null) {
				roomList.get(roomId).removeMember(identity);
			}
		}
	}

	public ArrayList<String> getMemberList(String roomId) {
		synchronized (this) {
			return roomList.get(roomId).getMemberList();
		}
	}

	public boolean isOtherServer(String roomId) {
		boolean ret = false;

		if (roomList.get(roomId) != null && !Configuration.getServerId().equals(roomList.get(roomId).getServerId())) {
			ret = true;
		}
		return ret;
	}

	public static String getLocalMainHall() {
		return MAIN_HALL + Configuration.getServerId();
	}

	public static String getMainHall(String serverId) {
		return MAIN_HALL + serverId;
	}
}