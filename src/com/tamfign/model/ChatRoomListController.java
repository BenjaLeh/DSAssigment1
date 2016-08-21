package com.tamfign.model;

import java.util.HashMap;

public class ChatRoomListController {
	private HashMap<String, ChatRoom> roomList = null;
	private static ChatRoomListController _instance = null;

	private ChatRoomListController() {
		this.roomList = new HashMap<String, ChatRoom>();
	}

	public static ChatRoomListController getInstance() {
		if (_instance == null) {
			_instance = new ChatRoomListController();
		}
		return _instance;
	}

	public void addRoom(String roomId, String serverId, String owner) throws ModelListException {
		if (roomList.containsKey(roomId)) {
			throw new ModelListException("Room Exist.");
		}
		roomList.put(roomId, new ChatRoom(roomId, serverId, owner));
	}

	public void removeRoom(String roomId) {
		roomList.remove(roomId);
	}

	public boolean isRoomExists(String roomId) {
		return roomList.containsKey(roomId);
	}
}