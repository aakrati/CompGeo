import java.awt.*;

public class GeomCanvas extends Canvas
{
  static final int VSIZE = 6;
  private CompGeomTest f; 	     // reference to the frame
  private cVertexList list, list2;
  private cPolygoni Pol;  
  private cChain myChain;
  private DelaunayTri delTri;
  private ConvexHull2D myCH;
  private cSegSeg segmts;
  private ConvConv cc;
  private MinkConvol mc;
  private boolean first, first2, done, done1, done2, toClear, toDelete, 
                  toAdd, toMove, poly2inters, aToMove, chain, found, query, 
                  centr, extrap, trian, minkconvol,
                  toPaint, toDelTri, firstpoly, secndpoly, toCH;
  private cVertex movingV;
  private cPointi queryP, oldQuery;
  private Color inColor;
  private Graphics gContext;   	     // variables for double buffering
  private Image buffer;
  private char ans;
  private int CanW, CanH; 	     // width and height of the drawing canvas
  private int w = VSIZE, h = VSIZE;  // w. and h. of vertices              
  private String regime      = "";
  static final String POLY   = "poly"; 
  static final String SEGM   = "segm";
  static final String POINTS = "point";
  static final String CHAIN  = "chain";
  static final String POLY2  = "2poly";
  private MesgFrame frame;

  GeomCanvas(int cw, int ch, CompGeomTest frame) { 
    resize(cw,ch);
    CanW = cw; 
    CanH = ch; 
    f = frame;                       // the color of the inside of the
    inColor = new Color(0,250,250); 
    setBackground(Color.white);
    first = found = firstpoly = true; 
    toClear = toDelete = toAdd = toMove = first2 = done = done1 = 
      done2 = secndpoly = poly2inters = minkconvol = false; 
    query = extrap = centr = trian = toPaint = toDelTri = false; 
    list = new cVertexList();
    list2 = new cVertexList();
    myCH = new ConvexHull2D(list);
    Pol = new cPolygoni(list);
    myChain = new cChain(list);
    delTri = new DelaunayTri();
    segmts = new cSegSeg(list);
    cc = new ConvConv();
    mc = new MinkConvol();
    queryP = new cPointi(); 
    oldQuery = new cPointi(); 
    ans = 'n'; 
  } 
  
  public void SetRegime(String state)
  {
    regime = state;
    if (!regime.equals(POLY2))
      NotToConvexInters();
  }

  public void ChangeToChain()
  {
    chain = true;
    
  }

  public void ChangeBPolygon()
  {
    chain = false;
  }

  public void ArmToMove()
  {
    aToMove = true;
    toAdd = toDelete = toMove = false;
    done = true;
    repaint();
  }
  
  public void ArmNotToMove()
  {
    aToMove = false;
    repaint();
  }

  public void IsQuery()
  {
    query = true;
  }

  public void IsNotQuery()
  {
    query = false;
    extrap = false;
  }
  
  public boolean GetQuery()
  {
    return query;
  }

  public void IsCentr()
  {
    centr = true;
  }

  public void IsDone()
  {
    if (!regime.equals(POLY2) || (regime.equals(POLY2) && firstpoly && list.n > 2)){
      done = true; done1 = true;
    }
    else if (secndpoly) {
      done2 = true;
    }
    if (!regime.equals(SEGM) && !regime.equals(CHAIN))
      OrientList();
    repaint();
  }

  private void FirstEntered()
  {
    if (!regime.equals(POLY2) || (regime.equals(POLY2) && firstpoly)) 
      first = false;
    else if (secndpoly) 
      first2 = true;
  }

  public void OnePolygonIsDone()
  {
    if (!regime.equals(POLY2)) {
      done = true; done1 = true;
      return;
    }

    if (!done1 && list.n > 2) {
      done1 = true;
    }
    else if (done1 == true && list2.n > 2) 
      done2 = true;
    repaint();
  }

  public void SetPolygon(String s)
  {
    if (s.equals("first")){
      firstpoly = true;
      secndpoly = false;
    }
    else if (s.equals("second") && list.head != null) {
      firstpoly = false;
      secndpoly = true;
    }
  }

  public void IsNotDone()
  {
    if (!regime.equals(POLY2) || (regime.equals(POLY2) && firstpoly)) 
      done = false;
    else if (firstpoly && list2.n < 3) 
      done1 = false; 
    else if (secndpoly && list2.n < 3)
      done2 = false;
  }

  public boolean GetDone()
  {
    return done;
  }

  public void ToBeCleared()
  {
    toClear = true;
    repaint();
  }

  public void NotToBeCleared()
  {
    toClear = false;
  }

  public void ToDelTri()
  {
    delTri.Start(list);
    toDelTri = true;
    repaint();
  }

  public void ToCH()
  {
    toCH = true;
    repaint();
  }

  public boolean ToConvConvIntersection()
  {
    if (list.n > 2 && list2.n > 2)
      if (cc.Start(list, list2)) {
	poly2inters = true;
	repaint();
	if (!cc.intersection)
	  f.SetMessage("Polygon boundaries don't cross\n(||a special case)");
	return true;
      }
    return false;
  }

  public boolean ToFindMinkConv()
  {
    if (list.n > 2 && list2.n > 2) {
      if (!mc.Start(list, list2)) 
	return false;
      else
	minkconvol = true;
    }
    return true;
  }
  
  public void NotToConvexInters()
  {
    if (poly2inters) {
      poly2inters = false;
      cc.ClearConvConv();
    }
  }
  
  public void NotToMinkConv()
  {
    minkconvol = false;
  }

  public void CoorClear()
  {
    Pol.ClearPolygon();
    list.ClearVertexList();
    list2.ClearVertexList();
    myChain.ClearChain();
    myCH.ClearHull();
    delTri.ClearDelaunay();
    if (minkconvol)
      mc.ClearMinkConvol();
    segmts.ClearSegments();
    first = found = firstpoly = true; 
    toDelete = toAdd = toMove = aToMove = first2 = done = done1 = 
               done2 = secndpoly = poly2inters = minkconvol = false; 
    query = extrap = centr = trian = toPaint = false; 
    queryP.x = queryP.y = 0;
    segmts.p.x = segmts.p.y = 0.0;
    oldQuery.x = oldQuery.y = 0; 
    ans = 'n'; 
    repaint();
  }

  public void ToDelete() 
  { 
    toDelete = true;
    if (regime.equals(CHAIN))
      myChain.ClearChain();
    if (toDelTri) {
      toDelTri = false;
      delTri.ClearDelaunay();
    }
    if (toCH)
    {
      toCH = false;
      myCH.ClearHull();
    }
  }

  public void NotToDelete()
  {
    toDelete = false;
  }

  public void ToAdd()
  {
    toAdd = true;
    if (regime.equals(CHAIN))
      myChain.ClearChain();
    if (toDelTri) {
      toDelTri = false;
      delTri.ClearDelaunay();
    }    
    if (toCH){
      toCH = false;
      myCH.ClearHull();
    }

  }

  public void NotToAdd()
  {
    toAdd = false;
  }

  public void ToMove()
  {
    toMove = true;
    if (regime.equals(CHAIN))
      myChain.ClearChain();
    if (toDelTri) {
      toDelTri = false;
      delTri.ClearDelaunay();
    }
    if (toCH){
      toCH = false;
      myCH.ClearHull();
    }
    if (regime.equals(SEGM))
      segmts.ClearSegments();
  }

  public void NotToMove()
  {
    toMove = false;
  }

  public int GetCount()
  {
    return list.n;
  }

  public char GetAns()
  {
    return ans;
  }

  public void NotToTrian()
  {
    trian = false;
  }

  public void SetAns(char input)
  {
    ans = input;
  }

  public void paint(Graphics g)
  {
    cDiagonal dtemp; 
    cVertex vtemp;

    if(toClear)
    {		
      g.setColor( Color.white );
      g.fillRect( 0, 0, CanW, CanH);			
      IsNotDone();
      toClear = false;
    }

    if (toPaint) {

      buffer = createImage(CanW,CanH);
      gContext = buffer.getGraphics();

      //deleting the old triangulation if it still exists 
      if ( trian == false && !Pol.DiagDrawn() )
      {    
	System.out.println("deleting old triangulation");
      	Pol.diaglist.DrawDiagonals(g, inColor);
      	Pol.SetDiagDrawn( true );
      	Pol.ClearDiagList();
      }	
      
      if(toClear)
      {		
	g.setColor( Color.white );
	g.fillRect( 0, 0, CanW, CanH);			
	IsNotDone();
      }

      if (regime.equals(POLY))     // polygon is the input
      {
	if (first == true && done == false && toClear == false)
	  list.DrawHead(gContext,w,h);
	
	if (first == false && done == false && toClear == false)
	  list.DrawChain(gContext, w, h);
	
	if (first == false && done == true && toClear == false) 
	  list.DrawPolygon(gContext, w, h, inColor, Color.blue, true);

	if (trian && Pol.diaglist.head != null) {
	  Pol.diaglist.DrawDiagonals(gContext, Color.black);
	  System.out.println("drawing diagonals");
	}

	if (query)
	{	
	  Pol.DrawInPoly(gContext,queryP,CanW, CanH, w, h);
	  oldQuery.x = queryP.x;
	  oldQuery.y = queryP.y; 
	}

	toClear = false;
	first = false;           // to set it back after every loop is checked 
      }
      
      if (regime.equals(SEGM))     // segments are the input
      {
	segmts.DrawSegments(gContext,w,h);
	if (segmts.code != '0')
	  segmts.DrawInters(gContext, w,h);
      }

      if (regime.equals(POINTS))   // points are the input
      {
	if (toDelTri && delTri.toDraw)
	  delTri.DrawDelaunayTri(gContext, w, h);
	else 
	{
	  if (toCH)
	  {
	    myCH.RunHull();
	    myCH.DrawHull(gContext, w, h);
	  }
	  else
	    list.DrawPoints(gContext, w, h);
	}
      }

      else if (regime.equals(CHAIN))    // chain is the input
      {
	list.DrawChain(gContext,w,h);
	myChain.DrawDots(gContext, w, h);
      }
      
      else if (regime.equals(POLY2))
      {
	if (first && !done1 && !toClear)
	  list.DrawHead(gContext,w,h);
	
	if (first2 && !done2 && !toClear)
	  list2.DrawHead(gContext,w,h);
	
	if (secndpoly) {	
	  if (!first && !done1 && !toClear) 
	    list.DrawChain(gContext, w, h);
	  
	  if (!first && done1 && !toClear)
	    list.DrawPolygon(gContext, w, h, inColor, Color.blue, true);
	  
	  if (first2 && !done2 && !toClear) 
	    list2.DrawChain(gContext, w, h); 
	  
	  if (first2 && done2 && !toClear)
	    list2.DrawPolygon(gContext, w, h, Color.pink, Color.blue, false);
	}
	else {
	  if (first2 && !done2 && !toClear) 
	    list2.DrawChain(gContext, w, h); 
	  
	  if (first2 && done2 && !toClear)
	    list2.DrawPolygon(gContext, w, h, Color.pink, Color.blue, false);

	  if (!first && !done1 && !toClear) 
	    list.DrawChain(gContext, w, h);
	  
	  if (!first && done1 && !toClear)
	    list.DrawPolygon(gContext, w, h, inColor, Color.blue, true);
	}
	if (poly2inters) {
	  cc.DrawIntersection(gContext, w, h, Color.yellow);
	}

	if (minkconvol)
	  mc.DrawMinkConvol(gContext, w, h);

	first = false;
	if (!first2 && secndpoly) first2=true;
	toClear = false;
      }

      if(extrap)
      {
	gContext.setColor(Color.red);
	gContext.fillOval(queryP.x - (int)(w/2), queryP.y - (int)(h/2), w, h);
	oldQuery.x = queryP.x;
	oldQuery.y = queryP.y;
      }

      g.drawImage(buffer, 0, 0, this);
    }
  }

  public void update(Graphics g)
  {
    paint(g); 
  }

  public void SetPaint()  //needed for the applet
  {
    repaint();	  
  }

  public void ExtraPaint(int x, int y)
  {
    queryP.x = x;
    queryP.y = y;
    extrap = true;
    repaint();
  }
  
  public boolean mouseDown(Event e, int x, int y)
  {
    int xset = x, yset = y;
    cVertex vNear = new cVertex();
    boolean lastpoint = false;
    cVertexList listCur;
    listCur = list;

    if (regime.equals(POLY2) && secndpoly && !firstpoly) {
      //      System.out.println("Cur list is the second one now...");
      listCur = list2;
    }

    if (e.modifiers == Event.SHIFT_MASK && listCur.n > 0) {
      cPointi l = listCur.head.prev.v;
      int dx = 0, dy = 0;
      dx = Math.abs(l.x - xset);
      dy = Math.abs(l.y - yset);
      if (dx < dy)
	xset = listCur.head.prev.v.x;
      else
	yset = listCur.head.prev.v.y;
    }
    
    boolean donex;

    donex = done;
    if (regime.equals(POLY2) && firstpoly)
      donex = done1;
    else if (regime.equals(POLY2) && secndpoly) 
      donex = done2;

    if(!donex && !(toAdd || toMove || toDelete || aToMove))
    {
      //do automatic closure if the user clicks 
      //on the first vertex for the second time
      if (listCur.n != 0) {
	cVertex last = listCur.head;
	lastpoint = (last.v.x <= xset+(w/2) && last.v.x >= xset-(w/2) 
		     && last.v.y <= yset+(h/2) && last.v.y >= yset-(h/2));
      }
      
      if (listCur.n == 4 && regime.equals(SEGM))   // there allowed only 2 segments
	IsDone();
      
      if (listCur.n >= 1 && lastpoint) 
	OnePolygonIsDone();      
      
      else  //just a vertex is added
      {	
	listCur.SetVertex( xset, yset);
      }
      
      //the appropriate finish mode is set (appearing on the button)
      toPaint = true;
      
    } // end of adding a chain
    
    if(toDelete)
    {
      vNear = listCur.GetNearVertex( xset, yset); //the the closest vertex
      if(vNear != null)
      {
	listCur.Delete( vNear );
	if(listCur.n == 0) {
	  toClear = true;
	  CoorClear();
	}
      }
      else System.out.println("No vertex can be deleted.");
    }
    
    if( toAdd )
    {
      if ( !regime.equals(CHAIN)) {
	if(listCur.n < 3) //just a vertex is added
	{		
	  listCur.AddVertex( xset, yset);
	  IsNotDone();
	}
	
	else               //must oriente P before final paint
	{		
	  listCur.AddVertex( xset, yset );
	  IsDone();	
	  OrientList();
	}
      }

      else {
	if (!aToMove )
	  listCur.SetVertex(xset,yset);
      }
    }

    if( toMove ) //the first time the mouse is put down before dragging starts
    {
      movingV = listCur.FindVertex(xset,yset,w,h); //finds vertex to be moved
      if(movingV == null) 
      { 
	found = false;
	System.out.println("There is no vertex to be moved");
      }
      else
      {
	found = true;
      }
    }

    if(query && regime.equals(POLY))
    {		   
	queryP.x = xset;
	queryP.y = yset;
	ans = Pol.InPoly1(queryP); 		   
    }

    if (!regime.equals(POLY2))
      list.PrintVertices();
    else {
      System.out.println("First list:");
      list.PrintVertices();
      System.out.println("Second list:");      
      list2.PrintVertices();
    }
    repaint();
    return true;
  }
  
  public boolean mouseUp(Event e, int x, int y)
  {
    cVertexList listCur;
    if (!regime.equals(POLY2) || (regime.equals(POLY2) && firstpoly)) 
      listCur = list;
    else
      listCur = list2;

    if(regime.equals(CHAIN) && aToMove && found)
    {
      if (!myChain.Solven(x, y)) {
	frame=new MesgFrame("Message Frame", "Hand out of reach!");
        frame.resize(400, 100);
        frame.show();
      }
      repaint();
    }
    
    if(toMove && found)
    {
      //the last position of mouse, final position for the moving vertex 
      listCur.ResetVertex( movingV, x, y);
      repaint();		  
    }
    return true;
  }  

  public boolean mouseDrag(Event e, int x, int y)
  {
    cVertexList listCur;
    if (!regime.equals(POLY2) || (regime.equals(POLY2) && firstpoly)) 
      listCur = list;
    else
      listCur = list2;
    
    if(toMove && found)
    {		  
      listCur.ResetVertex( movingV, x, y);		  
      repaint();
    }
    return true;
  }

  public void TrianPoly()
  {
    trian = true;
    System.out.println("Printing current vertices");
    Pol.PrintPoly();
    Pol.ListCopy();
    Pol.Triangulate(); 
    Pol.ClearListCopy(); 
    repaint();
  }

  public cPointd GetPolyCenter()
  {
    Pol.FindCG();
    return Pol.CG;
  }

  public int GetPolyArea()
  {
    int area;
    area = (int) (Pol.AreaPoly2()*0.5);
    return area;
  }

  public char GetSegSegCode()
  {
    return segmts.SegSegTopLevel();
  }


  // Note we assume GetSegSegCode() has been called first, and so inters. point
  // has already been computed.
  public cPointd GetSegSegInt()
  {
    return segmts.p;
  }

  public char WhereIsPoint(int x, int y)
  {
    cPointi p = new cPointi();
    p.x = x;
    p.y = y;
    if(list.n != 0)
    {
      char a = Pol.InPoly1(p);		
      return a;
    }
    else return 'n';
  }

  public void OrientList()
  {	  	  
    cVertexList listCur;
    listCur = list;
    if (regime.equals(POLY2) && secndpoly && !firstpoly)
      listCur = list2;

    if( (firstpoly && listCur.n >= 3) || (secndpoly && listCur.n >= 3))
    {	   
      if (listCur.Ccw() != 1) {
	if (firstpoly)
	  listCur.ReverseList();
	else
	  listCur.ReverseList();
      }
    }
  }
}


class MesgFrame extends Frame
{

  Label ms;
  Button ok;

  MesgFrame(String title, String message)
  {
    setLayout(new GridLayout(2,1));
    setTitle(title);
    ms=new Label(message);
    ok= new Button("OK");
    add(ms);
    add(ok);
  }

  public boolean action(Event evt, Object o)
  {
    if (evt.target == ok) hide();
    return true;
  }

  public boolean handleEvent(Event evt) 
  {
    if (evt.id == Event.WINDOW_DESTROY)
    {
      dispose();
    }
    return super.handleEvent(evt);
  }

} //end Mesg Frame 
