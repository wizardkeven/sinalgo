package projects.walker.nodes.messages;

import sinalgo.nodes.messages.Message;

public class WalkerMessage extends Message {

	private static int msgCounter = 0;
	private int msgId;
	public WalkerMessage() {
		super();
		msgId = msgCounter;
		msgCounter++;
	}

	public Message clone() {
		return this;
	}
	
	public String toString() {
		return "walker" + msgId;
	}
}