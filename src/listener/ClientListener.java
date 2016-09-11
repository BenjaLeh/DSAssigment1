package listener;

import java.net.Socket;

import org.json.simple.JSONObject;

import com.tamfign.command.ClientServerCmd;
import com.tamfign.command.Command;
import com.tamfign.connection.ClientConnector;

public class ClientListener extends CommandListener {
	private String clientId = null;
	private boolean aboutClosed = false;

	public ClientListener(ClientConnector connector, Socket socket) {
		super(connector, socket);
	}

	@Override
	protected void handleDisconnect() {
		// In case of quit again.
		if (!aboutClosed) {
			handleQuit();
		}
		clientId = null;
	}

	private void handleQuit() {
		handleRequest(ClientServerCmd.quit());
	}

	private void catchClientId(JSONObject cmd) {
		if (Command.isNewId(cmd)) {
			this.clientId = Command.getNewId(cmd);
		}
	}

	private void checkIfClosing(JSONObject cmd) {
		if (Command.isClosing(cmd)) {
			aboutClosed = true;
		}
	}

	@Override
	protected void handleRequest(String cmdLine) {
		System.out.println(cmdLine);
		JSONObject cmdObject = Command.getCmdObject(cmdLine);
		catchClientId(cmdObject);
		checkIfClosing(cmdObject);
		((ClientConnector) getConnector()).getMQ().addCmd(new Command(getSocket(), cmdObject, clientId));
	}
}
