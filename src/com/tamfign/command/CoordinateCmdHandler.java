package com.tamfign.command;

import java.net.Socket;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.ConnectorInf;
import com.tamfign.connection.CoordinateConnector;
import com.tamfign.model.ChatRoom;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ClientListController;

public class CoordinateCmdHandler implements CmdHandler {
	private CoordinateConnector connector = null;

	public CoordinateCmdHandler(ConnectorInf connector) {
		this.connector = (CoordinateConnector) connector;
	}

	public void cmdAnalysis(Command cmd) {
		switch ((String) cmd.getObj().get(Command.TYPE)) {
		case Command.TYPE_SERVER_ON:
			handleServerOn(cmd);
			break;
		case Command.TYPE_LOCK_ID:
			handleLockId(cmd);
			break;
		case Command.TYPE_RELEASE_ID:
			handleReleaseId(cmd);
			break;
		case Command.TYPE_LOCK_ROOM:
			handleLockRoom(cmd);
			break;
		case Command.TYPE_RELEASE_ROOM:
			handleReleaseRoom(cmd);
			break;
		case Command.TYPE_DELETE_ROOM:
			handleDeleteRoom(cmd);
			break;
		case Command.CMD_LOCK_IDENTITY:
			broadcastLockIdentity(cmd);
			break;
		case Command.CMD_RELEASE_IDENTITY:
			broadcastReleaseIdentity(cmd);
			break;
		case Command.CMD_LOCK_ROOM:
			broadcastLockRoomId(cmd);
			break;
		case Command.CMD_DELETE_ROOM:
			broadcastDeleteRoomId(cmd);
			break;
		case Command.CMD_RELEASE_ROOM:
			broadcastReleaseRoomId(cmd);
			break;
		default:
		}
	}

	private void broadcastReleaseRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean result = (boolean) cmd.getObj().get(Command.P_APPROVED);
		connector.broadcast(ServerServerCmd.releaseRoom(Configuration.getServerId(), roomId, result));
	}

	private void broadcastDeleteRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		connector.broadcast(ServerServerCmd.deleteRoomBc(Configuration.getServerId(), roomId));
	}

	private void broadcastLockRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		connector.requestTheOther(InternalCmd.getLockRoomResultCmd(cmd.getOwner(), cmd.getSocket(), roomId,
				connector.broadcastAndGetResult(ServerServerCmd.lockRoomRq(Configuration.getServerId(), roomId))));
	}

	private void broadcastReleaseIdentity(Command cmd) {
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);
		connector.broadcast(ServerServerCmd.releaseIdentityRq(Configuration.getServerId(), identity));
	}

	private void broadcastLockIdentity(Command cmd) {
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);
		connector.requestTheOther(InternalCmd.getLockIdentityResultCmd(cmd.getOwner(), cmd.getSocket(), connector
				.broadcastAndGetResult(ServerServerCmd.lockIdentityRq(Configuration.getServerId(), identity))));
	}

	private void handleDeleteRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);

		if (room != null && room.getServerId().equals(serverId)) {
			ChatRoomListController.getInstance().deleteRoom(roomId);
		}
	}

	protected void handleServerOn(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		connector.connectServer(serverId);
	}

	private void handleReleaseRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean approved = Boolean.parseBoolean((String) cmd.getObj().get(Command.P_APPROVED));

		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);
		if (room != null && room.getServerId().equals(serverId)) {
			if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
				if (!approved) {
					ChatRoomListController.getInstance().removeRoom(roomId);
				}
			}
		}
	}

	private void handleLockRoom(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (checkLocalChatRoomList(serverId, roomId)) {
			approveLockRoom(cmd.getSocket(), roomId);
		} else {
			disapproveLockRoom(cmd.getSocket(), roomId);
		}
	}

	private void disapproveLockRoom(Socket socket, String roomId) {
		response(socket, ServerServerCmd.lockRoomRs(Configuration.getServerId(), roomId, false));
	}

	private void approveLockRoom(Socket socket, String roomId) {
		response(socket, ServerServerCmd.lockRoomRs(Configuration.getServerId(), roomId, true));
	}

	private boolean checkLocalChatRoomList(String serverId, String roomId) {
		boolean ret = false;
		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ChatRoomListController.getInstance().addRoom(roomId, serverId, null);
			ret = true;
		}
		return ret;
	}

	private void handleLockId(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);

		if (checkLocalIdentityList(serverId, identity)) {
			approveLockId(cmd.getSocket(), identity);
		} else {
			disapproveLockId(cmd.getSocket(), identity);
		}
	}

	private void handleReleaseId(Command cmd) {
		String serverId = (String) cmd.getObj().get(Command.P_SERVER_ID);
		String identity = (String) cmd.getObj().get(Command.P_IDENTITY);

		if (ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().releaseId(serverId, identity);
		}
	}

	private void disapproveLockId(Socket socket, String identity) {
		response(socket, ServerServerCmd.lockIdentityRs(Configuration.getServerId(), identity, false));
	}

	private void approveLockId(Socket socket, String identity) {
		response(socket, ServerServerCmd.lockIdentityRs(Configuration.getServerId(), identity, true));
	}

	private boolean checkLocalIdentityList(String serverId, String identity) {
		boolean ret = false;
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			ClientListController.getInstance().addIndentity(identity, serverId, null);
			ret = true;
		}
		return ret;
	}

	// TODO refractory
	protected void handleDisconnect(Command cmd) {
		// Do nothing, as assuming that servers won't crash.
	}

	// TODO refractory
	protected void response(Socket socket, String cmd) {
		connector.write(socket, cmd);
	}
}
