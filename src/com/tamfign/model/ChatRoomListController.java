package com.tamfign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tamfign.configuration.Configuration;

public class ChatRoomListController {
	private HashMap<String, ChatRoom> roomList = null;
	private static ChatRoomListController _instance = null;
	private static final String MAIN_HALL = "MainHall-";

	private ChatRoomListController() {
		this.roomList = new HashMap<String, ChatRoom>();
		addRoom(getMainHall(), Configuration.getServerId(), null);
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
			roomList.get(getMainHall()).addMembers(roomList.get(roomId).getMemberList());
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

	public List<String> getMemberList(String roomId) {
		synchronized (this) {
			return roomList.get(roomId).getMemberList();
		}
	}

	public static String getMainHall() {
		return MAIN_HALL + Configuration.getServerId();
	}
}