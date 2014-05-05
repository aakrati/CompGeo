public class InputWait extends Thread
{
  private GeomCanvas c;
  private CompGeomTest f;
  
  public InputWait(GeomCanvas canv, CompGeomTest frame)
  {
    super("ForInput");		
    c = canv;
    f = frame;
    System.out.println("InputWait called");
  }
  
  public void run()
  {
    waitForEntry();
  }

  public void waitForEntry()
  {		
    while(c.GetQuery()==true)
    {
      while(c.GetAns()=='n') 
      {
	try { 
	  sleep(100);
	}
	catch (InterruptedException ie) {
	  System.out.println("Sleeping thread was interrupted");
	}
      }				 
      f.displayResult(c.GetAns());
      c.SetAns('n');
    }
  }
}
