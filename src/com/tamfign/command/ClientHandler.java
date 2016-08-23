package com.tamfign.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tamfign.configuration.Configuration;
import com.tamfign.connection.ClientListener;
import com.tamfign.connection.ConnectController;
import com.tamfign.model.ChatRoom;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.IdentityListController;
import com.tamfign.model.ServerConfig;

public class ClientHandler extends Handler {
	private String thisClientId = null;
	private ChatRoomCmd chatRoomCmd = null;
	private IdentityCmd identityCmd = null;
	private MessageCmd messageCmd = null;

	public ClientHandler(ClientListener connector, Socket socket) {
		super(connector, socket);
		chatRoomCmd = new ChatRoomCmd();
		identityCmd = new IdentityCmd();
		messageCmd = new MessageCmd();
	}

	protected void cmdAnalysis(JSONObject root) {
		String id;
		String roomId;
		String content;

		switch ((String) root.get(Command.TYPE)) {
		case Command.TYPE_MESSAGE:
			content = (String) root.get(Command.P_CONTENT);
			broadCastMessage(content);
			break;
		case Command.TYPE_QUIT:
			removeFromClientList();
			if (isOwnerOfRoom()) {
				sendRoomChange();
			}
			break;
		case Command.TYPE_NEW_ID:
			id = (String) root.get(Command.P_IDENTITY);
			if (lockIdentity(id)) {
				createIdentity(id);
				approveIdentity(id);
				broadcastRoomChange(id, "", ChatRoomListController.getInstance().getMainHall());
			} else {
				disapproveIdentity(id);
			}
			break;
		case Command.TYPE_LIST:
			sendRoomList();
			break;
		case Command.TYPE_WHO:
			sendMemberList();
			break;
		case Command.TYPE_CREATE_ROOM:
			roomId = (String) root.get(Command.P_ROOM_ID);
			if (lockRoomId(roomId)) {
				String currentRoomId = ChatRoomListController.getInstance().getRoomByMember(thisClientId).getName();
				createChatRoom(roomId);
				approveChatRoom(roomId);
				broadcastRoomChange(thisClientId, currentRoomId, roomId);
			} else {
				disapproveChatRoom(roomId);
			}
			break;
		case Command.TYPE_DELETE_ROOM:
			roomId = (String) root.get(Command.P_ROOM_ID);
			if (tryDeleteRoom(roomId)) {
				broadcastDeleteRoom(roomId);
				approveDeleteRoom(roomId);
			} else {
				disapproveDeleteRoom(roomId);
			}
			break;
		case Command.TYPE_JOIN:
			roomId = (String) root.get(Command.P_ROOM_ID);
			if (joinRoom(roomId)) {
				approveJoin(roomId);
				ServerConfig server = getServer(roomId);
				if (!server.isItselft()) {
					broadcaseRoute(roomId, server);
					moveJoin(roomId);
				}
			} else {
				disapproveJoin(roomId);
			}
			break;
		default:
		}
	}

	private void sendMemberList() {
		ChatRoom room = ChatRoomListController.getInstance().getRoomByMember(this.thisClientId);
		response(chatRoomCmd.whoRs(room.getName(), room.getMemberList(), room.getOwner()));
	}

	private void sendRoomChange() {
		// TODO Auto-generated method stub

	}

	private boolean isOwnerOfRoom() {
		// TODO Auto-generated method stub
		return false;
	}

	private void removeFromClientList() {
		// TODO Auto-generated method stub

	}

	private void broadCastMessage(String content) {
		String cmd = messageCmd.messageCmd(this.thisClientId, content);
	}

	private void broadcastDeleteRoom(String roomId) {
		String cmd = chatRoomCmd.deleteRoomBc(Configuration.getServerId(), roomId);
	}

	private void disapproveDeleteRoom(String roomId) {
		String cmd = chatRoomCmd.deleteRoomRs(roomId, false);
	}

	private void approveDeleteRoom(String roomId) {
		String cmd = chatRoomCmd.deleteRoomRs(roomId, true);
	}

	private boolean tryDeleteRoom(String roomId) {
		// TODO Auto-generated method stub
		return false;
	}

	private void moveJoin(String roomId) {
		String cmd = chatRoomCmd.moveJoinRq(former, roomId, this.thisClientId);
	}

	private void broadcaseRoute(String roomId, ServerConfig server) {
		String cmd = chatRoomCmd.routeRq(roomId, server.getHost(), server.getClientPort());
	}

	private ServerConfig getServer(String roomId) {
		return null;// TODO
	}

	private void disapproveJoin(String roomId) {
		String cmd = chatRoomCmd.roomChangeRq(this.thisClientId, former, former);
	}

	private void approveJoin(String roomId) {
		String cmd = chatRoomCmd.roomChangeRq(this.thisClientId, former, roomId);
	}

	private boolean joinRoom(String roomId) {
		// RoomExist?
		// Owner of a room
		return false;
	}

	private void disapproveChatRoom(String roomId) {
		response(chatRoomCmd.createRoomRs(roomId, false));
	}

	private void approveChatRoom(String roomId) {
		response(chatRoomCmd.createRoomRs(roomId, true));
	}

	private void createChatRoom(String roomId) {
		ChatRoomListController.getInstance().addRoom(roomId, Configuration.getServerId(), thisClientId);
	}

	private boolean lockRoomId(String roomId) {
		boolean ret = getConnector().requestTheOther(Command.CMD_LOCK_ROOM, roomId);
		releaseRoomId(roomId, ret);
		return false;
	}

	private void releaseRoomId(String roomId, boolean result) {
		getConnector().broadcast(chatRoomCmd.releaseRoom(Configuration.getServerId(), roomId, result));
	}

	private void sendRoomList() {
		response(chatRoomCmd.listRs(ChatRoomListController.getInstance().getList()));
	}

	private void disapproveIdentity(String id) {
		sendDisapproveIdentity(id);
	}

	private void sendDisapproveIdentity(String id) {
		response(identityCmd.newIdentityRs(id, false));
	}

	private void approveIdentity(String id) {
		response(identityCmd.newIdentityRs(id, true));
	}

	private void broadcastRoomChange(String id, String former, String newRoom) {
		((ClientListener) getConnector()).broadcastWithinRoom(former, chatRoomCmd.roomChangeRq(id, former, newRoom));
	}

	private void createIdentity(String identity) {
		IdentityListController.getInstance().addIndentity(Configuration.getServerId(), identity);
		this.thisClientId = identity;
		certainSocket(identity);
	}

	private boolean lockIdentity(String identity) {
		boolean ret = getConnector().requestTheOther(Command.CMD_LOCK_IDENTITY, identity);
		releaseIdentity(identity);
		return ret;
	}

	private void releaseIdentity(String identity) {
		getConnector().requestTheOther(Command.CMD_RELEASE_IDENTITY, identity);
	}
}
