import acm.graphics.*;
import acm.program.*;
import acm.util.RandomGenerator;
import java.applet.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
* Tower defense game!
* Your goal is to destroy enemy planes coming at you.
* Good Luck!
*
* @author  Mehmet Kagan Ilbak
* @version 2018-07-20
*/

public class FinalProject extends GraphicsProgram {

	private static RandomGenerator rg = new RandomGenerator();

	public static final int APPLICATION_WIDTH = 1280;
	public static final int APPLICATION_HEIGHT = 620;
	public static final int START_BUTTON_WIDTH = 630;
	public static final int START_BUTTON_HEIGHT = 200;

	GImage startButton, plane, sidewalk, enemyPlane;
	GCompound environment;
	int bulletCounter = 0, planeCounter=0;
	ArrayList<GRect> bullets = new ArrayList<GRect>();
	ArrayList<GImage> enemyPlanes = new ArrayList<GImage>();
	ArrayList<GImage> enemyPlanesBomb = new ArrayList<GImage>();
	ArrayList<Integer> enemyPlanesHealth = new ArrayList<Integer>();
	int health = 100;
	int bombCounter = 0;
	GLabel healthLabel;
	boolean gameStarted = false; // check if game is started or not. Turns into true when you press start button.
	boolean gameEnded = false;
	
	public void run() {
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT); // Sets size of the screen

		Color skyBlue = new Color(120, 210, 255); // 120 210 255
		setBackground(skyBlue); // Sets background to skyBlue

		addStartButton(); // adds start button
		GCompound environment = city();
		add(environment); // adds environment
		pause(1);
		addMouseListeners();
		addKeyListeners();
		int stopWatch=0;
		while (health>0) {
			pause(1);
			if (gameStarted == true && bullets.size() >= 0) {
				//move bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).move(1, 0);
					if (bullets.get(i).getX() == 1276) {
						remove(bullets.get(i));
						bullets.remove(i);
						bulletCounter--;
					}
				}
				//randomly create enemy planes every 2 seconds
				randomEnemyPlane(2000,stopWatch);
				randomEnemyPlane(2000,stopWatch);
				//move enemy planes if it passes x=0 it gets removed
				for (int i = 0; i < enemyPlanes.size(); i++) {
					enemyPlanes.get(i).move(-0.25, 0);
					if (enemyPlanes.get(i).getX() == 0-enemyPlanes.get(i).getWidth()) {
						remove(enemyPlanes.get(i));
						enemyPlanes.remove(i);
						enemyPlanesHealth.remove(i);
						planeCounter--;
						
					}
					//adds bomb under enemy plane
					if (enemyPlanes.get(i).getX() == 30) {
						enemyPlanesBomb.add(enemyPlanesBomb());
						add(enemyPlanesBomb.get(bombCounter),75,enemyPlanes.get(i).getY()+enemyPlanes.get(i).getHeight()); //
						bombCounter++;								
					}
					//Check if enemy plane hits our plane !!!
					GObject enemyPlaneFront = getElementAt(enemyPlanes.get(i).getX()-1,enemyPlanes.get(i).getY()+15);
					if (enemyPlaneFront == plane) {
						remove(enemyPlanes.get(i));
						enemyPlanes.remove(i);
						planeCounter--;
						health-=10;
						remove(healthLabel);
						add(healthLabel(health),25,570);
					}
				}
				//moves bombs
				for (int k=0;k<enemyPlanesBomb.size();k++) {
					enemyPlanesBomb.get(k).move(0, 0.25);
					double bombY=enemyPlanesBomb.get(k).getY()+9;
					GObject bombTarget = getElementAt(75,bombY+9);
					if (bombTarget == plane) {
						remove(enemyPlanesBomb.get(k));
						enemyPlanesBomb.remove(k);
						bombCounter--;
						health-=10;
						remove(healthLabel);
						add(healthLabel(health),25,570);
					}					
				}
				
				//reduces Enemy Plane's Health if bullet hits it
				for (int i = 0; i < bullets.size(); i++) {
					GObject bulletFront = getElementAt(bullets.get(i).getX()+5,plane.getY() + 49/*plane cannon position*/);
					for (int j=0;j<enemyPlanes.size();j++) {
						if (bulletFront == enemyPlanes.get(j)) {
							if (enemyPlanesHealth.get(j)>1) {
								remove(bullets.get(i));
								bullets.remove(i);
								bulletCounter--;
								enemyPlanesHealth.set(j, enemyPlanesHealth.get(j)-1);
								}
							else if (enemyPlanesHealth.get(j)==1) {
								remove(bullets.get(i));
								bullets.remove(i);
								bulletCounter--;
								remove(enemyPlanes.get(j));
								enemyPlanes.remove(j);
								enemyPlanesHealth.remove(j);
								planeCounter--;
							}							
						}
					}
				}				
			}
			stopWatch++;
		}
		//add finish things here
		GImage blast = new GImage("blast.gif");
		blast.setSize(125,100);
		add(blast,plane.getX(),plane.getY());
		pause(1500);
		removeAll();
		GLabel gameOver = new GLabel("GAME OVER");
		gameOver.setColor(Color.WHITE);
		gameOver.setFont("SansSerif-72");
		add(gameOver,430,300);
		gameEnded = true;
	}

	//Actions to do when user clicks with mouse
	public void mouseClicked(MouseEvent e) {
		if (gameStarted == true && gameEnded == false) {
			double planeCannonPositionX = plane.getX() + 105;
			double planeCannonPositionY = plane.getY() + 37;
			bullets.add(cannon20());
			pause(1);
			add(bullets.get(bulletCounter), planeCannonPositionX, planeCannonPositionY);
			bulletCounter++;
		}

		// checks if you clicked at startButton
		GObject checkStart = getElementAt(e.getX(), e.getY());
		if (checkStart == startButton && gameStarted == false) {
			remove(startButton);
			gameStarted = true;
			addPlane();
			GLabel healthLabel = healthLabel(health);
			add(healthLabel,25,570);
			plane.sendToFront();
		}
	}

	//Actions to do when user moves the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		if (gameStarted == true && gameEnded == false) {
			if (plane.getY() >= 0 && plane.getY() + plane.getHeight() <= sidewalk.getY()) {
				plane.setLocation(plane.getX(), mouseY - plane.getHeight() / 2);
			}
			if (plane.getY() + plane.getHeight() > sidewalk.getY() || plane.getY() <= 0) {
				plane.setLocation(30, 270);
			}
		}
	}
	int rapidCounter=0;

	//Actions to do when user drags the mouse
	public void mouseDragged(MouseEvent e) {
		if (gameStarted == true && gameEnded == false) {
			//move while dragged
			double mouseY = e.getY();
			if (gameStarted == true && gameEnded == false) {
				if (plane.getY() >= 0 && plane.getY() + plane.getHeight() <= sidewalk.getY()) {
					plane.setLocation(plane.getX(), mouseY - plane.getHeight() / 2);
				}
				if (plane.getY() + plane.getHeight() > sidewalk.getY() || plane.getY() <= 0) {
					plane.setLocation(30, 270);
				}
			}
		}
		rapidCounter++;
	}
	
	//Actions to do when user left clicks with mouse
	int rapidCounter2=0;
	public void mousePressed() {
		//RapidFire (doesn't work)
		double planeCannonPositionX = plane.getX() + 97;
		double planeCannonPositionY = plane.getY() + 49;
		pause(1);
		if(rapidCounter2%10==0) {
			bullets.add(cannon20());
			add(bullets.get(bulletCounter), planeCannonPositionX, planeCannonPositionY);
			bulletCounter++;
		}
		rapidCounter2++;
	}
	
	//Adds random enemy plane
	private void randomEnemyPlane(int time, int stopWatch) {
		if(stopWatch%time==0) {
			enemyPlanes.add(addEnemyPlane());
			add(enemyPlanes.get(planeCounter),1190,rg.nextDouble(25,450));
			enemyPlanesHealth.add(3);
			planeCounter++;
		}
	}

	//Adding start button
	private void addStartButton() { //adds start button to center of the screen
		startButton = new GImage("startButton.png");
		double buttonWidth = startButton.getWidth();
		double buttonHeight = startButton.getHeight();
		add(startButton, (APPLICATION_WIDTH - buttonWidth) / 2, (APPLICATION_HEIGHT - buttonHeight) / 2);
	}

	//Adding friendly plane
	private void addPlane() { //adds plane
		plane = new GImage("plane1.png");
		add(plane, 30, 270); // 30,270
	}

	//Adding enemy plane
	private GImage addEnemyPlane() { //adds enemy plane
		enemyPlane = new GImage("enemyPlane.png");
		return enemyPlane;
	}

	//Adding environment
	private GCompound city() { //creates city elements
		GCompound environment = new GCompound();
		sidewalk = new GImage("sidewalk.png");
		environment.add(sidewalk, 0, 620 - sidewalk.getHeight() + 5);
		GImage sun = new GImage("sun.png");
		add(sun, 1065, 10);
		GImage cloud = new GImage("cloud.png");
		GImage cloud2 = new GImage("cloud.png");
		GImage cloud3 = new GImage("cloud.png");
		GImage cloud4 = new GImage("cloud.png");
		GImage cloud5 = new GImage("cloud.png");
		GImage cloud6 = new GImage("cloud.png");
		GImage cloud7 = new GImage("cloud.png");
		environment.add(cloud, 100, 10);
		environment.add(cloud2, 250, 10);
		environment.add(cloud3, 400, 10);
		environment.add(cloud4, 550, 10);
		environment.add(cloud5, 700, 10);
		environment.add(cloud6, 850, 10);
		environment.add(cloud7, 900, 10);
		return environment;
	}

	//Adding cannon
	private GRect cannon20() {
		GRect cannon = new GRect(4, 1);
		cannon.setFilled(true);
		return cannon;
	}
	
	//Adding health bar
	private GLabel healthLabel(int health) {
		healthLabel = new GLabel("Health = "+health);
		healthLabel.setFont("SansSerif-28");
		healthLabel.setColor(Color.ORANGE);
		return healthLabel;
	}
	
	//adding enemy plane's bomb
	private GImage enemyPlanesBomb() {
		GImage bomb = new GImage("enemyPlaneBomb.png");
		return bomb;
	}
}