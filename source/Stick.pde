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


