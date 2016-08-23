package com.tamfign.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.tamfign.command.ChatRoomCmd;
import com.tamfign.command.Command;
import com.tamfign.command.CoordinateHandler;
import com.tamfign.command.Handler;
import com.tamfign.command.IdentityCmd;
import com.tamfign.configuration.Configuration;
import com.tamfign.model.ServerConfig;
import com.tamfign.model.ServerListController;

public class CoordinateListener extends Connector implements Runnable {

	private ChatRoomCmd chatRoomCmd = null;
	private IdentityCmd identityCmd = null;

	protected CoordinateListener(ConnectController controller) {
		super(controller);
		chatRoomCmd = new ChatRoomCmd();
		identityCmd = new IdentityCmd();
	}

	public void run() {
		checkOtherServers();
		try {
			keepListenPortAndAcceptMultiClient(Configuration.getCoordinationPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkOtherServers() {
		for (int i = 0; i < ServerListController.getInstance().size(); i++) {
			if (ServerListController.getInstance().get(i).isItselft()) {
				continue;
			}
			if (testConnection(ServerListController.getInstance().get(i))) {
				ServerListController.getInstance().get(i).setActived(true);
			}
		}
	}

	private boolean testConnection(ServerConfig server) {
		boolean ret = false;

		Socket client = null;
		try {
			client = new Socket(server.getHost(), server.getCoordinationPort());
			if (!client.isConnected()) {
				ret = false;
			} else {
				sendOutOwnId(client);
			}
		} catch (Exception e) {
			ret = false;
		} finally {
			close(client);
		}
		return ret;
	}

	private void sendOutOwnId(Socket socket) throws IOException {
		if (socket == null || socket.isClosed())
			return;
		PrintWriter os = new PrintWriter(socket.getOutputStream());
		os.println(Configuration.getServerId());// TODO format msg
		os.flush();
		os.close();
	}

	@Override
	protected Handler getHandler(Socket socket) {
		return new CoordinateHandler(this, socket);
	}

	public boolean runInternalRequest(String cmd, Object obj) {
		String identity;
		String roomId;
		boolean ret = false;

		switch (cmd) {
		case Command.CMD_LOCK_IDENTITY:
			identity = (String) obj;
			ret = broadcastLockIdentity(identity);
			break;
		case Command.CMD_RELEASE_IDENTITY:
			identity = (String) obj;
			broadcastReleaseIdentity(identity);
			break;
		case Command.CMD_LOCK_ROOM:
			roomId = (String) obj;
			ret = broadcastLockRoomId(roomId);
			break;
		default:
		}
		return ret;
	}

	//TODO Move to handler
	private boolean broadcastLockRoomId(String roomId) {
		return broadcastAndGetResult(chatRoomCmd.lockRoomRq(Configuration.getServerId(), roomId));
	}

	private void broadcastReleaseIdentity(String identity) {
		broadcast(identityCmd.releaseIdentityRq(Configuration.getServerId(), identity));
	}

	private boolean broadcastLockIdentity(String identity) {
		return broadcastAndGetResult(identityCmd.lockIdentityRq(Configuration.getServerId(), identity));
	}

	@Override
	public boolean requestTheOther(String cmd, Object obj) {
		return getController().requestClient(cmd, obj);
	}
}