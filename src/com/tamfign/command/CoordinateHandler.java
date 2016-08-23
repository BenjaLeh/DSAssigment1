package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.Connector;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.IdentityListController;

public class CoordinateHandler extends Handler {
	private ChatRoomCmd chatRoomCmd = null;
	private IdentityCmd identityCmd = null;
	private MessageCmd messageCmd = null;

	public CoordinateHandler(Connector connetor, Socket socket) {
		super(connetor, socket);
		chatRoomCmd = new ChatRoomCmd();
		identityCmd = new IdentityCmd();
		messageCmd = new MessageCmd();
	}

	@Override
	protected void cmdAnalysis(JSONObject obj) {
		String serverId;
		String identity;
		String roomId;

		switch ((String) obj.get(Command.TYPE)) {
		case Command.TYPE_LOCK_ID:
			serverId = (String) obj.get(Command.P_SERVER_ID);
			identity = (String) obj.get(Command.P_IDENTITY);
			handleLockIdRq(serverId, identity);
			break;
		case Command.TYPE_RELEASE_ID:
			serverId = (String) obj.get(Command.P_SERVER_ID);
			identity = (String) obj.get(Command.P_IDENTITY);
			releaseIdIfExists(serverId, identity);
			break;
		case Command.TYPE_LOCK_ROOM:
			serverId = (String) obj.get(Command.P_SERVER_ID);
			roomId = (String) obj.get(Command.P_ROOM_ID);
			handleLockRoomRq(serverId, roomId);
			break;
		case Command.TYPE_RELEASE_ROOM:
			serverId = (String) obj.get(Command.P_SERVER_ID);
			roomId = (String) obj.get(Command.P_ROOM_ID);
			releaseRoomIfExists(serverId, roomId);
			break;
		default:
		}
	}

	private void releaseRoomIfExists(String serverId, String roomId) {
		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ChatRoomListController.getInstance().removeRoom(roomId);
		}
	}

	private void handleLockRoomRq(String serverId, String roomId) {
		if (checkLocalChatRoomList(serverId, roomId)) {
			approveLockRoom(roomId);
		} else {
			disapproveLockRoom(roomId);
		}
	}

	private void disapproveLockRoom(String roomId) {
		response(chatRoomCmd.lockRoomRs(Configuration.getServerId(), roomId, false));
	}

	private void approveLockRoom(String roomId) {
		response(chatRoomCmd.lockRoomRs(Configuration.getServerId(), roomId, true));
	}

	private boolean checkLocalChatRoomList(String serverId, String roomId) {
		boolean ret = false;
		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ChatRoomListController.getInstance().addRoom(roomId, serverId, null);
			ret = true;
		}
		return ret;
	}

	private void handleLockIdRq(String serverId, String identity) {
		if (checkLocalIdentityList(serverId, identity)) {
			approveLockId(identity);
		} else {
			disapproveLockId(identity);
		}
	}

	private void releaseIdIfExists(String serverId, String identity) {
		if (IdentityListController.getInstance().isIdentityExist(identity)) {
			IdentityListController.getInstance().releaseId(serverId, identity);
		}
	}

	private void disapproveLockId(String identity) {
		response(identityCmd.lockIdentityRs(Configuration.getServerId(), identity, false));
	}

	private void approveLockId(String identity) {
		response(identityCmd.lockIdentityRs(Configuration.getServerId(), identity, true));
	}

	private boolean checkLocalIdentityList(String serverId, String identity) {
		boolean ret = false;
		if (!IdentityListController.getInstance().isIdentityExist(identity)) {
			IdentityListController.getInstance().addIndentity(serverId, identity);
			ret = true;
		}
		return ret;
	}
}
