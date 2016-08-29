package com.tamfign.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.tamfign.main.ServerArguments;
import com.tamfign.model.ServerListController;

public class Configuration {
	private static Configuration _instance = null;
	private ServerConfig itself = null;

	private Configuration(ServerArguments arguments) throws IOException {
		FileReader reader = new FileReader(arguments.getServerConfigPath());
		BufferedReader br = new BufferedReader(reader);

		String configLine = br.readLine();
		while (configLine != null) {
			ServerConfig server = ServerConfig.getInstance(configLine);
			if (arguments.getServerId() != null && arguments.getServerId().equals(server.getId())) {
				this.itself = server;
				server.setActived(true);
				server.setItselft(true);
			}
			ServerListController.getInstance().addServer(server);
			configLine = br.readLine();
		}
		br.close();

		if (itself == null) {
			throw new IOException("No matched ServerId");
		}
	}

	public static String getServerId() {
		return _instance.itself.getId();
	}

	public static int getClientPort() {
		return _instance.itself.getClientPort();
	}

	public static int getCoordinationPort() {
		return _instance.itself.getCoordinationPort();
	}

	public static Configuration init(ServerArguments arguments) throws IOException {
		if (_instance == null) {
			_instance = new Configuration(arguments);
		}
		return _instance;
	}
}
