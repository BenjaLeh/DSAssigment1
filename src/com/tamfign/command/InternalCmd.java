package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class InternalCmd {
	public static Command getInternRoomCmd(String owner, Socket socket, String cmd, String roomId) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		return new Command(socket, obj, owner);
	}

	public static Command getInternRoomResultCmd(String owner, Socket socket, String cmd, String roomId, boolean result) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, result);
		return new Command(socket, obj, owner);
	}

	public static Command getInternIdCmd(String owner, Socket socket, String cmd, String identity) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_IDENTITY, identity);
		return new Command(socket, obj, owner);
	}

	public static Command getLockIdentityResultCmd(String owner, Socket socket, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, Command.CMD_LOCK_IDENTITY);
		obj.put(Command.P_APPROVED, approved);
		return new Command(socket, obj, owner);
	}

	public static Command getLockRoomResultCmd(String owner, Socket socket, String roomId, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, Command.CMD_LOCK_ROOM);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, approved);
		return new Command(socket, obj, owner);
	}
}
