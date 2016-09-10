package com.tamfign.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class ClientServerCmd extends Command {

	public ClientServerCmd(Socket socket, JSONObject cmd, String owner) {
		super(socket, cmd, owner);
	}

	public static String newIdentityRs(String identity, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_NEW_ID);
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String createRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CREATE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String deleteRoomRs(String roomId, boolean result) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_DELETE_ROOM);
		root.put(P_ROOM_ID, roomId);
		root.put(P_APPROVED, Boolean.toString(result));
		return root.toJSONString();
	}

	public static String roomChangeRq(String identity, String former, String newRoom) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_CHANGE_ROOM);
		root.put(P_IDENTITY, identity);
		root.put(P_FORMER, former);
		root.put(P_ROOM_ID, newRoom);
		return root.toJSONString();
	}

	// Server todo
	public static String serverChangeRs(boolean result, String serverId) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_SERVER_CHANGE);
		root.put(P_APPROVED, Boolean.toString(result));
		root.put(P_SERVER_ID, serverId);
		return root.toJSONString();
	}

	public static String listRs(ArrayList<String> roomList) {
		JSONObject root = new JSONObject();
		JSONArray jList = new JSONArray();
		jList.addAll(roomList);

		root.put(TYPE, TYPE_ROOM_LIST);
		root.put(P_ROOMS, jList);
		return root.toJSONString();
	}

	public static String whoRs(String roomId, ArrayList<String> idList, String owner) {
		JSONObject root = new JSONObject();
		JSONArray jList = new JSONArray();
		for (String member : idList) {
			if (member != null) {
				jList.add(member);
			}
		}

		root.put(TYPE, TYPE_ROOM_CONTENTS);
		root.put(P_ROOM_ID, roomId);
		root.put(P_IDENTITIES, jList);
		root.put(P_OWNER, owner == null ? "" : owner);
		return root.toJSONString();
	}

	public static String messageCmd(String id, String content) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_MESSAGE);
		root.put(P_IDENTITY, id);
		root.put(P_CONTENT, content);
		return root.toJSONString();
	}

	public static String routeRq(String roomId, String host, int port) {
		JSONObject root = new JSONObject();
		root.put(TYPE, TYPE_ROUTE);
		root.put(P_ROOM_ID, roomId);
		root.put(P_HOST, host);
		root.put(P_PORT, String.valueOf(port));
		return root.toJSONString();
	}
}
