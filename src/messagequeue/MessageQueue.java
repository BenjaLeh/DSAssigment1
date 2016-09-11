package messagequeue;

import java.util.concurrent.LinkedBlockingQueue;

import com.tamfign.command.CmdHandler;
import com.tamfign.command.Command;

public class MessageQueue implements Runnable {
	private LinkedBlockingQueue<Command> queue = null;
	private CmdHandler handler = null;

	public MessageQueue(CmdHandler handler) {
		this.queue = new LinkedBlockingQueue<Command>();
		this.handler = handler;
	}

	public void addCmd(Command cmd) {
		this.queue.add(cmd);
	}

	@Override
	public void run() {
		while (true) {
			handleCmd(takeCommand());
		}
	}

	private Command takeCommand() {
		Command ret = null;
		try {
			ret = this.queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void handleCmd(Command cmd) {
		handler.cmdAnalysis(cmd);
	}
}
