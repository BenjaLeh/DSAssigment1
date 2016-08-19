package com.tamfign.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.tamfign.main.ServerArguments;

public class Configuration {
	private static Configuration _instance = null;
	private ArrayList<Server> serverList = null;

	private Configuration(ServerArguments arguments) throws IOException {
		FileReader reader = new FileReader(arguments.getServerConfigPath());
		BufferedReader br = new BufferedReader(reader);

		String configLine = br.readLine();
		while (configLine != null) {
			Server server = Server.getInstance(configLine);
			if (arguments.getServerId() != null && arguments.getServerId().equals(server.getId())) {
				server.setActived(true);
				server.setItselft(true);
			}
			serverList.add(server);
		}
		br.close();
	}

	public static Configuration getInstance(ServerArguments arguments) throws IOException {
		if (_instance != null) {
			_instance = new Configuration(arguments);
		}
		return _instance;
	}

	public ArrayList<Server> getServerList() {
		return serverList;
	}
}
