import java.awt.*; 
import java.applet.*;
import java.io.*;
import java.net.*;

public class CompGeomTest extends Frame
{
  private Button ready,clear,delete,add,move, armmove;
  private Button bprev, bdummy;        //for reference in resetBLabel()
  private Panel subp0, subp1, subp022, subp02, subp2, subp3, subp4, subp44,
                subp5, subp6, subp56, p, p1, canvasp;
  private Label loutput, lregime, lnumpoly, ledit;
  private TextArea output;
  private Checkbox xpoly, xpoint, xsegm, xchain, x2poly;
  private CheckboxGroup regime;
  private Checkbox first, second;
  private CheckboxGroup polynum;
  private GeomCanvas c;
  private InputWait th;
  private String answer = new String();
  private String on = new String("on the edge of the polygon.");
  private String in = new String("inside the polygon.");
  private String out = new String("outside the polygon.");
  private String vert = new String("a vertex of the polygon.");
  private MenuBar mbar;
  private Menu close, operations, help;
  private MenuItem term;
  private MenuItem area, tri, inpoly, segseg, chull, delTri, poly2inters, mink, oarmmove;
  private Menu hregime, hoperations, hedit, happlications;
  private MenuItem hgeneral, hrgeneral,hrpoly, hrpoints, hrsegments, hrchain, hr2poly, 
                   hogeneral, hoarea, hoinpoly, hoseg, hotriang, hochull2d, hageneral,
                   hodelaunay, ho2inters, homink, hachull3d, haptinpoly, haptsonsphere,
                   hegeneral,hefinish, heclear, hedelete, headd, hemove, hemovearm,
                   hoarmmove;
  private int w,h,cw,ch; //width and height for info frame and canvas
  private boolean thread = false;
  private boolean ischain=false;
  private Font f;
  private HelpFrame hf;

  public CompGeomTest(Applet a)
  {
      super("Computational Geometry in C: Implemented Algorithms");
      setBackground(Color.lightGray);

      w = Integer.parseInt(a.getParameter("info width"));
      h = Integer.parseInt(a.getParameter("info height"));
      cw = Integer.parseInt(a.getParameter("canvas width"));
      ch = Integer.parseInt(a.getParameter("canvas height"));
      
      c = new GeomCanvas(cw,ch,this);
      System.out.println("New GeomCanvas is created");
      p = new Panel();
      p.setBackground(Color.lightGray);
      p1 = new Panel();
      p1.setBackground(Color.lightGray);

      subp1 = new Panel();
      subp022 = new Panel();
      subp02 = new Panel();
      subp2 = new Panel();
      subp3 = new Panel();
      subp4 = new Panel();
      subp5 = new Panel();
      subp6 = new Panel();
      canvasp = new Panel();
	   
      ready = new Button("Finish polygon");	   
      clear = new Button("Clear");
      delete = new Button("Delete vertex");
      add = new Button("Add vertex");
      armmove = new Button("Move arm");
      armmove.disable();
      move = new Button("Move vertex");

      operations = new Menu("Operations");
      area = new  MenuItem("Area/Centroid");
      tri = new  MenuItem("Triangulate");
      inpoly = new  MenuItem ("In Poly?");
      segseg = new  MenuItem ("Seg-Seg Int");
      chull = new  MenuItem("Convex Hull 2D");
      delTri = new MenuItem("Delaunay Triangulation");
      poly2inters = new MenuItem("Inter of 2 Conv Polys");
      mink = new  MenuItem("Minkowski Convolution");
      oarmmove = new MenuItem ("Move Arm");
      help = new Menu ("Help");
      hgeneral = new MenuItem ("General");
      hregime = new Menu ("Regime");
      
      hrgeneral = new MenuItem("General");
      hrpoly = new MenuItem ("Polygon");
      hrpoints = new MenuItem ("Points");
      hrsegments = new MenuItem ("Segments");
      hrchain = new MenuItem ("Chain");
      hr2poly = new MenuItem ("2 Polygons");
      hregime.add(hrgeneral);
      hregime.add(new MenuItem("-"));
      hregime.add(hrpoly);
      hregime.add(hrpoints);
      hregime.add(hrsegments);
      hregime.add(hrchain);
      hregime.add(hr2poly);

      hoperations = new Menu ("Operations");
      hogeneral = new MenuItem ("General");
      hoarea = new MenuItem ("Area/Centroid");
      hoinpoly = new MenuItem ("In Poly");
      hoseg = new MenuItem ("SegSegInt");
      hotriang = new MenuItem ("Triangulate");
      hochull2d = new MenuItem ("Convex Hull in 2D");
      hodelaunay = new MenuItem ("Delaunay Triangulation"); 
      ho2inters = new MenuItem ("Inters. of 2 Polygons"); 
      homink = new MenuItem ("Minkowski Convolution");
      hoarmmove = new MenuItem("Move Arm");
      hoperations.add(hogeneral);
      hoperations.add(new MenuItem("-"));
      hoperations.add(hoarea);
      hoperations.add(hotriang);
      hoperations.add(hochull2d);
      hoperations.add(hodelaunay);
      hoperations.add(hoseg);
      hoperations.add(hoinpoly);
      hoperations.add(homink);
      hoperations.add(hoarmmove);

      hedit = new Menu ("Edit");
      hegeneral = new MenuItem("General");
      hefinish = new MenuItem ("Finish");
      heclear = new MenuItem ("Clear");
      hedelete = new MenuItem ("Delete vertex"); 
      headd = new MenuItem ("Add vertex"); 
      hemove = new MenuItem ("Move vertex"); 
      hemovearm = new MenuItem ("Move arm");
      hedit.add(hegeneral);
      hedit.add(new MenuItem("-"));
      hedit.add(hefinish);
      hedit.add(heclear);
      hedit.add(hedelete);
      hedit.add(headd);
      hedit.add(hemove);
      hedit.add(hemovearm);

      happlications = new Menu("Applications");
      hageneral = new MenuItem("General");
      hachull3d = new MenuItem("Convex Hull in 3D");
      haptinpoly = new MenuItem("Point in Polyhedra");
      haptsonsphere = new MenuItem("Points on Sphere");
      happlications.add(hageneral);
      happlications.add(hachull3d);
      happlications.add(haptinpoly);
      happlications.add(haptsonsphere);

      help.add(hgeneral);
      help.add(new MenuItem("-"));
      help.add(hregime);
      help.add(hoperations);
      help.add(hedit);
      help.add(happlications);

      bprev = new Button();
      bdummy = new Button();              //this one is never displayed
      bprev = bdummy;                     //this cannot be pressed first
      
      lregime = new Label("Regime:");
      regime = new CheckboxGroup();
      xpoly = new Checkbox("Poly", regime, true);
      xpoint = new Checkbox("Points", regime, false);
      xsegm = new Checkbox("Segments", regime, false);
      xchain = new Checkbox("Chain", regime, false);
      x2poly = new Checkbox("2Poly;", regime, false);
      lnumpoly = new Label("#:");
      polynum = new CheckboxGroup();
      first = new Checkbox ("1st Poly", polynum, true);
      second = new Checkbox("2nd Poly", polynum, false);

      p1.setLayout (new FlowLayout(FlowLayout.LEFT));
      p1.add(lregime);
      p1.add(xpoly);
      p1.add(xpoint);
      p1.add(xsegm);
      p1.add(xchain);
      p1.add(x2poly);
      p1.add(lnumpoly);
      p1.add(first);
      p1.add(second);

      ledit = new Label("Edit:");
      f = ready.getFont();   //System font of button labels
      loutput = new Label("Output:");
      output = new TextArea("", 5, 30);
      output.setEditable(false);
      subp0 = new Panel();
      subp0.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp0.add(ledit);
      subp1.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp1.add(ready);
      subp1.add(clear);
      
      subp022.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp022.add(delete);
      subp022.add(add);
      subp022.add(move);
      subp02.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp02.add(armmove);
      subp2.setLayout(new BorderLayout());
      subp2.add("North", subp1);
      subp2.add("Center", subp022);
      subp2.add("South",  subp02);

      subp56 = new Panel();
      subp56.setLayout(new BorderLayout());
      subp5.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp5.add(loutput);
      subp6.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp6.add(output);
      subp56.add("North", subp5);
      subp56.add("South",subp6);

      subp3.setLayout(new FlowLayout(FlowLayout.LEFT));
      subp3.add(subp56);
      subp4 = new Panel();
      subp4.setLayout(new BorderLayout());
      subp44 = new Panel();
      subp44.setLayout(new BorderLayout());
      subp44.add("Center", subp0);
      subp4.add("Center", subp44);
      p.setLayout(new BorderLayout());
      p.add("North",subp4);
      p.add("Center", subp2);
      p.add("South",subp3);
      
      canvasp.setLayout(new BorderLayout());
      canvasp.add("Center",c);
      
      setLayout(new BorderLayout());
      add("North",p1);
      add("Center",canvasp);
      add("East",p);
      
      mbar = new MenuBar();
      setMenuBar(mbar);	   
      
      close = new Menu("Close");
      term = new MenuItem("Close frame");
      close.add(term);
      operations.add(area);
      operations.add(tri);
      operations.add(chull);
      operations.add(delTri);
      operations.add(segseg);
      operations.add(inpoly);
      operations.add(poly2inters);
      operations.add(mink);
      operations.add(oarmmove);
      mbar.add(close);	      
      mbar.add(operations);
      mbar.add(help);
      c.SetRegime("poly");
  }
  
  public boolean action(Event e, Object o)
  {
    if (e.target instanceof Checkbox) {
      output.setText("");
      if (regime.getCurrent() == xchain ) {
	c.SetRegime("chain");
	armmove.enable();
	ready.disable();
      }
      else {
	armmove.disable();
	ready.enable();
	if (regime.getCurrent() == xpoly ) 
	  c.SetRegime("poly"); 
	else if (regime.getCurrent() == xpoint )
	  c.SetRegime("point");
	else if (regime.getCurrent() == xsegm ) 
	  c.SetRegime("segm");
	else if (regime.getCurrent() == x2poly)
	  c.SetRegime("2poly");
     
      }
	
      if (polynum.getCurrent() == first) {
	c.SetPolygon("first"); c.repaint();
      }
      else if (polynum.getCurrent() == second) {
	c.SetPolygon("second"); c.repaint();
	c.NotToDelete();
	c.NotToAdd();
	c.NotToMove();
      }
      c.repaint();
    } //end of instanceof Checkbox

    if (e.target instanceof MenuItem)
    {	
      answer = "";
      if(e.target == term)
      {
	hide();
	dispose();
      }
      
      if (regime.getCurrent() == xpoly ) {
	c.NotToMinkConv();
	if (e.target == inpoly)   
	{
	  resetBLabel(bprev);
	  bprev = bdummy;
	  c.NotToBeCleared();
	  c.NotToDelete();		
	  c.NotToAdd();
	  c.NotToMove();
	  c.NotToTrian();
	  c.NotToMinkConv();
	  c.NotToConvexInters();
	  c.IsQuery();
	  thread = true;
	  th = new InputWait(c,this);
	  th.start();
	}

	if (e.target == area)
	{	
	  resetBLabel(bprev);
	  bprev = bdummy;
	  c.NotToBeCleared();
	  c.NotToDelete();		
	  c.NotToAdd();
	  c.NotToTrian();
	  c.NotToMove();	  
	  c.ArmNotToMove();
	  c.IsNotQuery();
	  if (c.GetCount()>=3 && c.GetDone()==true) //value can appear 
	                                     //only if the polygon is closed
	  {
	    c.IsCentr();
	    cPointd d;
	    d = c.GetPolyCenter();
	    int area;
	    area = c.GetPolyArea();
	    output.appendText("Area = "+Integer.toString(area)+"\n");
	    output.appendText("Centroid = "+Integer.toString((int)d.x)+" , " 
			      + Integer.toString((int)d.y)+"\n");
	    c.ExtraPaint((int)d.x, (int)d.y);		
	  }
	  else output.appendText("NON-EXISTENT!!"+"\n");
	}
	
	if (e.target == tri && c.GetDone() == true)
	{
	  resetBLabel(bprev);
	  bprev = bdummy;
	  c.NotToBeCleared(); //boolean variable
	  c.NotToDelete();
	  c.NotToAdd();
	  c.IsNotQuery();
	  c.NotToMove();
	  c.ArmNotToMove();
	  if(c.GetCount() >= 3)			   
	    c.TrianPoly();
	}
      }

      if (regime.getCurrent() == xsegm) {
	c.NotToMinkConv();
	if ( e.target == segseg )
	{
	  resetBLabel(bprev);
	  bprev = bdummy;
	  c.NotToBeCleared();
	  c.NotToDelete();		
	  c.NotToAdd();
	  c.NotToTrian();
	  c.NotToMove();
	  c.ArmNotToMove();
	  c.IsNotQuery();
	  if (c.GetCount() >= 4)
	  {
	    char code = c.GetSegSegCode();
	    cPointd p = c.GetSegSegInt();
	    output.appendText("Code{e,v,1,0}="+code+"; ");
	    output.appendText("Pt="+Integer.toString((int)p.x)+"," 
			      + Integer.toString((int)p.y)+"\n");
	  }
	  else
	    output.appendText("Two segments must be entered\n");
	}// end segseg
      }
      
      if (regime.getCurrent() == xpoint) {
	c.ArmNotToMove();
	c.NotToTrian();
	c.NotToMinkConv();
        c.IsNotQuery();
	if (e.target == chull )
	{	  
	  c.NotToTrian();
	  c.ToCH();
	  resetBLabel(bprev);
	  bprev = bdummy;
	}

	if (e.target == delTri ) {
	  resetBLabel(bprev);
	  c.NotToTrian();
	  bprev = bdummy;
	  c.ToDelTri();
	}
      }

      if (regime.getCurrent() == x2poly) {
	output.setText("");
	c.ArmNotToMove();
	c.NotToTrian();
	c.IsNotQuery();
	if (e.target == poly2inters)
	{
	  c.NotToMinkConv();
	  resetBLabel(bprev);
	  bprev = bdummy;
	  if (!c.ToConvConvIntersection())
	    output.appendText("Polygons are not convex\n");
	}
	if (e.target == mink)
	{
	  c.NotToTrian();
	  c.NotToConvexInters();
	  resetBLabel(bprev);
	  bprev = bdummy;
	  if (!c.ToFindMinkConv())
	    output.appendText("Polygon is not covex\n");
	}
      }

      if (regime.getCurrent() == xchain) {
	c.IsNotQuery();
	if (e.target == oarmmove)
	{
	  armmove.setFont(new Font("Times Roman",Font.ITALIC,11)); 	     
	  ready.disable();
	  resetBLabel(bprev);
	  bprev = move;
	  c.NotToDelete();
	  c.NotToTrian();
	  c.NotToMinkConv();
	  c.NotToBeCleared();
	  c.NotToAdd();
	  c.NotToMove();
	  c.ArmToMove();
	}
      }
      
      if (e.target == hgeneral) {     // General Help
	hf = new HelpFrame("greeting", "Greetings from the Authors!");
	hf.show();
      }

      if (e.target == hrpoly) {       // Regime: Polygon
	hf = new HelpFrame("regimepoly", "Regime: Polygon");
	hf.show();
      }   

      if (e.target == hrpoints) {     // Regime: Points
	hf = new HelpFrame("regimepoints", "Regime: Points");
	hf.show();
      }

      if (e.target == hrsegments) {    // Regime: Segments
	hf = new HelpFrame("regimesegments", "Regime: Segments");
	hf.show();  
      }

      if (e.target == hrchain) {      // Regime: Chain
	hf = new HelpFrame("regimechain", "Regime: Chain");
	hf.show();
      }

      if (e.target == hr2poly) {      // Regime: 2 Polygons
	hf = new HelpFrame("regime2poly", "Regime: 2 Polygons");
	hf.show();
      }

      if (e.target == hoarea) {       // Operations: Area/Centroid
	hf = new HelpFrame("operarea", 
			   "Operations: Area/Centroi");
	hf.show();
      }

      if (e.target == hoinpoly) {     // Operations: In Poly
	hf = new HelpFrame("operinpoly","Operations: In Poly");
	hf.show();
      }

      if (e.target == hoseg) {        // Operations: SegSegInt
	hf = new HelpFrame("opersegseg", 
			   "SegSegInt");
	hf.show();
      }

      if (e.target == hotriang) {     // Operations: Triangulate
	hf = new HelpFrame("opertriang", "Operations: Triangulate");
	hf.show();
      }

      if (e.target == hochull2d) {      // Operations: Convex Hull
	hf = new HelpFrame("operchull2d", 
			   "Operations: Convex Hul");
	hf.show();
      }

      if (e.target == hodelaunay) {   // Operations: Delaunay Triangulation
	hf = new HelpFrame("operdeltri", 
			   "Operations: Delaunay Triangulation");
	hf.show();
      }

      if (e.target == ho2inters) {    // Operations: Inters. of 2 Polygons
	hf = new HelpFrame("operinters2p", 
			   "Operations: Inters. of 2 Polygons");
	hf.show();
      }

      if (e.target == homink) {       // Operations: Minkowski Convolution
	hf = new HelpFrame("operminkow", 
			   "Operations: Minkowski Convolution");
	hf.show();
      }

      if (e.target == hoarmmove) {       // Operations: Arm Move
	hf = new HelpFrame("editarmmove", 
			   "Operations: Move Arm");
	hf.show();
      }

      if (e.target == hefinish) {     // Edit: Finish
	hf = new HelpFrame("editfinish", "Edit: Finish");
	hf.show();
      }

      if (e.target == heclear) {      // Edit: Clear
	hf = new HelpFrame("editclear", "Edit: Clear");
	hf.show();
      }

      if (e.target == hedelete) {     // Edit: Delete
	hf = new HelpFrame("editdelete", "Edit: Delete");
	hf.show();
      }

      if (e.target == headd) {        // Edit: Add
	hf = new HelpFrame("editadd", "Edit: Add");
	hf.show();
      }

      if (e.target == hemove) {       // Edit: Move
	hf = new HelpFrame("editmove","Edit: Move");
	hf.show();
      }

      if (e.target == hemovearm) {    // Edit: Move Arm
	hf = new HelpFrame("editarmmove","Edit: Move Arm");
	hf.show();
      }

      if (e.target == hrgeneral) {    // General on Regime
	hf = new HelpFrame("regimegeneral", "General on Regime");
	hf.show();
      }

      if (e.target == hogeneral) {    // General on Operations
	hf = new HelpFrame("opergeneral", "General on Operations");  
	hf.show();
      }

      if (e.target == hegeneral) {    // General on Edit
	hf = new HelpFrame("editgeneral", "General on Edit"); 
	hf.show();
      }

      if (e.target == hageneral) {    // General on Applications
	hf = new HelpFrame("applgeneral", "General on Applications");
	hf.show();
      }

      if (e.target == hachull3d) {    // Applications: ConvexHull3D
	hf = new HelpFrame("applchull3d", "Applications: ConvexHull3D");
	hf.show();
      }

      if (e.target == haptinpoly) {   // Applications: Point in Polyhydra
	hf = new HelpFrame("applptinpoly", "Applications: Point in Polyhydra");
	hf.show();
      }

      if (e.target == haptsonsphere) {// Applications: Points on Sphere
	hf = new HelpFrame("applptsonsphere", "Applications: Points on Sphere");
	hf.show();
      }

    }//end instanceof MenuItem

    if (e.target instanceof Button)
    {
      if(e.target == ready)
      {	
	if (regime.getCurrent() == x2poly)
	  c.OnePolygonIsDone();
	ready.setFont(new Font("Times Roman",Font.ITALIC,11));
	resetBLabel(bprev);
	bprev = ready;
	c.IsDone();
	c.IsNotQuery();
	c.NotToDelete();
	c.NotToTrian();
	c.ArmNotToMove();
	c.SetPaint();				  
      }

      if (e.target == clear)
      {
	output.setText("");
	clear.setFont(new Font("Times Roman",Font.ITALIC,11));
	resetBLabel(bprev);
	bprev = clear;
	c.ToBeCleared();//boolean variable
	c.NotToDelete();
	c.NotToMinkConv();
	c.NotToAdd();
	c.NotToTrian();	
	c.IsNotQuery();
	c.ArmNotToMove();
	c.NotToConvexInters();
	c.NotToMove();
	c.ArmNotToMove();
	c.CoorClear();
	c.SetPaint();
	if (polynum.getCurrent() == second)
	  polynum.setCurrent(first);
      }

      if (e.target == delete)
      {
	delete.setFont(new Font("Times Roman",Font.ITALIC,11));	 	
	resetBLabel(bprev);
	bprev = delete;
	c.NotToMinkConv();
	c.IsDone();
	c.IsNotQuery();
	c.NotToTrian();	
	c.NotToAdd();
	c.ArmNotToMove();
	c.NotToConvexInters();
	c.NotToMove();	
	c.ArmNotToMove();
	c.ToDelete();//boolean variabble
      }

      if (e.target == add)
      {
	add.setFont(new Font("Times Roman",Font.ITALIC,11));	  
	resetBLabel(bprev);
	bprev = add;
	c.NotToDelete();
	c.IsNotQuery();
	c.ArmNotToMove();
	c.NotToMinkConv();
	c.NotToConvexInters();
	c.NotToTrian();	
	c.NotToBeCleared();
	c.IsDone();
	c.NotToMove();
	c.ArmNotToMove();
	c.ToAdd();
	
      }
      if (e.target == move)
      {
	move.setFont(new Font("Times Roman",Font.ITALIC,11)); 	     
	resetBLabel(bprev);
	bprev = move;
	c.NotToDelete();
	c.ArmNotToMove();
	c.NotToConvexInters();
	c.IsNotQuery();
	c.NotToMinkConv();
	c.NotToBeCleared();
	c.IsDone();
	c.NotToTrian();	
	c.NotToAdd();
	c.ToMove();
	c.ArmNotToMove();  
      }
      if (e.target == armmove)
      {
	armmove.setFont(new Font("Times Roman",Font.ITALIC,11)); 	     
	ready.disable();
	resetBLabel(bprev);
	bprev = move;
	c.NotToDelete();
	c.NotToMinkConv();
	c.NotToTrian();	
	c.IsNotQuery();
	c.NotToBeCleared();
	c.NotToAdd();
	c.NotToMove();
	c.ArmToMove();
      }  
    }
    return true;
  }
  
  public boolean handleEvent(Event e)
  {
    if (e.id == Event.WINDOW_DESTROY)
    {
      if(thread) th.stop();//the thread is killed if it was alive
      hide();
      dispose();
      return true;
    }
    return super.handleEvent(e);
  }
  
  public void resetBLabel(Button bprev)
  {
    bprev.setFont(f);
  }
  
  public void SetFinish()
  {
    clear.setFont(f);
    ready.setFont(new Font("Times Roman",Font.ITALIC,11));
    bprev = ready;
  }
  
  public void displayResult(char ans)
  {
    if(ans=='e') answer = on;
    if(ans=='i') answer = in;
    if(ans=='o') answer = out;
    if(ans=='v') answer = vert;
    output.appendText(answer + "\n");
  }

  public void SetMessage(String s)
  {
    output.setText(s);
  }
}



