package com.tamfign.command;

public class Command {
	protected final static String TYPE = "type";
	protected final static String CMD = "cmd";
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
	protected final static String TYPE_RELEASE_ROOM = "roomchange";
	protected final static String TYPE_JOIN = "join";
	protected final static String TYPE_DELETE_ROOM = "deleteroom";
	protected final static String TYPE_ROUTE = "route";
	protected final static String TYPE_MOVE_JOIN = "movejoin";
	protected final static String TYPE_SERVER_CHANGE = "serverchange";

	protected final static String TYPE_MESSAGE = "message";
	protected final static String TYPE_QUIT = "quit";

	protected final static String P_IDENTITY = "identity";
	protected final static String P_SERVER_ID = "serverid";
	protected final static String P_LOCKED = "locked";
	public final static String P_APPROVED = "approved";
	protected final static String P_FORMER = "former";
	protected final static String P_ROOM_ID = "roomid";
	protected final static String P_ROOMS = "rooms";
	protected final static String P_IDENTITIES = "identities";
	protected final static String P_OWNER = "owner";
	protected final static String P_HOST = "host";
	protected final static String P_PORT = "port";
	protected final static String P_CONTENT = "content";

	public final static String CMD_LOCK_IDENTITY = "CMD_LOCK_IDENTITY";
	public final static String CMD_RELEASE_IDENTITY = "CMD_RELEASE_IDENTITY";
	public final static String CMD_LOCK_ROOM = "CMD_LOCK_ROOM";
	public final static String CMD_RELEASE_ROOM = "CMD_RELEASE_ROOM";
	public final static String CMD_DELETE_ROOM = "CMD_DELETE_ROOM";
}
