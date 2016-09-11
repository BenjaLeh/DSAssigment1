package com.tamfign.connection;

import java.io.IOException;
import java.net.Socket;

import com.tamfign.command.Command;

public interface ConnectorInf {

	public abstract boolean broadcastAndGetResult(String cmd);

	public abstract void broadcast(String cmd);

	public abstract void addBroadcastList(String id, Socket socket);

	public abstract void removeBroadcastList(String id);

	public abstract String readCmd(Socket socket) throws IOException;

	public abstract void write(Socket socket, String cmd);

	public abstract void close(Socket socket);

	public abstract void requestTheOther(Command command);
}
