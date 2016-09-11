package com.tamfign.command;

import java.net.Socket;
import java.util.ArrayList;

import com.tamfign.configuration.Configuration;
import com.tamfign.configuration.ServerConfig;
import com.tamfign.connection.ClientConnector;
import com.tamfign.connection.ConnectorInf;
import com.tamfign.model.ChatRoom;
import com.tamfign.model.ChatRoomListController;
import com.tamfign.model.Client;
import com.tamfign.model.ClientListController;
import com.tamfign.model.ServerListController;

public class ClientCmdHandler implements CmdHandler {
	private ClientConnector connector = null;

	public ClientCmdHandler(ConnectorInf connector) {
		this.connector = (ClientConnector) connector;
	}

	public void cmdAnalysis(Command cmd) {
		switch ((String) cmd.getObj().get(Command.TYPE)) {
		case Command.TYPE_MESSAGE:
			handleMessage(cmd);
			break;
		case Command.TYPE_QUIT:
			handleQuit(cmd);
			break;
		case Command.TYPE_NEW_ID:
			handleLockIdentiy(cmd);
			break;
		case Command.CMD_LOCK_IDENTITY:
			handleNewIdentity(cmd);
			break;
		case Command.TYPE_LIST:
			handleList(cmd);
			break;
		case Command.TYPE_WHO:
			handleWho(cmd);
			break;
		case Command.TYPE_CREATE_ROOM:
			handleLockRoomId(cmd);
			break;
		case Command.CMD_LOCK_ROOM:
			handleCreateRoom(cmd);
			break;
		case Command.TYPE_DELETE_ROOM:
			handleDeleteRoom(cmd);
			break;
		case Command.TYPE_JOIN:
			handleJoinRoom(cmd);
			break;
		case Command.TYPE_MOVE_JOIN:
			handlerMoveJoin(cmd);
			break;
		default:
		}
	}

	private void handleJoinRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (!isOwnerOfRoom(cmd.getOwner()) && isRoomAvailable(roomId)) {
			ServerConfig server = getServer(roomId);
			if (!server.isItselft()) {
				routeClient(cmd.getOwner(), roomId, server, cmd.getSocket());
			} else {
				approveJoin(cmd.getOwner(), roomId);
			}
		} else {
			disapproveJoin(cmd.getSocket(), cmd.getOwner(), roomId);
		}
	}

	private void handleDeleteRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (isRoomCanBeDel(cmd.getOwner(), roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getChatRoom(roomId)
					.getMemberList();
			deleteRoom(roomId);
			broadcastDeleteRoom(cmd.getSocket(), cmd.getOwner(), roomId, currentMemberList);
			approveDeleteRoom(cmd.getSocket(), roomId);
		} else {
			disapproveDeleteRoom(cmd.getSocket(), roomId);
		}
	}

	private void handleLockRoomId(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (!ChatRoomListController.getInstance().isRoomExists(roomId)) {
			connector.requestTheOther(
					InternalCmd.getInternRoomCmd(cmd.getOwner(), cmd.getSocket(), Command.CMD_LOCK_ROOM, roomId));
		} else {
			disapproveChatRoom(cmd.getSocket(), roomId);
		}
	}

	private void handleCreateRoom(Command cmd) {
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);
		boolean approved = (Boolean) cmd.getObj().get(Command.P_APPROVED);

		releaseRoomId(cmd.getSocket(), cmd.getOwner(), roomId, approved);
		if (approved) {
			String currentRoomId = getCurrentRoomId(cmd.getOwner());
			createChatRoom(cmd.getOwner(), roomId);
			approveChatRoom(cmd.getSocket(), roomId);
			broadcastRoomChange(cmd.getOwner(), currentRoomId, roomId);
		} else {
			disapproveChatRoom(cmd.getSocket(), roomId);
		}
	}

	private void handleNewIdentity(Command cmd) {
		boolean approved = (Boolean) cmd.getObj().get(Command.P_APPROVED);
		if (approved) {
			createIdentity(cmd.getOwner(), cmd.getSocket(), ChatRoomListController.getLocalMainHall());
			approveIdentity(cmd.getSocket(), cmd.getOwner());
			connector.broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					ClientServerCmd.roomChangeRq(cmd.getOwner(), "", ChatRoomListController.getLocalMainHall()));
		} else {
			sendDisapproveIdentity(cmd.getSocket(), cmd.getOwner());
		}
	}

	private void handleLockIdentiy(Command cmd) {
		String id = (String) cmd.getObj().get(Command.P_IDENTITY);

		if (id != null && !("").equals(id)) {
			lockIdentity(id, cmd.getSocket());
		} else {
			sendDisapproveIdentity(cmd.getSocket(), id);
		}
	}

	private void lockIdentity(String identity, Socket socket) {
		if (!ClientListController.getInstance().isIdentityExist(identity)) {
			connector
					.requestTheOther(InternalCmd.getInternIdCmd(identity, socket, Command.CMD_LOCK_IDENTITY, identity));
			releaseIdentity(identity, socket);
		}
	}

	private void releaseIdentity(String identity, Socket socket) {
		connector.requestTheOther(InternalCmd.getInternIdCmd(identity, socket, Command.CMD_RELEASE_IDENTITY, identity));
	}

	private void handleMessage(Command cmd) {
		String clientId = cmd.getOwner();
		String content = (String) cmd.getObj().get(Command.P_CONTENT);
		connector.broadcastWithinRoom(null, getCurrentRoomId(clientId), ClientServerCmd.messageCmd(clientId, content));
	}

	private void handleQuit(Command cmd) {
		if (isOwnerOfRoom(cmd.getOwner())) {
			deleteRoomAndBroadcastRoomChange(cmd.getSocket(), cmd.getOwner());
		}
		removeFromClientList(cmd.getOwner());
		responseChangeRoomAndTerminate(cmd.getSocket(), cmd.getOwner());
	}

	private void handlerMoveJoin(Command cmd) {
		String id = (String) cmd.getObj().get(Command.P_IDENTITY);
		String formerRoom = (String) cmd.getObj().get(Command.P_FORMER);
		String roomId = (String) cmd.getObj().get(Command.P_ROOM_ID);

		if (ClientListController.getInstance().isIdentityExist(id)) {
			response(cmd.getSocket(), ClientServerCmd.serverChangeRs(false, Configuration.getServerId()));
			return;
		}
		String newRoom = null;
		if (ChatRoomListController.getInstance().isRoomExists(roomId)) {
			newRoom = roomId;
		} else {
			newRoom = ChatRoomListController.getLocalMainHall();
		}
		createIdentity(id, cmd.getSocket(), newRoom);
		response(cmd.getSocket(), ClientServerCmd.serverChangeRs(true, Configuration.getServerId()));
		connector.broadcastWithinRoom(null, newRoom, ClientServerCmd.roomChangeRq(id, formerRoom, newRoom));
	}

	private void responseChangeRoomAndTerminate(Socket socket, String clientId) {
		response(socket, ClientServerCmd.roomChangeRq(clientId, "", ""));
		connector.broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
				ClientServerCmd.roomChangeRq(clientId, "", ""));
		terminate(socket);
	}

	private void handleWho(Command cmd) {
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(getCurrentRoomId(cmd.getOwner()));
		response(cmd.getSocket(), ClientServerCmd.whoRs(room.getName(), room.getMemberList(), room.getOwner()));
	}

	private String getCurrentRoomId(String clientId) {
		String ret = null;

		Client client = ClientListController.getInstance().getClient(clientId);
		if (client != null) {
			ret = client.getRoomId();
		}
		return ret;
	}

	private void deleteRoomAndBroadcastRoomChange(Socket socket, String clientId) {
		String roomId = ClientListController.getInstance().getClient(clientId).getOwnRoom();
		if (isRoomCanBeDel(clientId, roomId)) {
			ArrayList<String> currentMemberList = ChatRoomListController.getInstance().getChatRoom(roomId)
					.getMemberList();
			currentMemberList.remove(clientId);
			deleteRoom(roomId);
			broadcastDeleteRoom(socket, clientId, roomId, currentMemberList);
		}
	}

	private boolean isOwnerOfRoom(String clientId) {
		boolean ret = false;
		Client client = ClientListController.getInstance().getClient(clientId);
		if (client != null) {
			ret = client.getOwnRoom() != null;
		}

		return ret;
	}

	private void removeFromClientList(String clientId) {
		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(getCurrentRoomId(clientId));
		if (room != null) {
			room.removeMember(clientId);
		}
		ClientListController.getInstance().removeIndentity(clientId);
	}

	private void broadcastDeleteRoom(Socket socket, String clientId, String roomId,
			ArrayList<String> currentMemberList) {
		connector.requestTheOther(InternalCmd.getInternRoomCmd(clientId, socket, Command.CMD_DELETE_ROOM, roomId));

		for (String id : currentMemberList) {
			connector.broadcastWithinRoom(null, ChatRoomListController.getLocalMainHall(),
					ClientServerCmd.roomChangeRq(id, roomId, ChatRoomListController.getLocalMainHall()));
		}
	}

	private void deleteRoom(String roomId) {
		// Change room id in the client list.
		for (String identity : ChatRoomListController.getInstance().getChatRoom(roomId).getMemberList()) {
			ClientListController.getInstance().getClient(identity).setRoomId(ChatRoomListController.getLocalMainHall());
		}
		ChatRoomListController.getInstance().deleteRoom(roomId);
	}

	private void disapproveDeleteRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.deleteRoomRs(roomId, false));
	}

	private void approveDeleteRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.deleteRoomRs(roomId, true));
	}

	private boolean isRoomCanBeDel(String clientId, String roomId) {
		boolean ret = false;

		ChatRoom room = ChatRoomListController.getInstance().getChatRoom(roomId);
		if (room != null && clientId.equals(room.getOwner())) {
			ret = true;
		}
		return ret;
	}

	private void routeClient(String clientId, String roomId, ServerConfig server, Socket socket) {
		String formerRoom = getCurrentRoomId(clientId);
		response(socket, ClientServerCmd.routeRq(roomId, server.getHost(), server.getClientPort()));
		removeFromClientList(clientId);
		connector.broadcastWithinRoom(formerRoom, null, ClientServerCmd.roomChangeRq(clientId, formerRoom, roomId));
		terminate(socket);
	}

	private void terminate(Socket socket) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connector.close(socket);// Will close socket and terminal this thread.
	}

	private ServerConfig getServer(String roomId) {
		return ServerListController.getInstance().get(getRoomServerId(roomId));
	}

	private String getRoomServerId(String roomId) {
		return ChatRoomListController.getInstance().getChatRoom(roomId).getServerId();
	}

	private void disapproveJoin(Socket socket, String clientId, String roomId) {
		response(socket,
				ClientServerCmd.roomChangeRq(clientId, getCurrentRoomId(clientId), getCurrentRoomId(clientId)));
	}

	private void approveJoin(String clientId, String roomId) {
		String former = getCurrentRoomId(clientId);
		ClientListController.getInstance().getClient(clientId).setRoomId(roomId);
		ChatRoomListController.getInstance().changeRoom(former, roomId, clientId);
		broadcastRoomChange(clientId, former, roomId);
	}

	private boolean isRoomAvailable(String roomId) {
		return (ChatRoomListController.getInstance().isRoomExists(roomId));
	}

	private void disapproveChatRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.createRoomRs(roomId, false));
	}

	private void approveChatRoom(Socket socket, String roomId) {
		response(socket, ClientServerCmd.createRoomRs(roomId, true));
	}

	private void createChatRoom(String clientId, String roomId) {
		ChatRoomListController.getInstance().addRoom(roomId, Configuration.getServerId(), clientId);
		ChatRoomListController.getInstance()
				.getChatRoom(ClientListController.getInstance().getClient(clientId).getRoomId()).removeMember(clientId);
		ClientListController.getInstance().getClient(clientId).setRoomId(roomId);
		ClientListController.getInstance().getClient(clientId).setOwnRoom(roomId);
	}

	private void releaseRoomId(Socket socket, String clientId, String roomId, boolean result) {
		connector.requestTheOther(
				InternalCmd.getInternRoomResultCmd(clientId, socket, Command.CMD_RELEASE_ROOM, roomId, result));
	}

	private void handleList(Command cmd) {
		response(cmd.getSocket(), ClientServerCmd.listRs(ChatRoomListController.getInstance().getList()));
	}

	private void sendDisapproveIdentity(Socket socket, String id) {
		response(socket, ClientServerCmd.newIdentityRs(id, false));
	}

	private void approveIdentity(Socket socket, String id) {
		response(socket, ClientServerCmd.newIdentityRs(id, true));
	}

	private void broadcastRoomChange(String clientId, String former, String newRoom) {
		connector.broadcastWithinRoom(former, newRoom, ClientServerCmd.roomChangeRq(clientId, former, newRoom));
	}

	private void createIdentity(String identity, Socket socket, String roomId) {
		ClientListController.getInstance().addIndentity(identity, Configuration.getServerId(), roomId);
		ChatRoomListController.getInstance().getChatRoom(roomId).addMember(identity);
		connector.addBroadcastList(identity, socket);
	}

	protected void response(Socket socket, String cmd) {
		connector.write(socket, cmd);
	}
}
