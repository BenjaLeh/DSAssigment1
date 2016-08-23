package com.tamfign.command;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class ChatRoomCmd extends Command {

	public String roomChangeRq(String identity, String former, String newRoom) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CHANGE_ROOM);
		root.put(P_IDENTITY, identity);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, newRoom);
		return root.toJSONString();
	}

	public String listRq() {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LIST);
		return root.toJSONString();
	}

	public String listRs(ArrayList<String> roomList) {
		JSONObject root = new JSONObject();
		JSONArray jList = new JSONArray();
		jList.addAll(roomList);

		root.put(TYPE, TYPE_ROOM_LIST);
		root.put(P_ROOMS, jList);
		return root.toJSONString();
	}

	public String whoRq() {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_WHO);
		return root.toJSONString();
	}

	public String whoRs(String roomId, ArrayList<String> idList, String owner) {
		JSONObject root = new JSONObject();
		JSONArray jList = new JSONArray();
		jList.addAll(idList);

		root.put(TYPE, TYPE_ROOM_CONTENTS);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITIES, idList);
		root.put(P_OWNER, owner);
		return root.toJSONString();
	}

	public String createRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CREATE_ROOM);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public String createRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CREATE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, result);
		return root.toJSONString();
	}

	public String lockRoomRq(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public String lockRoomRs(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_LOCKED, result);
		return root.toJSONString();
	}

	public String joinRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_JOIN);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public String routeRq(String roomId, String host, int port) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_ROUTE);
		root.put(P_ROOM_ID, roomId);
		root.put(P_HOST, host);
		root.put(P_PORT, port);
		return root.toJSONString();
	}

	public String moveJoinRq(String former, String roomId, String id) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MOVE_JOIN);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITY, id);
		return root.toJSONString();
	}

	public String serverChangeBc(boolean result, String serverId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_SERVER_CHANGE);
		root.put(P_APPROVED, result);
		root.put(P_SERVER_ID, serverId);
		return root.toJSONString();
	}

	public String deleteRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public String deleteRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, result);
		return root.toJSONString();
	}

	public String deleteRoomBc(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public String releaseRoom(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, result);
		return root.toJSONString();
	}
}
