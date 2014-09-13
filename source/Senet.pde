import ddf.minim.*;
import java.awt.Point;

Stone[] stones = new Stone[14];
Point[] squares = new Point[30];
Minim minim;

boolean sticksThrown = false;
boolean clickEnabled = true;

byte stoneClicked = -1; //tells whether or not user is holding a stone, and which stone the user is holding
byte turns = 0;
byte turn = 1;
byte winner = 0;
byte darkPoints = 0;
byte lightPoints = 0;

int rectWidth,rectHeight,squareClicked;
int boardWidth = 850;
int boardLength = 255;
int displacement = 0; //the distance the selected ball will cover, multiplied with the number of turns received
int groundCovered = 0;

Standard standard = new Standard();
StoneMethods stoneMethods = new StoneMethods();
StickMethods stickMethods = new StickMethods();

void setup()
{
  javax.swing.ImageIcon titlebaricon = new javax.swing.ImageIcon(loadBytes("data\\img\\ankhIcon.png"));
  frame.setIconImage(titlebaricon.getImage());

  size(850,755);
  background(232,210,160);
  ellipseMode(CENTER);
  smooth();
  
  minim = new Minim(this);
  
  rectWidth = boardWidth/10;
  rectHeight = boardLength/3;
  
  stoneMethods.MakeStones();
}

void draw()
{
  winner = standard.DrawBoard();
  
  if(winner == 1)
  {
    noLoop();
    standard.GetWinner("lightwins.png");
    standard.PlaySound("data\\sound\\Touching Moments.mp3");
  }
  
  if(winner == -1)
  {
    noLoop();
    standard.GetWinner("darkwins.png");
    standard.PlaySound("data\\sound\\Alchemists Tower.mp3");
  }
}

void mouseClicked()
{
  if(winner == 1 || winner == -1)
  {
    try
    {
        Process p = Runtime.getRuntime().exec("java -jar Senet.jar");
    }
    catch(Exception e){javax.swing.JOptionPane.showMessageDialog(null,"Unable to run welcome menu - Contact Developer");}
   
    System.exit(1);
  }
  
  if(!clickEnabled)
    return;

  stoneMethods.StoneHandler();
  
  if(sticksThrown)
    return;
    
  sticksThrown = stickMethods.StickHandler();
}
