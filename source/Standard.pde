//This class contains a set of standard methods, common to all other classes.

class Standard
{
  byte DrawBoard()
  {
    DrawSquare(0,true,0);
    DrawSquare(1,false,10);
    DrawSquare(2,true,20);
    
    DrawImages();
    DrawDivider();
    
    return stoneMethods.DrawStones();
  }

  void DrawBackground()
  {
    color backgroundColor = color(232,210,160);
    
    for(int i=0;i<850;i++)
      for(int j=boardLength;j<755;j++)
        set(i,j,backgroundColor);
  }  
  
  void PlaySound(String s)
  {
     AudioSample sound = minim.loadSample(s, 2048); 
     sound.trigger();
     
     delay(100);
     
     sound.close();
     minim.stop();     
  }
  
  int GetSquare()
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
  
  byte GetTurns(String r)
  {
    byte t = 0;
    
    for(int i=0;i<r.length();i++)
      if(r.charAt(i) == '0')
        t++;
    
    if(t == 4)
      return 5;
      
    return t;
  }
  
  byte GetScore(color c)
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
  
  int GetDisplacement()
  {
    return (85/2) + (85 * turns);
  }
  
  void GetWinner(String file)
  {
    DrawImage("data\\img\\" + file,0,0);
  }
  
  private void DrawImages()
  {
    DrawImage("data\\img\\ankh.png",5 * rectWidth,rectHeight * 1);
    DrawImage("data\\img\\vulture.png",5 * rectWidth,rectHeight * 2);
    DrawImage("data\\img\\water.png",6 * rectWidth,rectHeight * 2);
    DrawImage("data\\img\\stones.png",7 * rectWidth,rectHeight * 2);
    DrawImage("data\\img\\eye.png",8 * rectWidth,rectHeight * 2);
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
