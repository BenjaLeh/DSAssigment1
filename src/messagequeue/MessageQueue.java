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
		synchronized (queue) {
			this.queue.add(cmd);
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (queue) {
				if (!this.queue.isEmpty()) {
					handleCmd(this.queue.poll());
				}
			}
		}
	}

	private void handleCmd(Command cmd) {
		handler.cmdAnalysis(cmd);
	}
}
