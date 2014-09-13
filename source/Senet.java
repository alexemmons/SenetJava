import processing.core.*; 
import processing.xml.*; 

import ddf.minim.*; 
import java.awt.Point; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Senet extends PApplet {




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

public void setup()
{
  javax.swing.ImageIcon titlebaricon = new javax.swing.ImageIcon(loadBytes("data/img/ankhIcon.png"));
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

public void draw()
{
  winner = standard.DrawBoard();
  
  if(winner == 1)
  {
    noLoop();
    standard.GetWinner("lightwins.png");
    standard.PlaySound("data/sound/Touching Moments.mp3");
  }
  
  if(winner == -1)
  {
    noLoop();
    standard.GetWinner("darkwins.png");
    standard.PlaySound("data/sound/Alchemists Tower.mp3");
  }
}

public void mouseClicked()
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
//This class contains a set of standard methods, common to all other classes.

class Standard
{
  public byte DrawBoard()
  {
    DrawSquare(0,true,0);
    DrawSquare(1,false,10);
    DrawSquare(2,true,20);
    
    DrawImages();
    DrawDivider();
    
    return stoneMethods.DrawStones();
  }

  public void DrawBackground()
  {
    int backgroundColor = color(232,210,160);
    
    for(int i=0;i<850;i++)
      for(int j=boardLength;j<755;j++)
        set(i,j,backgroundColor);
  }  
  
  public void PlaySound(String s)
  {
     AudioSample sound = minim.loadSample(s, 2048); 
     sound.trigger();
     
     delay(100);
     
     sound.close();
     minim.stop();     
  }
  
  public int GetSquare()
  {
    int xPos = mouseX/rectWidth;
    
    if(mouseY/rectHeight == 0)
      return xPos;
     if(mouseY/rectHeight == 1)
      return xPos + 10;

    return xPos + 20;
  }
  
  private String GetRecord()
  {
    String record = "";
    
    for(int i=0;i<4;i++)
      record+= round(random(0,1));
      
    return record;    
  }
  
  public byte GetTurns(String r)
  {
    byte t = 0;
    
    for(int i=0;i<r.length();i++)
      if(r.charAt(i) == '0')
        t++;
    
    if(t == 4)
      return 5;
      
    return t;
  }
  
  public byte GetScore(int c)
  {
    byte score = 0;
    
    for(int i=0;i<14;i++)
      if(stones[i].original == c && stones[i].x > 850)
        score++;
    
    return score;
  }
  
  private void DrawDivider()
  {
    strokeWeight(8);
    stroke(212,170,96);
    line(0,boardLength + 4,boardWidth,boardLength + 4);    
  }
  
  public int GetDisplacement()
  {
    return (85/2) + (85 * turns);
  }
  
  public void GetWinner(String file)
  {
    DrawImage("data/img/" + file,0,0);
  }
  
  private void DrawImages()
  {
    DrawImage("data/img/ankh.png",5 * rectWidth,rectHeight * 1);
    DrawImage("data/img/vulture.png",5 * rectWidth,rectHeight * 2);
    DrawImage("data/img/water.png",6 * rectWidth,rectHeight * 2);
    DrawImage("data/img/stones.png",7 * rectWidth,rectHeight * 2);
    DrawImage("data/img/eye.png",8 * rectWidth,rectHeight * 2);
  }

  private void DrawImage(String img,int x, int y)
  {
    PImage pic = loadImage(img);
    image(pic,x,y);     
  }

  private void DrawSquare(int squareRow,boolean darkFirst,int offset)
  {
    noStroke();
    int k;

    if(darkFirst)
      k = 1;
    else
      k = -1;

    for(int i=0;i<10;i++)
    {
      if(k > 0)
        fill(222,184,135);
      else
        fill(245,245,220);
        
      squares[i + offset] = new Point(i*rectWidth,rectHeight * squareRow);
      
      rect(i*rectWidth,rectHeight * squareRow,rectWidth,rectHeight);
      k*=-1;
    }  
  }
}
//This class represents a stick object

class Stick
{
  float ax,ay,bx,by;
  
  public Stick()
  {
    ax = random(20,boardWidth - 20);
    ay = random(boardLength + 20,735);
    bx = random(20,boardWidth - 20);
    by = random(boardLength + 20,735);
  }
}


//This class contains a set of methods used on Stick objects.

class StickMethods
{ 
  private void DrawStick(Stick s)
  {
    line(s.ax,s.ay,s.bx,s.by);
  }
  
  public byte ThrowSticks()
  {
    standard.DrawBackground();
    strokeWeight(5);
    stroke(63,31,8);
    
    String record = standard.GetRecord();
    
    if(record.indexOf("0") < 0)
      turn*= -1;
    
    standard.PlaySound("data/sound/throwSound.wav");
      
    for(int i=0;i<4;i++)
    {
      Stick s = new Stick();
      
      if(record.charAt(i) == '0')
        stroke(63,31,8);
      else
        stroke(255);
      
      DrawStick(s);
    }
    
    return standard.GetTurns(record);
  }
  
  public boolean StickHandler()
  {
    if(mouseY < boardLength + 9) //if mouse is not clicked outside game board
      return sticksThrown;
      
    turns = ThrowSticks();
    
    if(turns == 0)
      return false;
    
    displacement = standard.GetDisplacement();
    
    return true;
  }  
}
//This class represents a Stone object

class Stone
{
  int x,y,w,h;
  int col,travelingColor,original;
  
  public Stone(int _x,int _y)
  {
    x = _x;
    y = _y;
    w = 40;
    h = 40;
    
    original = color(52,52,52);
    travelingColor = color(100,52,52,52);
    col = original;
  }
  
  public void SetColor(int c)
  {
    col = c;
    original = col;
  }
  
  public void trans()
  {
    col = travelingColor;
  }
  
  public void untrans()
  {
    col = original;
  }
}


//This class contains a set of methods used on Stone objects.

class StoneMethods
{ 
  private byte GetStoneClicked()
  {
    int d = (int)dist(stones[0].x,stones[0].y,mouseX,mouseY);
    
    stoneClicked = 0; 
    
    /*
     if we entered this method we know we have clicked a stone, but we don't know which one yet, so we default to 0
     if we did not default to 0, clicking the leftmost stone would have no effect, as xd would never be less than d in the for loop, and stoneClicked would not be set to anything
    */
    
    for(byte i=0;i<14;i++)
    {
      int xd = (int)dist(stones[i].x,stones[i].y,mouseX,mouseY);
      
      if(xd < d)
      {
        d = xd;
        stoneClicked = i;
      }
      
    }
    
    return stoneClicked;
  }
  
  public byte IsStoneClicked()
  { 
    int pixelClicked = get(mouseX,mouseY);
    
    if((pixelClicked == color(204,204,204) || pixelClicked == color(52,52,52)) && sticksThrown)
    {
      if((turn == 1 && pixelClicked == color(52,52,52)) || turn == -1 && (pixelClicked == color(204,204,204)))
      {
        standard.PlaySound("data/sound/illegalTurn.wav");
        sticksThrown = true;
        return -1;
      }
      
      turn*= -1; //switch turn
      sticksThrown = false;   
      groundCovered = 0;
      
      squareClicked = standard.GetSquare();
      
      return GetStoneClicked();
    }
    
    return -1;
  }

  public void MakeStones()
  {
    for(int i=0;i<10;i++)
    {
      stones[i] = new Stone((85/2) + (85 * i),85/2);

      if(i % 2 == 0)
        stones[i].SetColor(color(204,204,204));
    }

    for(int i=10;i<14;i++)
    {
      stones[i] = new Stone((85/2) + (85 * i) - (85*4),85/2 + 85);

      if(i % 2 != 0)
        stones[i].SetColor(color(204,204,204));
    }
  }
  
  public int TargetToSquare(int t)
  {
    if(t < 10 || t > 19)
      return t;
    return 29 - t;
  }
  
  public Stone GetStone(int t) //returns the stone at the given square
  {
    int s = TargetToSquare(t);
    
    Point squarePoint = new Point((int)squares[s].getX() + rectWidth/2,(int)squares[s].getY() + rectHeight/2);
    
    for(int i=0;i<14;i++)
      if(stones[i].x == squarePoint.getX() && stones[i].y == squarePoint.getY())
        return stones[i];
    
    return null;
  }

  public int IsOccupied(Stone stone, int target)
  { 
    Stone targetStone = GetStone(target);
    
    if(targetStone == null)
      return 0;
    
    if(stone.original == targetStone.original)
      return 1;
    
    return -1;
  }
  
  public void Teleport(Stone s, int square)
  {
    Point squarePoint = new Point((int)squares[square].getX() + rectWidth/2,(int)squares[square].getY() + rectHeight/2);
    
    strokeWeight(5);
    stroke(0,0,255);
    
    line((float)s.x,(float)s.y,(float)squarePoint.getX(),(float)squarePoint.getY());
    
    noStroke();
    
    s.x = (int)squarePoint.getX();
    s.y = (int)squarePoint.getY();
  }
  
  public void SwapStones(Stone a, Stone b)
  {
    standard.PlaySound("data/sound/swapSound.wav");
    
    strokeWeight(5);
    stroke(255,0,0);
    line(a.x,a.y,b.x,b.y);
    noStroke();   
    
    int tempX = a.x;
    int tempY = a.y;
    
    a.x = b.x;
    a.y = b.y;
    b.x = tempX;
    b.y = tempY;
  }
  
  public int GetStoneTarget(int squareClicked)
  {
    if(squareClicked < 10 || squareClicked > 19)
      return squareClicked + (displacement/rectWidth);
    else
      return 29 - squareClicked + (displacement/rectWidth);
  }
  
  public int GetSquaresToTraverse(int stoneTarget, int squareClicked)
  {
    if(squareClicked < 10 || squareClicked > 19)
      return stoneTarget - squareClicked;
    else
      return displacement/rectWidth;
  }
  
  public Point Move(Stone s)
  {
    int stoneTarget = GetStoneTarget(squareClicked);
    int squaresToTraverse = GetSquaresToTraverse(stoneTarget,squareClicked);
   
   if( (squareClicked == 28 && squaresToTraverse != 2) || (squareClicked == 27 && squaresToTraverse != 3) ) //stone is trapped in House of Re-Atoum or Three Truths!
     return new Point(0,0);
   
   if(stoneTarget > 29)
     return new Point(1,0);
   
    clickEnabled = false;
    
    s.trans();
    
    int targetOccupied = IsOccupied(s,stoneTarget);
    
    if(targetOccupied == 1)
    {
      clickEnabled = true;
      return new Point(0,0);
    }
    
    if(targetOccupied == -1)
    {
      SwapStones(s,GetStone(stoneTarget));
      clickEnabled = true;
      return new Point(0,0);
    }
   
    if(stoneTarget < 10)
    {
      if(s.x < displacement + (squareClicked * 85))
        return new java.awt.Point(1,0);
    }
    
    if(stoneTarget > 9 && stoneTarget < 20 && groundCovered < squaresToTraverse * 85)
    { 
      if(s.x < 807 && s.y < (85/2) + (85)) //moving from 0 to 9
      {
        groundCovered++;
        return new java.awt.Point(1,0);
      }
        
      if(s.x == 807 && s.y < (85/2) + (85)) //moving from 9 to 10
      {
        groundCovered++;
        return new java.awt.Point(0,1);
      }
      
      groundCovered++;
      
      return new java.awt.Point(-1,0);
    }
    
    if(stoneTarget > 19 && groundCovered < squaresToTraverse * 85)
    {
      if(stoneTarget > 25 && squareClicked < 25) //house of happiness
      {
        Point squarePoint = new Point((int)squares[25].getX() + rectWidth/2,(int)squares[25].getY() + rectHeight/2);
        
        clickEnabled = true;
        
        targetOccupied = IsOccupied(s,25);
    
        if(targetOccupied == 1)
          return new Point(0,0);
    
        if(targetOccupied == -1)
        {
          SwapStones(s,GetStone(25));
          return new Point(0,0);
        }        
        
        if(s.x < squarePoint.getX())
          return new Point(1,0);
        return new Point(0,0);
      }
      
      if(stoneTarget == 26)
      {
        Point squarePoint = new Point((int)squares[26].getX() + rectWidth/2,(int)squares[26].getY() + rectHeight/2);
        
        clickEnabled = true;
  
        targetOccupied = IsOccupied(s,14); //must insert target # here, as opposed to square #.
  
        if(targetOccupied == 1)
          return new Point(0,0);

        if(targetOccupied == -1)
        {
          if(s.x < squarePoint.getX() - 1)
            return new Point(1,0);
            
          SwapStones(s,GetStone(14)); //inserting target # for same reason as above
          return new Point(0,0);
        }
        
        if(targetOccupied == 0)
        {
          if(s.x < squarePoint.getX() - 1)
            return new Point(1,0);
          Teleport(s,15);          
        }
        
       return new Point(0,0);
      }
      
      if(s.x > 42 && s.y < (85/2) + (85 * 2)) //moving from 10 to 19
      {
        groundCovered++;
        return new java.awt.Point(-1,0);
      }
        
      if(s.x == 42 && s.y < (85/2) + (85 * 2)) //moving from 19 to 20
      {
        groundCovered++;
        return new java.awt.Point(0,1);
      }
      
      groundCovered++;
      
      return new java.awt.Point(1,0);    
    }
    
    clickEnabled = true;
    
    return new java.awt.Point(0,0);
  }

  public byte DrawStones()
  {
    noStroke();
    
    for(int i=0;i<14;i++)
    {
      Stone s = stones[i];
      fill(s.col);
      
      if(i != stoneClicked)
        ellipse(s.x,s.y,s.w,s.h);
      else
      {
        java.awt.Point p = Move(s);
        
        if(p.getX() == 0 && p.getY() == 0)
          s.untrans();
          
        lightPoints = standard.GetScore(color(204,204,204));
        darkPoints = standard.GetScore(color(52,52,52));
        
        if(darkPoints == 7)
          return -1;
        if(lightPoints == 7)
          return 1;

        ellipse((float)(s.x+=p.getX()),(float)(s.y+= + p.getY()),s.w,s.h);
      }
    }
    return 0;
  }

  public void StoneHandler()
  {
    if(stoneClicked > -1) //if a stone is clicked, unclick it
      stoneClicked = -1;
    else
      stoneClicked = stoneMethods.IsStoneClicked();
  }  
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#E0DFE3", "Senet" });
  }
}
