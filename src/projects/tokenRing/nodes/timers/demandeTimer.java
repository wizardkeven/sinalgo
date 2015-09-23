package projects.tokenRing.nodes.timers;


import projects.tokenRing.nodes.nodeImplementations.tokenNode;
import sinalgo.nodes.timers.Timer;

public class demandeTimer extends Timer {
	
	public void fire() {
		tokenNode n= (tokenNode) this.node;
		n.demande();
	}
}
