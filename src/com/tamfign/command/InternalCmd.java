package com.tamfign.command;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class InternalCmd extends Command {
	public InternalCmd(Command oldCmd, JSONObject obj) {
		super(oldCmd, obj);
	}

	public static Command getInternRoomCmd(Command oldCmd, String cmd, String roomId) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternRoomResultCmd(Command oldCmd, String cmd, String roomId, boolean result) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, result);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getInternIdCmd(Command oldCmd, String cmd, String identity) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_IDENTITY, identity);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getLockIdentityResultCmd(Command oldCmd, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, Command.CMD_LOCK_IDENTITY);
		obj.put(Command.P_APPROVED, approved);
		return new InternalCmd(oldCmd, obj);
	}

	public static Command getLockRoomResultCmd(Command oldCmd, String roomId, boolean approved) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, Command.CMD_LOCK_ROOM);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, approved);
		return new InternalCmd(oldCmd, obj);
	}
}
