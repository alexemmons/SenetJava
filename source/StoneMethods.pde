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
  
  byte IsStoneClicked()
  { 
    color pixelClicked = get(mouseX,mouseY);
    
    if((pixelClicked == color(204,204,204) || pixelClicked == color(52,52,52)) && sticksThrown)
    {
      if((turn == 1 && pixelClicked == color(52,52,52)) || turn == -1 && (pixelClicked == color(204,204,204)))
      {
        standard.PlaySound("data\\sound\\illegalTurn.wav");
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

  void MakeStones()
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
  
  int TargetToSquare(int t)
  {
    if(t < 10 || t > 19)
      return t;
    return 29 - t;
  }
  
  Stone GetStone(int t) //returns the stone at the given square
  {
    int s = TargetToSquare(t);
    
    Point squarePoint = new Point((int)squares[s].getX() + rectWidth/2,(int)squares[s].getY() + rectHeight/2);
    
    for(int i=0;i<14;i++)
      if(stones[i].x == squarePoint.getX() && stones[i].y == squarePoint.getY())
        return stones[i];
    
    return null;
  }

  int IsOccupied(Stone stone, int target)
  { 
    Stone targetStone = GetStone(target);
    
    if(targetStone == null)
      return 0;
    
    if(stone.original == targetStone.original)
      return 1;
    
    return -1;
  }
  
  void Teleport(Stone s, int square)
  {
    Point squarePoint = new Point((int)squares[square].getX() + rectWidth/2,(int)squares[square].getY() + rectHeight/2);
    
    strokeWeight(5);
    stroke(0,0,255);
    
    line((float)s.x,(float)s.y,(float)squarePoint.getX(),(float)squarePoint.getY());
    
    noStroke();
    
    s.x = (int)squarePoint.getX();
    s.y = (int)squarePoint.getY();
  }
  
  void SwapStones(Stone a, Stone b)
  {
    standard.PlaySound("data\\sound\\swapSound.wav");
    
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
  
  int GetStoneTarget(int squareClicked)
  {
    if(squareClicked < 10 || squareClicked > 19)
      return squareClicked + (displacement/rectWidth);
    else
      return 29 - squareClicked + (displacement/rectWidth);
  }
  
  int GetSquaresToTraverse(int stoneTarget, int squareClicked)
  {
    if(squareClicked < 10 || squareClicked > 19)
      return stoneTarget - squareClicked;
    else
      return displacement/rectWidth;
  }
  
  Point Move(Stone s)
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

  byte DrawStones()
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

  void StoneHandler()
  {
    if(stoneClicked > -1) //if a stone is clicked, unclick it
      stoneClicked = -1;
    else
      stoneClicked = stoneMethods.IsStoneClicked();
  }  
}
