package com.tamfign.command;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.connection.Connector;

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
			if (checkLocalIdentityList()) {
				approveLockId();
			} else {
				disapproveLockId();
			}
			break;

		default:
		}
	}

	private void disapproveLockId() {
		// TODO Auto-generated method stub
		
	}

	private void approveLockId() {
		// TODO Auto-generated method stub
		
	}

	private boolean checkLocalIdentityList() {
		// TODO Auto-generated method stub
		return false;
	}
}
