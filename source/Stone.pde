//This class represents a Stone object

class Stone
{
  int x,y,w,h;
  color col,travelingColor,original;
  
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
  
  void SetColor(color c)
  {
    col = c;
    original = col;
  }
  
  void trans()
  {
    col = travelingColor;
  }
  
  void untrans()
  {
    col = original;
  }
}


