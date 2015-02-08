/* TODO */

package projects.walker;

import java.util.ArrayList;

import projects.walker.nodes.nodeImplementations.*;
import sinalgo.nodes.Node;
import sinalgo.runtime.AbstractCustomGlobal;

/**
 * This class holds customized global state and methods for the framework. 
 * The only mandatory method to overwrite is 
 * <code>hasTerminated</code>
 * <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 * @see sinalgo.runtime.AbstractCustomGlobal for more details.
 * <br>
 * In addition, this class also provides the possibility to extend the framework with
 * custom methods that can be called either through the menu or via a button that is
 * added to the GUI. 
 */
public class CustomGlobal extends AbstractCustomGlobal{
	
	/* (non-Javadoc)
	 * @see runtime.AbstractCustomGlobal#hasTerminated()
	 */
	public boolean hasTerminated() {
		return false;
	}

	/** Button to create a grid network. */
	@AbstractCustomGlobal.CustomButton(buttonText="Build Grid Network")
	public void gridButton() {
		int length = 6;
		int width = 10;
		buildGrid(length, width);
	}

	/** Button to create a star network. */
	@AbstractCustomGlobal.CustomButton(buttonText="Build Star Network")
	public void starButton() {
		int stages = 4;
		int degree = 5;
		buildStar(degree, stages);
	}

	/** Button to create a tree network. */
	@AbstractCustomGlobal.CustomButton(buttonText="Build a Tree Network")
	public void treeButton() {
/*		int stages = Integer.parseInt(sinalgo.tools.Tools.showQueryDialog("Number of stages:"));
		if(stages <= 2) {
			sinalgo.tools.Tools.showMessageDialog("The number of stages needs to be at least" +
												  "3.\nCreation of tree aborted.");
			return; 
		}
		
		int degree = Integer.parseInt(sinalgo.tools.Tools.showQueryDialog("Degree of the sink:"));
		if(degree <= 0) {
			sinalgo.tools.Tools.showMessageDialog("The degree of the sink needs to be at least" +
												  "1.\nCreation of tree aborted.");
			return; 
		}
		buildTree(degree, stages);
*/ 
		buildTree(5, 6);
	}

	/** Button to create a ring network. */
	@AbstractCustomGlobal.CustomButton(buttonText="Build a Ring Network")
	public void ringButton() {
		buildRing(15);
	}

	private void addEdge(Node from, Node to) {
		from.addConnectionTo(to);
		to.addConnectionTo(from);
	}

	private void buildGrid(int length, int width) {		
		sinalgo.tools.Tools.removeAllNodes();
		if (length <= 0 || width <= 0) 
			throw new RuntimeException("negative length or width for grid");

		//size of the screen to print the nodes : 
		// sinalgo.configuration.Configuration.dimX (same with dimY)		
		
		// nodes
		sinalgo.tools.Tools.removeAllNodes();
		WalkerNode [][] theNodes = new WalkerNode[length][width];
				
		double deltaX = sinalgo.configuration.Configuration.dimX / (length + 1);
		double deltaY = sinalgo.configuration.Configuration.dimY / (width + 1);
		double posX = deltaX;
		for (int x = 0; x < length; x++) {
			double posY = deltaY;
			for (int y = 0; y < width; y++) {
				WalkerNode node;
				if (x == 0 && y == 0) { 
					node = new InitNode();
				}
				else if (x == length - 1 && y == width - 1) { 
					node = new EndNode();
				}
				else node = new WalkerNode();
				node.setPosition(posX, posY, 0);
				node.finishInitializationWithDefaultModels(true);				
				theNodes[x][y] = node;
				posY += deltaY;
			}
			posX += deltaX;
		}
		
		// connections
		for (int x = 0; x < length; x++) { 
			for (int y = 1; y < width; y++) 
				addEdge(theNodes[x][y - 1], theNodes[x][y]); 
		}
		for (int y = 0; y < width; y++) { 
			for (int x = 1; x < length; x++) 
				addEdge(theNodes[x - 1][y], theNodes[x][y]); 
		}

		// Repaint the GUI as we have added some nodes
		sinalgo.tools.Tools.repaintGUI();
	}

	private void buildStar(int degree, int numStages) {
		sinalgo.tools.Tools.removeAllNodes();		

		// nodes
		WalkerNode [][] theNodes = new WalkerNode[degree][numStages];
		WalkerNode center;
		
		// the center
		double centerPosX = sinalgo.configuration.Configuration.dimX / 2;
		double centerPosY = sinalgo.configuration.Configuration.dimY / 2;
		center = new WalkerNode();
		center.setPosition(centerPosX, centerPosY, 0);
		center.finishInitializationWithDefaultModels(true);
		
		// the star...
		double initAngle = 2 * Math.PI / degree;
		double initRange = 100.0;		
		double range = 0.0;
		WalkerNode node = center;
		for(int i = 1; i <= numStages; i++){
			range += initRange;
			double angle = 0;
			for(int j = 0; j < degree; j++){
				double posX = centerPosX + range * Math.cos(angle);
				double posY = centerPosY + range * Math.sin(angle);
				if (i ==  numStages && j == 0) node = new InitNode();
				else if (i ==  numStages && j == degree / 2) node = new EndNode();
				else node = new WalkerNode();
				node.setPosition(posX, posY, 0);
				node.finishInitializationWithDefaultModels(true);
				theNodes[j][i-1] = node;
				angle += initAngle;
			}
		}
		
		// connections
		for(int j=0; j<degree; j++) { // per branch
			addEdge(center, theNodes[j][0]);
			for(int i=1; i<numStages; i++) {
				addEdge(theNodes[j][i-1], theNodes[j][i]);
			}
		}
		for(int j=0; j<degree; j++) { 
			// stage 1
			addEdge(theNodes[j][0], theNodes[(j+1)%degree][0]);
			addEdge(theNodes[j][0], theNodes[(j+2)%degree][0]);
			addEdge(theNodes[j][1], theNodes[(j+1)%degree][0]);
			addEdge(theNodes[(j+1)%degree][1], theNodes[(j)%degree][0]);
		}
		
		// Repaint the GUI as we have added some nodes
		sinalgo.tools.Tools.repaintGUI();
	}

	private void buildTree(int degree, int numStages) {
		sinalgo.tools.Tools.removeAllNodes();
		ArrayList<WalkerNode> nodes = new ArrayList<WalkerNode>();

		// the root
		double posX = sinalgo.configuration.Configuration.dimX / 2;
		double posY = 50;
		WalkerNode node = new WalkerNode();
		node.setPosition(posX, posY, 0);
		node.finishInitializationWithDefaultModels(true);
		nodes.add(node);
		
		double initPosX = sinalgo.configuration.Configuration.dimX / (degree + 1);
		for(int i = 1; i < numStages; i++){			
			posY += 100;
			posX = initPosX;
			for(int j = 0; j < degree; j++){	
				if (i == numStages - 1 && j == 0) node = new InitNode();
				else if (i == numStages -1 && j == degree - 1) node = new EndNode();
				else node = new WalkerNode();
				node.setPosition(posX, posY, 0);
				node.finishInitializationWithDefaultModels(true);
				if (i == 1) addEdge(nodes.get(0), node);
				else addEdge(nodes.get(nodes.size() - degree), node);
				nodes.add(node);
				posX += initPosX;
			}
		}
		// Repaint the GUI as we have added some nodes
		sinalgo.tools.Tools.repaintGUI();
	}

	private void buildRing(int numOfNodes) {
		sinalgo.tools.Tools.removeAllNodes();		

		// nodes
		WalkerNode [] theNodes = new WalkerNode[numOfNodes];
	
		// the center
		double centerPosX = sinalgo.configuration.Configuration.dimX / 2;
		double centerPosY = sinalgo.configuration.Configuration.dimY / 2;
	
		// the ring...
		double initAngle = 2 * Math.PI / numOfNodes;
		double range = 200.0;
		double angle = 0;
		for(int i = 0; i < numOfNodes; i++){
			double posX = centerPosX + range * Math.cos(angle);
			double posY = centerPosY + range * Math.sin(angle);
			WalkerNode node;
			if (i ==  0) node = new InitNode();
			else if (i ==  numOfNodes / 2) node = new EndNode();
			else node = new WalkerNode();
			node.setPosition(posX, posY, 0);
			node.finishInitializationWithDefaultModels(true);
			if (i > 0) addEdge(theNodes[i - 1], node);
			theNodes[i] = node;
			angle += initAngle;
		}
		addEdge(theNodes[0], theNodes[numOfNodes - 1]);
		// Repaint the GUI as we have added some nodes
		sinalgo.tools.Tools.repaintGUI();
	}
}