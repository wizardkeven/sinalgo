package projects.tokenRing.nodes.timers;

import projects.tokenRing.nodes.messages.tokenMessage;
import projects.tokenRing.nodes.nodeImplementations.tokenNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.timers.Timer;

public class waitTimer extends Timer {	
	Node D;
	public waitTimer(Node D){
		this.D=D;
	}
	public void fire() {
		tokenNode n= (tokenNode) this.node;
		n.gereSortieCS();
		n.send(new tokenMessage(this.node),this.D);
		n.jeton=false;
	}

}
