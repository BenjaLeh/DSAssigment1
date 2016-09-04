package com.tamfign.command;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.Connector;

public class InternalHandler {
	private Connector connector = null;

	public InternalHandler(Connector connector) {
		this.connector = connector;
	}

	public boolean cmdAnalysis(JSONObject obj) {
		String identity;
		String roomId;
		boolean approve;
		boolean ret = false;

		switch ((String) obj.get(Command.CMD)) {
		case Command.CMD_LOCK_IDENTITY:
			identity = (String) obj.get(Command.P_IDENTITY);
			ret = broadcastLockIdentity(identity);
			break;
		case Command.CMD_RELEASE_IDENTITY:
			identity = (String) obj.get(Command.P_IDENTITY);
			broadcastReleaseIdentity(identity);
			break;
		case Command.CMD_LOCK_ROOM:
			roomId = (String) obj.get(Command.P_ROOM_ID);
			ret = broadcastLockRoomId(roomId);
			break;
		case Command.CMD_DELETE_ROOM:
			roomId = (String) obj.get(Command.P_ROOM_ID);
			broadcastDeleteRoomId(roomId);
			break;
		case Command.CMD_RELEASE_ROOM:
			roomId = (String) obj.get(Command.P_ROOM_ID);
			approve = (boolean) obj.get(Command.P_APPROVED);
			broadcastReleaseRoomId(roomId, approve);
			break;
		default:
		}
		return ret;
	}

	private void broadcastReleaseRoomId(String roomId, boolean result) {
		connector.broadcast(ServerServerCmd.releaseRoom(Configuration.getServerId(), roomId, result));
	}

	private void broadcastDeleteRoomId(String roomId) {
		connector.broadcast(ServerServerCmd.deleteRoomBc(Configuration.getServerId(), roomId));
	}

	private boolean broadcastLockRoomId(String roomId) {
		return connector.broadcastAndGetResult(ServerServerCmd.lockRoomRq(Configuration.getServerId(), roomId));
	}

	private void broadcastReleaseIdentity(String identity) {
		connector.broadcast(ServerServerCmd.releaseIdentityRq(Configuration.getServerId(), identity));
	}

	private boolean broadcastLockIdentity(String identity) {
		return connector.broadcastAndGetResult(ServerServerCmd.lockIdentityRq(Configuration.getServerId(), identity));
	}
}
