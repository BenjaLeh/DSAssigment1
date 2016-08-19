package com.tamfign.configuration;

import java.io.IOException;

public class Server {
	private String id = null;
	private String host = null;
	private int clientPort = 0;
	private int coordinationPort = 0;
	private boolean isItselft = false;
	private boolean isActived = false;

	private Server(String id, String host, String clientPort, String coordinationPort) {
		this.id = id;
		this.host = host;
		this.clientPort = Integer.parseInt(clientPort);
		this.coordinationPort = Integer.parseInt(coordinationPort);
	}

	public static Server getInstance(String configLine) throws IOException {
		String[] configs = configLine.split("\t");
		if (configs.length < 4)
			throw new IOException("Configuration File's Format invalid.");

		return new Server(configs[0], configs[1], configs[2], configs[3]);
	}

	public boolean isItselft() {
		return isItselft;
	}

	public void setItselft(boolean isItselft) {
		this.isItselft = isItselft;
	}

	public String getId() {
		return id;
	}

	public String getHost() {
		return host;
	}

	public int getClientPort() {
		return clientPort;
	}

	public int getCoordinationPort() {
		return coordinationPort;
	}

	public boolean isActived() {
		return isActived;
	}

	public void setActived(boolean isActived) {
		this.isActived = isActived;
	}
}
