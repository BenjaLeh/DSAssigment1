package com.tamfign.command;

import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.connection.ClientConnector;
import com.tamfign.model.ChatRoom;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.ClientListController;
import com.tamfign.model.ServerListController;

public class ClientHandler extends ExternalHandler {
	private String thisClientId = null;
	private ClientServerCmd command = null;

	public ClientHandler(ClientConnector connector, Socket socket) {
		super(connector, socket);
		this.command = new ClientServerCmd();
	}

	public void cmdAnalysis(JSONObject root) {
		switch ((String) root.get(Command.TYPE)) {
		case Command.TYPE_MESSAGE:
			handleMessage((String) root.get(Command.P_CONTENT));
			break;
		case Command.TYPE_QUIT:
			handleQuit();
			break;
		case Command.TYPE_NEW_ID:
			handleNewIdentity((String) root.get(Command.P_IDENTITY));
			break;
		case Command.TYPE_LIST:
			handleList();
			break;
		case Command.TYPE_WHO:
			handleWho();
			break;
		case Command.TYPE_CREATE_ROOM:
			handleCreateRoom((String) root.get(Command.P_ROOM_ID));
			break;
		case Command.TYPE_DELETE_ROOM:
			handleDeleteRoom((String) root.get(Command.P_ROOM_ID));
			break;
		case Command.TYPE_JOIN:
			handleJoinRoom((String) root.get(Command.P_ROOM_ID));
			break;
		case Command.TYPE_MOVE_JOIN:
			handlerMoveJoin((String) root.get(Command.P_IDENTITY), (String) root.get(Command.P_FORMER),
					(String) root.get(Command.P_ROOM_ID));
			break;
		default:
		}
	}

	private void handleJoinRoom(String roomId) {
		if (isRoomAvailable(roomId)) {
			ServerConfig server = getServer(roomId);
			if (!server.isItselft()) {
				routeClient(roomId, server);
			} else {
				approveJoin(roomId);
			}
		} else {
			disapproveJoin(roomId);
		}
	}

	private void handleDeleteRoom(String roomId) {
		if (isRoomCanBeDel(roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getChatRoom(roomId)
					.getMemberList();
			deleteRoom(roomId);
			broadcastDeleteRoom(roomId, currentMemberList);
			approveDeleteRoom(roomId);
		} else {
			disapproveDeleteRoom(roomId);
		}
	}

	private void handleCreateRoom(String roomId) {
		if (lockRoomId(roomId)) {
			String currentRoomId = getCurrentRoomId();
			createChatRoom(roomId);
			approveChatRoom(roomId);
			broadcastRoomChange(currentRoomId, roomId);
		} else {
			disapproveChatRoom(roomId);
		}
	}

	private void handleNewIdentity(String id) {
		if (lockIdentity(id)) {
			createIdentity(id, ChatRoomListController.getLocalMainHall());
			approveIdentity(id);
			((ClientConnector) getConnector()).broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					command.roomChangeRq(thisClientId, "", ChatRoomListController.getLocalMainHall()));
		} else {
			disapproveIdentity(id);
		}
	}

	private void handleMessage(String content) {
		((ClientConnector) getConnector()).broadcastWithinRoom(null, getCurrentRoomId(),
				command.messageCmd(this.thisClientId, content));
	}

	private void handleQuit() {
		if (isOwnerOfRoom()) {
			deleteRoomAndBroadcastRoomChange();
		}
		removeFromClientList();
		responseChangeRoomAndTerminate();
	}

	private void handlerMoveJoin(String id, String formerRoom, String roomId) {
		String newRoom = null;
		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			newRoom = roomId;
		} else {
			newRoom = ChatRoomListController.getLocalMainHall();
		}
		createIdentity(id, newRoom);
		((ClientConnector) getConnector()).broadcastWithinRoom(null, newRoom,
				command.roomChangeRq(thisClientId, formerRoom, newRoom));
	}

	private void responseChangeRoomAndTerminate() {
		response(command.roomChangeRq(thisClientId, "", ""));
		terminate();
	}

	private void handleWho() {
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(getCurrentRoomId());
		response(command.whoRs(room.getName(), room.getMemberList(), room.getOwner()));
	}

	private String getCurrentRoomId() {
		return ClientListController.getInstance().getClient(thisClientId).getRoomId();
	}

	private void deleteRoomAndBroadcastRoomChange() {
		String roomId = ClientListController.getInstance().getClient(thisClientId).getOwnRoom();
		if (isRoomCanBeDel(roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getChatRoom(roomId)
					.getMemberList();
			currentMemberList.remove(this.thisClientId);
			deleteRoom(roomId);
			broadcastDeleteRoom(roomId, currentMemberList);
		}
	}

	private boolean isOwnerOfRoom() {
		return ClientListController.getInstance().getClient(thisClientId).getOwnRoom() != null;
	}

	private void removeFromClientList() {
		ChatRoomListController.getInstance().getChatRoom(getCurrentRoomId()).removeMember(thisClientId);
		ClientListController.getInstance().removeIndentity(thisClientId);
	}

	private void broadcastDeleteRoom(String roomId, ArrayList<String> currentMemberList) {
		getConnector().requestTheOther(getInternRoomCmdObject(Command.CMD_DELETE_ROOM, roomId));

		// TODO need to be well tested
		for (String id : currentMemberList) {
			((ClientConnector) getConnector()).broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					command.roomChangeRq(id, roomId, ChatRoomListController.getLocalMainHall()));
		}
	}

	private void deleteRoom(String roomId) {
		// Change room id in the client list.
		for (String identity : ChatRoomListController.getInstance().getChatRoom(roomId).getMemberList()) {
			ClientListController.getInstance().getClient(identity).setRoomId(ChatRoomListController.getLocalMainHall());
		}
		ChatRoomListController.getInstance().deleteRoom(roomId);
	}

	private void disapproveDeleteRoom(String roomId) {
		response(command.deleteRoomRs(roomId, false));
	}

	private void approveDeleteRoom(String roomId) {
		response(command.deleteRoomRs(roomId, true));
	}

	private boolean isRoomCanBeDel(String roomId) {
		boolean ret = false;

		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);
		if (room != null && thisClientId.equals(room.getOwner())) {
			ret = true;
		}
		return ret;
	}

	private void routeClient(String roomId, ServerConfig server) {
		String formerRoom = getCurrentRoomId();
		response(command.routeRq(roomId, server.getHost(), server.getClientPort()));
		removeFromClientList();
		((ClientConnector) getConnector()).broadcastWithinRoom(formerRoom, null,
				command.roomChangeRq(thisClientId, formerRoom, roomId));
		terminate();
	}

	private void terminate() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		terminate(thisClientId);// Will close socket and terminal this thread.
		thisClientId = null;
	}

	private ServerConfig getServer(String roomId) {
		return ServerListController.getInstance().get(getRoomServerId(roomId));
	}

	private String getRoomServerId(String roomId) {
		return ChatRoomListController.getInstance().getChatRoom(roomId).getServerId();
	}

	private void disapproveJoin(String roomId) {
		response(command.roomChangeRq(this.thisClientId, getCurrentRoomId(), getCurrentRoomId()));
	}

	private void approveJoin(String roomId) {
		String former = getCurrentRoomId();
		ClientListController.getInstance().getClient(this.thisClientId).setRoomId(roomId);
		ChatRoomListController.getInstance().changeRoom(former, roomId, this.thisClientId);
		broadcastRoomChange(former, roomId);
	}

	private boolean isRoomAvailable(String roomId) {
		boolean ret = false;

		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			if (!isOwnerOfRoom()) {
				ret = true;
			}
		}
		return ret;
	}

	private void disapproveChatRoom(String roomId) {
		response(command.createRoomRs(roomId, false));
	}

	private void approveChatRoom(String roomId) {
		response(command.createRoomRs(roomId, true));
	}

	private void createChatRoom(String roomId) {
		ChatRoomListController.getInstance().addRoom(roomId, Configuration.getServerId(), thisClientId);
		ChatRoomListController.getInstance()
				.getChatRoom(ClientListController.getInstance().getClient(thisClientId).getRoomId())
				.removeMember(thisClientId);
		ClientListController.getInstance().getClient(thisClientId).setRoomId(roomId);
	}

	private boolean lockRoomId(String roomId) {
		boolean ret = false;

		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			ret = getConnector().requestTheOther(getInternRoomCmdObject(Command.CMD_LOCK_ROOM, roomId));
			releaseRoomId(roomId, ret);
		}
		return ret;
	}

	private void releaseRoomId(String roomId, boolean result) {
		getConnector().requestTheOther(getInternRoomResultCmdObject(Command.CMD_RELEASE_ROOM, roomId, result));
	}

	private void handleList() {
		response(command.listRs(ChatRoomListController.getInstance().getList()));
	}

	private void disapproveIdentity(String id) {
		sendDisapproveIdentity(id);
	}

	private void sendDisapproveIdentity(String id) {
		response(command.newIdentityRs(id, false));
	}

	private void approveIdentity(String id) {
		response(command.newIdentityRs(id, true));
	}

	private void broadcastRoomChange(String former, String newRoom) {
		((ClientConnector) getConnector()).broadcastWithinRoom(former, newRoom,
				command.roomChangeRq(this.thisClientId, former, newRoom));
	}

	private void createIdentity(String identity, String roomId) {
		ClientListController.getInstance().addIndentity(identity, Configuration.getServerId(), roomId);
		ChatRoomListController.getInstance().getChatRoom(roomId).addMember(identity);
		this.thisClientId = identity;
		certainClientSocket(identity);
	}

	private boolean lockIdentity(String identity) {
		boolean ret = false;
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			ret = getConnector().requestTheOther(getInternIdCmdObject(Command.CMD_LOCK_IDENTITY, identity));
			releaseIdentity(identity);
		}
		return ret;
	}

	private void releaseIdentity(String identity) {
		getConnector().requestTheOther(getInternIdCmdObject(Command.CMD_RELEASE_IDENTITY, identity));
	}

	@SuppressWarnings("unchecked")
	private JSONObject getInternRoomCmdObject(String cmd, String roomId) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getInternRoomResultCmdObject(String cmd, String roomId, boolean result) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_ROOM_ID, roomId);
		obj.put(Command.P_APPROVED, result);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getInternIdCmdObject(String cmd, String identity) {
		JSONObject obj = new JSONObject();
		obj.put(Command.CMD, cmd);
		obj.put(Command.P_IDENTITY, identity);
		return obj;
	}
}
