package com.tamfign.command;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class ServerServerCmd extends Command {

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
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public String joinRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_JOIN);
		root.put(P_ROOM_ID, roomId);
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

	public String deleteRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
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
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public String lockIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public String lockIdentityRs(String serverId, String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public String releaseIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}
}
