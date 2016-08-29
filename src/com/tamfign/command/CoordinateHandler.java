package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.Connector;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ClientListController;

public class CoordinateHandler extends ExternalHandler {
	private ServerServerCmd command = null;

	public CoordinateHandler(Connector connetor, Socket socket) {
		super(connetor, socket);
		this.command = new ServerServerCmd();
	}

	@Override
	public void cmdAnalysis(JSONObject obj) {
		switch ((String) obj.get(Command.TYPE)) {
		case Command.TYPE_SERVER_ON:
			handleServerOn((String) obj.get(Command.P_SERVER_ID));
			break;
		case Command.TYPE_LOCK_ID:
			handleLockId((String) obj.get(Command.P_SERVER_ID), (String) obj.get(Command.P_IDENTITY));
			break;
		case Command.TYPE_RELEASE_ID:
			handleReleaseId((String) obj.get(Command.P_SERVER_ID), (String) obj.get(Command.P_IDENTITY));
			break;
		case Command.TYPE_LOCK_ROOM:
			handleLockRoom((String) obj.get(Command.P_SERVER_ID), (String) obj.get(Command.P_ROOM_ID));
			break;
		case Command.TYPE_RELEASE_ROOM:
			handleReleaseRoom((String) obj.get(Command.P_SERVER_ID), (String) obj.get(Command.P_ROOM_ID),
					Boolean.parseBoolean((String) obj.get(Command.P_APPROVED)));
			break;
		default:
		}
	}

	protected void handleServerOn(String serverId) {
		getConnector().addBroadcastList(serverId, null);
	}

	private void handleReleaseRoom(String serverId, String roomId, boolean approved) {
		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			if (!approved) {
				ChatRoomListController.getInstance().removeRoom(roomId);
			}
		}
	}

	private void handleLockRoom(String serverId, String roomId) {
		if (checkLocalChatRoomList(serverId, roomId)) {
			approveLockRoom(roomId);
		} else {
			disapproveLockRoom(roomId);
		}
	}

	private void disapproveLockRoom(String roomId) {
		response(command.lockRoomRs(Configuration.getServerId(), roomId, false));
	}

	private void approveLockRoom(String roomId) {
		response(command.lockRoomRs(Configuration.getServerId(), roomId, true));
	}

	private boolean checkLocalChatRoomList(String serverId, String roomId) {
		boolean ret = false;
		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ChatRoomListController.getInstance().addRoom(roomId, serverId, null);
			ret = true;
		}
		return ret;
	}

	private void handleLockId(String serverId, String identity) {
		if (checkLocalIdentityList(serverId, identity)) {
			approveLockId(identity);
		} else {
			disapproveLockId(identity);
		}
	}

	private void handleReleaseId(String serverId, String identity) {
		if (ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().releaseId(serverId, identity);
		}
	}

	private void disapproveLockId(String identity) {
		response(command.lockIdentityRs(Configuration.getServerId(), identity, false));
	}

	private void approveLockId(String identity) {
		response(command.lockIdentityRs(Configuration.getServerId(), identity, true));
	}

	private boolean checkLocalIdentityList(String serverId, String identity) {
		boolean ret = false;
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().addIndentity(identity, serverId, null);
			ret = true;
		}
		return ret;
	}
}
