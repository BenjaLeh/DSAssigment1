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
	}

	public static ChatRoomListController getInstance() {
		if (_instance == null) {
			_instance = new ChatRoomListController();
		}
		return _instance;
	}

	public void addRoom(String roomId, String serverId, String owner) {
		roomList.put(roomId, new ChatRoom(roomId, serverId, owner));
	}

	public void removeRoom(String roomId) {
		roomList.remove(roomId);
	}

	public boolean isRoomExists(String roomId) {
		return roomList.containsKey(roomId);
	}

	public ArrayList<String> getList() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(roomList.keySet());
		return ret;
	}

	public String getMainHall() {
		return MAIN_HALL + Configuration.getServerId();
	}
}