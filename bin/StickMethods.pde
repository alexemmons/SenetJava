//This class contains a set of methods used on Stick objects.

class StickMethods
{ 
  private void DrawStick(Stick s)
  {
    line(s.ax,s.ay,s.bx,s.by);
  }
  
  byte ThrowSticks()
  {
    standard.DrawBackground();
    strokeWeight(5);
    stroke(63,31,8);
    
    String record = standard.GetRecord();
    
    if(record.indexOf("0") < 0)
      turn*= -1;
    
    standard.PlaySound("data\\sound\\throwSound.wav");
      
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
  
  boolean StickHandler()
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
