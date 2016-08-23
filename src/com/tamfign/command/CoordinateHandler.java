package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.Connector;
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
		String approval;

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
		default:
		}
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
			ret = true;
			IdentityListController.getInstance().addIndentity(serverId, identity);
		}
		return ret;
	}
}
