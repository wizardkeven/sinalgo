package projects.walker.nodes.nodeImplementations;

import java.awt.Color;
import java.util.Random;
import projects.walker.nodes.messages.WalkerMessage;
import sinalgo.nodes.Node;

public class WalkerNode extends sinalgo.nodes.Node {

	/* WalkerNode() { 
	 *   // no constructor code, it breaks the way sinalgo builds the nodes. 
	 *   // instead use the init() method 
	 * }
	 * */
	public void init() {
		setColor(Color.YELLOW);
	}
		
	public String toString() {
		return " " + ID + " "; 
	}

	public void handleMessages(sinalgo.nodes.messages.Inbox inbox) {
		while(inbox.hasNext()) {
			sinalgo.nodes.messages.Message msg = inbox.next();
			if (msg instanceof WalkerMessage) {
				WalkerMessage walker = (WalkerMessage) msg;
				Node next = randomWalkChoice(outgoingConnections);		
				send(walker, next);
				System.out.println(this + " received message " + walker + 
								   " and sends it now to " + next);
			}
		}
	}
	
	private static Random random = new Random();
	Node randomWalkChoice(sinalgo.nodes.Connections neighbors) {
		int degree = neighbors.size();
		if (degree == 0) throw new RuntimeException("no neighbor");
		int positionOfNext = random.nextInt(degree);
		sinalgo.tools.storage.ReusableListIterator<sinalgo.nodes.edges.Edge> iter 
			= neighbors.iterator();
		Node node = iter.next().endNode;
		for (int i = 1; i <= positionOfNext; i++) node = iter.next().endNode;
		return node;
	}

	
	public void preStep() {};
	public void neighborhoodChange() {};
	public void postStep() {}; 
	public void checkRequirements() throws sinalgo.configuration.WrongConfigurationException {};
	public void draw(java.awt.Graphics g, sinalgo.gui.transformation.PositionTransformation pt, 
					 boolean highlight) {
		// draw the node as a circle with the text inside
		super.drawNodeAsDiskWithText(g, pt, highlight, toString(), 20, Color.black);
	}
}