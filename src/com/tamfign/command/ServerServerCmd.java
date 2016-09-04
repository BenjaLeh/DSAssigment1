package com.tamfign.command;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;

@SuppressWarnings("unchecked")
public class ServerServerCmd extends Command {

	public static String lockRoomRq(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String lockRoomRs(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String joinRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_JOIN);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String moveJoinRq(String former, String roomId, String id) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MOVE_JOIN);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITY, id);
		return root.toJSONString();
	}

	public static String deleteRoomRq(String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String deleteRoomBc(String serverId, String roomId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		return root.toJSONString();
	}

	public static String releaseRoom(String serverId, String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ROOM);
		root.put(P_SERVER_ID, serverId);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String lockIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public static String lockIdentityRs(String serverId, String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_LOCK_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		root.put(P_LOCKED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String releaseIdentityRq(String serverId, String identity) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_RELEASE_ID);
		root.put(P_SERVER_ID, serverId);
		root.put(P_IDENTITY, identity);
		return root.toJSONString();
	}

	public static String getServerOnCmd() {
		JSONObject obj = new JSONObject();
		obj.put(Command.TYPE, Command.TYPE_SERVER_ON);
		obj.put(Command.P_SERVER_ID, Configuration.getServerId());
		return obj.toJSONString();
	}

	public static JSONObject getInternRoomCmdObject(String cmd, String roomId) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		return obj;
	}

	public static JSONObject getInternRoomResultCmdObject(String cmd, String roomId, boolean result) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, result);
		return obj;
	}

	public static JSONObject getInternIdCmdObject(String cmd, String identity) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_IDENTITY, identity);
		return obj;
	}
}
