package projects.tokenRing.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import projects.tokenRing.nodes.messages.tokenMessage;
import projects.tokenRing.nodes.timers.demandeTimer;
import projects.tokenRing.nodes.timers.initTimer;
import projects.tokenRing.nodes.timers.waitTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

enum Status{
	demandeur,
	dedans,
	dehors
}

public class tokenNode extends Node {

	public boolean jeton=false;
	public Status etat=Status.dehors;
	
	public void preStep() {}

	// ATTENTION lorsque init est appel� les liens de communications n'existent pas
	// il faut attend une unit� de temps, avant que les connections soient r�alis�es
	// nous utilisons donc un timer
	
	public void init() {
		(new initTimer()).startRelative(1, this);
		
	}
	
	// Lorsque le timer pr�c�dent expire, la fonction start est appel�e
	// elle correspond ainsi � l'initialisation r�elle du processus

	public void start(){
		if(this.ID==1) this.send(new tokenMessage(this),this.Droite());
		this.initDemande();
	}

	// Cette fonction g�re la r�ception de message
	// Elle est appel�e r�guli�rement m�me si aucun message n'a �t� re�u
	
	public void handleMessages(Inbox inbox) {
	// Test si il y a des messages
	while(inbox.hasNext())
	{
	Message m=inbox.next();
	if(m instanceof tokenMessage) // Si le processus a re�u le jeton 
		{	
		this.jeton=true;
		tokenNode D=this.Droite(); 

		if(this.etat==Status.demandeur){
			this.etat=Status.dedans;	
			// CS
			}
		
		// On renvoit le jeton � droite apr�s 10 unit�s de temps
		(new waitTimer(D)).startRelative(10, this);				 	
		}	}
	}

	
	// cette fonction est appel�e 
	// sur expiration de waitTimer
	// c'est-�-dire juste avant de renvoyer le jeton
	
	public void gereSortieCS(){
		if(this.etat == Status.dedans){
			this.etat=Status.dehors;
			
			// apr�s avoir acc�der � la CS
			// on relance un timer avec un temps al�atoire
			// lorsque ce timer expire
			// le processus redevient demandeur
			
			this.initDemande();
		}
	}
	
	public tokenNode Droite(){
		for(Edge e : this.outgoingConnections)
			if(e.endNode.ID==this.ID%Tools.getNodeList().size()+1)
				return (tokenNode) e.endNode;
		return null;
	}	
	
	public void initDemande(){
		demandeTimer t=new demandeTimer();
		int tps=Math.max((int) ((Math.random()*10000.0)%500.0),1);
		t.startRelative(tps, this);
	}	
	
	public void demande(){
		this.etat=Status.demandeur;
	}
	
	public void neighborhoodChange() {}
	public void postStep() {}
	public void checkRequirements() throws WrongConfigurationException {}
	
	public Color Couleur(){
		if(this.etat==Status.dehors && !this.jeton) return Color.BLUE;
		if(this.etat==Status.dehors && this.jeton) return Color.yellow;		
		if(this.etat==Status.dedans) return Color.red;
		return Color.black;
	}
	
	// affichage du noeud
	
	 public void draw(Graphics g, PositionTransformation pt, boolean highlight){
	 this.setColor(this.Couleur());
	 String text = ""+this.ID;
	 super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
	 }
	
	
}
