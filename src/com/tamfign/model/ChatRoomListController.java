package com.tamfign.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
			roomList.put(roomId, new ChatRoom(roomId, serverId, owner));
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

	public List<String> getMemberList(String roomId) {
		synchronized (this) {
			return roomList.get(roomId).getMemberList();
		}
	}

	public String getMainHall() {
		return MAIN_HALL + Configuration.getServerId();
	}

	public ChatRoom getRoomByMember(String identity) {
		synchronized (this) {
			Iterator<Entry<String, ChatRoom>> it = roomList.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, ChatRoom> entry = it.next();
				for (String member : entry.getValue().getMemberList()) {
					if (member != null && member.equals(identity)) {
						return entry.getValue();
					}
				}
			}
			return null;
		}
	}
}