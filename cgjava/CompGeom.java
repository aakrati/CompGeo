import java.applet.*;
import java.awt.*;
import java.applet.AppletStub;

public class CompGeom extends Applet
{
  private Button start;
  private CompGeomTest test;
  private int w,h;
  
  public void init()
  {
    start=new Button("Push to start the CompGeom...");
    w = Integer.parseInt(getParameter("frame width"));
    h = Integer.parseInt(getParameter("frame height"));
    setLayout(new FlowLayout (FlowLayout.CENTER)); 
    setBackground(Color.white);
    add(start);
  }
  
  public boolean action(Event e, Object o)
  {
    if(e.target == start)
    {
      test = new CompGeomTest(this );
      test.resize(w,h);
      test.setResizable(false);
      test.show();
    }
    return true;
  }
}
















