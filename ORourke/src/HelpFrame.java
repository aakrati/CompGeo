/*----------------------------------------------------------------------------
 * Class HelpFrame -- used by the Help Menu
 *
 *---------------------------------------------------------------------------*/
import java.io.*;
import java.net.*;
import java.awt.*;

class HelpFrame extends Frame
{
  Button ok;
  TextArea helpmsg;

  final static String greeting        = new String (""+
"This applet illustrates code from the book _Computational Geometry in C_ "+
"(Second Edition).  It is not written to be self-explanatory without "+
"the book. All code is available free, in either Java or C.  This applet "+
"illustrates all the code that can easily be displayed on a 2D plane; "+
"three programs that are primarily 3D are available as Java applications. "+
"See Help/Operations/General for a list of available operations. "+
"For more information, see http://cs.smith.edu/~orourke/; "+
"questions to orourke@cs.smith.edu.\n"+
"Applet written by Irena Paschenko, Octavia Petrovici, Lilla Zollei, "+
"and Joseph O'Rourke.  All code Copyright 1998." 
);

  final static String regimegeneral   = new String (""+
"We call the different classes of possible 2D data input *Regimes*. "+
"All consist of vertices, 2D points entered by clicking, and depending "+
"on the regime, perhaps segments connecting vertices. "+
"The editing operations are common to all regimes.\n"+
"The shift key constrains one coordinate of the entered point to be "+
"the same as that of the previous point, whichever coordinate is closer. "+
"This permits entering exact horizontal and vertical segments. "+
"This feature works across all regimes."
);

  final static String regimepoly      = new String (""+
"Regime: Polygon\n\n"+
"Enter vertices in sequence.  The orientation is irrelevant, as "+
"immediately after the polygon is complete, it is reoriented to be "+
"counterclockwise.\n"+
"The polygon is closed in one of two ways: either by clicking "+
"within 3 pixels of the zeroth point; "+
"or by selecting *Finish*.\n"
);

  final static String regimepoints    = new String (""+
"Regime: Points\n\n"+
"Enter points in random order.  No need to *Finish*:  "+
"choose an appropriate operation at any time."
);

  final static String regimesegments  = new String (""+
"Regime: Segments\n\n"+
"Enter four points, which are interpretted as the endpoints of "+
"two segments.  No need to *Finish*.  For use with "+
"Operation/SegSegInt only."
);

  final static String regimechain     = new String (""+
"Regime: Chain\n\n"+
"Enter a sequence of vertices to represent a multilink polygonal chain. "+
"No need to *Finish*.  For use with MoveArm only."
);

  final static String regime2poly     = new String (""+
"Regime: Two Polygons\n\n"+
"Enter two separate polygons.  Choose 1st or 2nd Poly, then enter "+
"just like a single polygon.  Edit operations affect whichever is "+
"selected.  For use with Operations Intersection of two *convex* "+
"polygons, or Minkowski convolution of convex with an arbitrary polygon."
);  


  final static String opergeneral     = new String (""+
"The nine available operations correspond directly to code described "+
"in the book.  Several require input in specialized regimes, "+
"e.g., triangulation operates on a polygon, whereas convex hull "+
"operates on a set of points.  However, one can freely reinterpret "+
"a polygon as points simply by changing regimes after the points have "+
"been entered.\n\n"+
"Applet Operations:\n"+
"Area/Centroid:\t\tChapter 1, Code 1.5; Exercise 1.6.5.\n"+
"Triangulate:\t\tChapter 1, Code 1.14.\n"+
"Convex Hull (2D):\t\tChapter 3, Code 3.8\n"+
"Delaunay Triangulation:\tChapter 5, Code 5.2\n"+
"SegSegInt:\t\t\tChapter 7, Code 7.2.\n"+
"In Poly?:\t\t\tChapter 7, Code 7.13.\n"+
"Inter. 2 Conv. Poly:\t\tChapter 7, Code 7.17.\n"+
"Minkowski Convolution:\tChapter 8, Code 8.5.\n"+
"Arm Move:\t\tChapter 8, Code 8.7.\n\n"+
"Java Applications:\n"+
"Convex Hull (3D):\t\tChapter 4, Code 4.8\n"+
"sphere.c:\t\t\tChapter 4, Figure 4.15\n"+
"Point-in-Polyhedron:\tChapter 7, Code 7.15\n"
);

  final static String operarea        = new String (""+
"Area/Centroid\n\n"+
"Regime: Polygon\n\n"+
"Input: Polygon of 3 or more vertices.\n\n"+
"This option allows you to compute area and centroid of a polygon. "+
"The area is then displayed in the output window of the frame\n\n"+
"Centroid is displayed as a red point.\n"
);

  final static String operinpoly      = new String (""+
"In Poly?\n\n"+
"Regime: Polygon\n\n"+
"Input: Polygon of 3 or more vertices.\n\n"+
"Allows you to determine if a point is inside a polygon or not\n"+
"Select this option and click on points in and around polygon\n"+
"The query point is displayed red, and left and right rays "+
"are shown with their crossings displayed.\n\n"
);

  final static String opersegseg      = new String (""+
"SegSegInt\n\n"+
"Regime: Segments\n\n"+
"Input four points, endpoints of two segments.  \n\n"+
"Choose Operation/SegSegInt, and the intersection of the two "+
"segments will be displayed: a single point when the segments "+
"cross, the common subsegment when they overlap.  \n\n"+
"The return code is displayed in the output window."
);

  final static String opertriang      = new String (""+
"Triangulate\n\n"+
"Regime: Polygon"+
"Input: Polygon of > 3 vertices\n\n"+
"This operation gives you a triangulation of a polygon\n"
);

  final static String operchull2d     = new String (""+
"Convex Hull 2D\n\n"+
"Regime: Points (3 or more)\n\n"+
"This operation computes and displays the convex hull of a set of points in 2D"
);

  final static String operdeltri      = new String (""+
"Delaunay Triangulation\n\n"+
"Regime: Points\n\n"+
"Input: 4 or more points\n\n"+
"Draws Delaunay Triangulation of the input points\n"+
"You can edit points however you like as many times as necessary\n"
);

  final static String operinters2p    = new String (""+
"Intersection of 2 Polygons\n\n"+
"Regime: 2Poly\n\n"+
"Input: 2 convex polygons (of more than 3 vertices)\n\n"+
"This operations lets you compute intesection of two convex polygons\n\n"+
"Note: The case when one polygon is inside of another "+
"is not handled in the code.  As is the case when several edges of one "+
"polygon lie on an edge of another"
);

  final static String operminkow      = new String (""+
"Minkowski Convolution\n\n"+
"Regime: 2Poly\n\n"+
"Input: 2 polygons, second of which is convex\n\n"+
"Computes Minkowski Convolution of the input polygons\n"+
"A piece of advise:  to see all of it, make your input smaller, "+
"so that the picture fits on the screen\n"
);

  final static String editgeneral     = new String (""+  // General
"Editting:\n\n"+
"You can easily change shapes of objects and recompute on "+
"on the new objects any operations\n\n"+
"Finish polygon\n"+
"Clear\n"+
"Add vertex\n"+
"Delete vertex\n"+
"Move vertex\n"+
"Movearm -- which is rightly speaking an operation"
);

  final static String editfinish      = new String (""+
"Polygon Finish - Button\n\n"+
"Click this button, if you what to finish polygon\n\n"+
"Another way to do it is to simply click on its first point\n\n"+
"Warning: If you forget to close it yourself and try editing, "+
"the polygon will be automatically finished for you\n"
);

  final static String editclear       = new String (""+
"Clear - Button\n\n"+
"Click this button whenever you want to erase \n"+
"your input entirely, all previous work that you "+
"have done then gets deleted and you start from "+
"scratch, as if you just opened the frame\n" 
);

  final static String editadd         = new String (""+
"Add Vertex- Button\n\n"+
"Used for adding more points to already existing objects.\n"+
"The new points are then added between the closest edge formed by "+
"the already existing points\n"
);

  final static String editdelete      = new String (""+
"Delete Vertex- Button\n\n"+
"Used for deleting existing points:\n"+
"Click on the point you want to delete and it will dissappear"
);

  final static String editmove        = new String (""+
"Move Vertex\n\n"+
"Allows you to move any of the existing vertices/points on the canvas:\n"+
"Click and drag on a point to move it.\n"
);

  final static String editarmmove     = new String (""+
"Move Arm\n\n"+
"Choose MoveArm button/operation and then click on the point in the plane"+
" you want to reach.  If the point is not reachable a pop-up"+
" window will warn you about this. "
);


  final static String applgeneral     = new String (""+  // General
"Applications\n\n"
);

  final static String applchull3d     = new String (""+
"Convex Hull in 3D -- Application\n\n"+		  
"To run:  java ConvexHull3D < inputfile\n\n"+
"Input points by the following dirrections:\n"+	         
"1. Coord-s must be seperated by a *tab*\n"+	     	 
"2. Hit ENTER after the third coordinate of each point\n"+                 
"3. To finish input type end + ENTER at the end\n"+                  
"Example:\n"+                  
"17      23      123\n"+
"34      5      1\n"+                  
"end "
);

  final static String applptinpoly    = new String (""+
"Point in Polygon\n\n"+
"Application: Point in Polyhedron\n\n"+
"To run:  java InPolyh < inputfile\n\n"+
"Input points by the following dirrections:\n"+	         
"1. Coord-s must be seperated by a *tab*\n"+	     	 
"2. Hit ENTER after the third coordinate of each point\n"+                 
"3. To finish input type end + ENTER at the end\n"+                  
"Example:\n"+                  
"17      23      123\n"+
"34      5      1\n"+                  
"end "+
"Determines if a point is inside or outside a polyhedron.\n"+
"It can return the following values:\n\ti: point inside\n\to: "+
"points outside\n\tv: point is a vertex\n\te: point is on one of "+
"the edges\nf: point is on one of the faces "
);

  final static String applptsonsphere = new String (""+
"Application which generates points on a sphere\n\n"+
"It generates a given number of points uniformly distributed on the "+
"surface of a sphere.  The number of points is given on the command "+
"line as the first parameter.\n"+
"The following parameters can also be used:\n"+
"\t* The flag -r determines the radius of the sphere (default is 100).\n"+
"\t* The flags -a, -b, -c are used to set ellipsoid axis lengths."
);

  final static int lwidth = 70;       // how many characters fit per line

  HelpFrame (String nameofstring, String title)
  {
    setLayout(new FlowLayout(FlowLayout.LEFT));
    resize (650, 410);
    setResizable (false);
    setFont(new Font("Helvetica", Font.BOLD, 16));
    setTitle(title);
    helpmsg = new TextArea(15, lwidth-5);
    String s = new String();
    s = GetString(nameofstring);
    s = EditString(s);
    setFont(new Font("TimesRoman", Font.BOLD, 18));
    helpmsg.setText(s);
    ok = new Button("OK");
    add (helpmsg);
    add (ok);
  }

  private String EditString (String name)
  {
    String st = new String();
    st = name;
    if (!st.endsWith(new String("\n")))
      st = st + "\n";
    String result = new String("");
    int k = st.length();

    while (st.length() != 0) {
      k = st.indexOf('\n');
      if (k == 0) {
	result += "\n";
	st = st.substring(1,st.length());
	continue;
      }
      result += AdjustParagraph(st.substring(0,k))+"\n";
      st = st.substring(k+1, st.length());
    }
    return result;
  }

  private String AdjustParagraph (String name)
  {
    String p = new String();
    char c;
    int i;
    int r, l;
    String result = new String();

    p = name;

    if (p.length() < lwidth) {        // if length of a line < line width
      i = 0;
      c = p.charAt(i);
      while ( c == ' ' || c == '\t' ){// delete all spaces in the beginning
	i++;
	c = p.charAt(i);
      } 
      p = p.substring (i, p.length());
      return p;
    }

    i = lwidth - 1;

    while ( p.length() > i) {

      boolean flag = false;
      for (int j = i; j >= 0; j--) {   //check if line is regular, contains ' '
	if (p.charAt(j) == ' ' || p.charAt(j) == '\t')
	  flag = true;
      }
      
      if (!flag) {                     // if it is not, take it as it is
	result += p + "\n";
	p = p.substring (i+1, p.length());
	i = lwidth - 1;
	continue;
      }

      c = p.charAt(i);
                                       // If it is regular,
      while ( c == ' ' || c == '\t' ){ // find (l,r) segments of spaces
	i++;                           // in the neighborhood of the end
	c = p.charAt(i);               // of the line
      } 
      i = i-1;
      c = p.charAt(i);
      while ( c != ' ' && c != '\t' ) {
	i--;
	c = p.charAt(i);
      }
      r = i;
      c = p.charAt(i);
      while ( c == ' ' && c != '\t' ) {
	i--;
	c = p.charAt(i);
      }
      l = i+1;
      result += p.substring (0,l)+"\n";
      p = p.substring (r+1, p.length());
      i = lwidth - 1;
    }
    
    result += p;
    return result; 
  }

  private String GetString (String name)
  {
    if      (name.equals("greeting"))
      return greeting; 

    if (name.equals("regimegeneral"))
      return regimegeneral;

    if (name.equals("regimepoly"))
      return regimepoly;
    if (name.equals("regimepoints"))
      return regimepoints;
    if (name.equals("regimesegments"))
      return regimesegments;
    if (name.equals("regimechain"))
      return regimechain;
    if (name.equals("regime2poly"))
      return regime2poly;                    

    if (name.equals("opergeneral"))
      return opergeneral;
    if (name.equals("operarea"))
      return operarea;
    if (name.equals("operinpoly"))
      return operinpoly;
    if (name.equals("opersegseg"))
      return opersegseg;
    if (name.equals("opertriang"))
      return opertriang;
    if (name.equals("operchull2d"))
      return operchull2d;     
    if (name.equals("operdeltri"))
      return operdeltri;
    if (name.equals("operinters2p"))
      return operinters2p;
    if (name.equals("operminkow"))
      return operminkow;

    if (name.equals("editgeneral"))
      return editgeneral;
    if (name.equals("editfinish"))
      return editfinish;
    if (name.equals("editclear"))
      return editclear;
    if (name.equals("editdelete"))
      return editdelete;
    if (name.equals("editadd"))
      return editadd;
    if (name.equals("editmove"))
      return editmove;
    if (name.equals("editarmmove"))
      return editarmmove;

    if (name.equals("applgeneral"))
      return applgeneral;
    if (name.equals("applchull3d"))
      return applchull3d;
    if (name.equals("applptinpoly"))
      return applptinpoly;
    if (name.equals("applptsonsphere"))
      return applptsonsphere;

    return " ";
  }

  public boolean action(Event evt, Object o)
  {
    if (evt.target == ok) hide();
    return true;
  }

  public boolean handleEvent(Event evt) 
  {
    if (evt.id == Event.WINDOW_DESTROY)
      dispose();
    return super.handleEvent(evt);
  }

} //end Help Frame

