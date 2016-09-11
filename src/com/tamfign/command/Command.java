package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

public class Command {
	protected final static String TYPE = "type";
	protected final static String CMD = "type";
	protected final static String TYPE_NEW_ID = "newidentity";
	protected final static String TYPE_LOCK_ID = "lockidenity";
	protected final static String TYPE_RELEASE_ID = "releaseidentity";
	protected final static String TYPE_WHO = "who";

	protected final static String TYPE_LIST = "list";
	protected final static String TYPE_ROOM_LIST = "roomlist";
	protected final static String TYPE_CHANGE_ROOM = "roomchange";
	protected final static String TYPE_ROOM_CONTENTS = "roomcontents";
	protected final static String TYPE_CREATE_ROOM = "createroom";
	protected final static String TYPE_LOCK_ROOM = "lockroomid";
	protected final static String TYPE_RELEASE_ROOM = "releaseroomid";
	protected final static String TYPE_JOIN = "join";
	protected final static String TYPE_DELETE_ROOM = "deleteroom";
	protected final static String TYPE_ROUTE = "route";
	protected final static String TYPE_MOVE_JOIN = "movejoin";
	protected final static String TYPE_SERVER_CHANGE = "serverchange";

	protected final static String TYPE_MESSAGE = "message";
	protected final static String TYPE_QUIT = "quit";

	protected final static String TYPE_SERVER_ON = "server_on";

	protected final static String P_IDENTITY = "identity";
	protected final static String P_SERVER_ID = "serverid";
	protected final static String P_LOCKED = "locked";
	protected final static String P_APPROVED = "approved";
	protected final static String P_FORMER = "former";
	protected final static String P_ROOM_ID = "roomid";
	protected final static String P_ROOMS = "rooms";
	protected final static String P_IDENTITIES = "identities";
	protected final static String P_OWNER = "owner";
	protected final static String P_HOST = "host";
	protected final static String P_PORT = "port";
	protected final static String P_CONTENT = "content";

	protected final static String CMD_LOCK_IDENTITY = "CMD_LOCK_IDENTITY";
	protected final static String CMD_RELEASE_IDENTITY = "CMD_RELEASE_IDENTITY";
	protected final static String CMD_LOCK_ROOM = "CMD_LOCK_ROOM";
	protected final static String CMD_RELEASE_ROOM = "CMD_RELEASE_ROOM";
	protected final static String CMD_DELETE_ROOM = "CMD_DELETE_ROOM";

	private String owner = null;
	private JSONObject obj = null;
	private Socket socket = null;

	public Command(Socket socket, JSONObject cmd, String owner) {
		this.owner = owner;
		this.socket = socket;
		this.obj = cmd;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getOwner() {
		return owner;
	}

	public JSONObject getObj() {
		return obj;
	}

	public static boolean getResult(JSONObject obj) {
		boolean ret = false;

		switch ((String) obj.get(Command.TYPE)) {
		case (Command.TYPE_LOCK_ID):
		case (Command.TYPE_LOCK_ROOM):
			ret = Boolean.parseBoolean((String) obj.get(Command.P_LOCKED));
			break;
		default:
			ret = Boolean.parseBoolean((String) obj.get(Command.P_APPROVED));
		}
		return ret;
	}

	public static boolean isNewId(JSONObject obj) {
		String cmdType = (String) obj.get(Command.TYPE);
		return Command.TYPE_NEW_ID.equals(cmdType) || Command.TYPE_MOVE_JOIN.equals(cmdType);
	}

	public static String getNewId(JSONObject obj) {
		return (String) obj.get(Command.P_IDENTITY);
	}

	public static boolean isClosing(JSONObject obj) {
		String cmdType = (String) obj.get(Command.TYPE);
		return Command.TYPE_QUIT.equals(cmdType);
	}
}
