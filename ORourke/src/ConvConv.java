/*------------------------------------------------------------------------
Class ConvConv - used for computing intersection of two convex polygons
This code is described in "Computational Geometry in C" (Second Edition),
Chapter 7.  It is not written to be comprehensible without the
explanation in that book.
-------------------------------------------------------------------------*/

import java.awt.*;

public class ConvConv {

  private int n, m;
  private cVertexList P, Q;
  private cVertexList inters;  /* intersection of the two polygons */
  private cVertex a, b;        /* indices on P and Q (resp.) */
  private cPointi A, B;        /* directed edges on P and Q (resp.) */
  private int     cross;       /* sign of z-component of A x B */
  private int     bHA, aHB;    /* b in H(A); a in H(b). */
  private cPointi Origin;      /* (0,0) */
  private cPointd p;           /* double point of intersection */
  private cPointd q;           /* second point of intersection */
  private cInFlag inflag;      /* {Pin, Qin, Unknown}: which inside */
  private int     aa, ba;      /* # advances on a & b indices (after 1st inter.) */
  private boolean FirstPoint;  /* Is this the first point? (used to initialize).*/
  private cPointd p0;          /* The first point. */
  private int     code;        /* SegSegInt return code. */ 
  boolean intersection = true;

  ConvConv() 
  {
    
  }
  
  /* Equivalent of main() function in the C code,
   * returns false if polygons are not convex
   */
  public boolean Start(cVertexList p, cVertexList q)
  {
    intersection = true;
    this.P = new cVertexList();
    this.Q = new cVertexList();
    p.ListCopy(P);
    q.ListCopy(Q);
    if (!CheckForConvexity()) {
      System.out.println("Polygons are not convex...");
      return false;
    }
    else {
      System.out.println("Polygons are convex...");
      n = P.n;
      m = Q.n;
      inters = new cVertexList();
      ConvexIntersect( P, Q, n, m );
    }
    return true;
  }

  public void ClearConvConv()
  {
    P.ClearVertexList();
    Q.ClearVertexList();
    inters.ClearVertexList();
  }

  private boolean CheckForConvexity()
  {
    if (P.Ccw() != 1)	
      P.ReverseList();
    if (Q.Ccw() != 1)
      Q.ReverseList();
    if (!P.CheckForConvexity())
      return false;
      
    if (!Q.CheckForConvexity()) 
      return false;
    
    return true;
  }

  /*---------------------------------------------------------------------
   * Consult the book for explanations
   *--------------------------------------------------------------------*/
  private void ConvexIntersect( cVertexList P, cVertexList Q, int n, int m )
  /* P has n vertices, Q has m vertices. */
  {
    
    /* Initialize variables. */
    a = new cVertex(); 
    b = new cVertex(); 
    a = P.head; b = Q.head;
    aa = ba = 0;
    Origin = new cPointi();  /* (0,0) */
    inflag = new cInFlag(); 
    FirstPoint = true;
    cVertex a1, b1;
    A = new cPointi();
    B = new cPointi();
    p = new cPointd();
    q = new cPointd();
    p0 = new cPointd();
    
    do {
      /* System.out.println("Before Advances:a="+a.v.x+","+a.v.y+
	 ", b="+b.v.x+","+b.v.y+"; aa="+aa+", ba="+ba+"; inflag="+
	 inflag.f); */
      /* Computations of key variables. */
      a1 = a.prev;
      b1 = b.prev;
      
      SubVec( a.v, a1.v, A );
      SubVec( b.v, b1.v, B );
      
      cross = Origin.AreaSign( Origin, A, B );
      aHB   =   b1.v.AreaSign( b1.v, b.v, a.v );
      bHA   =   a1.v.AreaSign( a1.v, a.v, b.v );
      System.out.println("cross="+cross+", aHB="+aHB+", bHA="+bHA);
      
      /* If A & B intersect, update inflag. */
      code = a1.v.SegSegInt( a1.v, a.v, b1.v, b.v, p, q );
      System.out.println("SegSegInt: code = "+code);
      
      if ( code == '1' || code == 'v' ) {
	if ( inflag.f == inflag.Unknown && FirstPoint ) {
	  aa = ba = 0;
	  FirstPoint = false;
	  p0.x = p.x; p0.y = p.y;
	  InsertInters (p0.x, p0.y);
	}
	inflag = InOut( p, inflag, aHB, bHA );
	System.out.println("InOut sets inflag="+inflag.f);
      }
      
      /*-----Advance rules-----*/
      
      /* Special case: A & B overlap and oppositely oriented. */
      if ( ( code == 'e' ) && (Dot( A, B ) < 0) ) {
	InsertSharedSeg( p, q );
	return;
      }
      
      /* Special case: A & B parallel and separated. */
      if ( (cross == 0) && ( aHB < 0) && ( bHA < 0 ) ) {
	System.out.println("P and Q are disjoint.");
	return;
      }
      
      
      /* Special case: A & B collinear. */
      else if ( (cross == 0) && ( aHB == 0) && ( bHA == 0 ) ) {
	/* Advance but do not output point. */
	if ( inflag.f == inflag.Pin )
	  b = Advance( b, "ba", inflag.f == inflag.Qin, b.v );
	else
	  a = Advance( a, "aa", inflag.f == inflag.Pin, a.v );
      }
      
      /* Generic cases. */
      else if ( cross >= 0 ) {
	if ( bHA > 0)
	  a = Advance( a, "aa", inflag.f == inflag.Pin, a.v );
         else
	   b = Advance( b, "ba", inflag.f == inflag.Qin, b.v );
      }
      else /* if ( cross < 0 ) */{
	if ( aHB > 0)
	  b = Advance( b, "ba", inflag.f == inflag.Qin, b.v );
	else
	  a = Advance( a, "aa", inflag.f == inflag.Pin, a.v );
      }
      System.out.println("After advances:a=("+a.v.x+", "+a.v.y+
			 "), b=("+b.v.x+", "+b.v.y+"); aa="+aa+
			 ", ba="+ba+"; inflag="+inflag.f);
      
      /* Quit when both adv. indices have cycled, or one has cycled twice. */
    } while ( ((aa < n) || (ba < m)) && (aa < 2*n) && (ba < 2*m) );
    
    if ( !FirstPoint ) /* If at least one point output, close up. */
      InsertInters(p0.x, p0.y);
    
    /* Deal with special cases: not implemented. */
    if ( inflag.f == inflag.Unknown) {
      System.out.print("The boundaries of P and Q do not cross.");
      intersection = false;
    }
  }

  private void InsertInters(double x, double y)
  {
    cVertex v = new cVertex((int)x, (int)y);
    inters.InsertBeforeHead(v);
  }

  /*---------------------------------------------------------------------
    Prints out the double point of intersection, and toggles in/out flag.
    ---------------------------------------------------------------------*/
  cInFlag InOut( cPointd p, cInFlag inflag, int aHB, int bHA )
  {
    InsertInters( p.x, p.y );
    
    /* Update inflag. */
    if      ( aHB > 0) {
      inflag.f = inflag.Pin; return inflag;
    }
    else if ( bHA > 0) {
      inflag.f = inflag.Qin; return inflag;
    }
    else    /* Keep status quo. */
      return inflag;
  }

  /*---------------------------------------------------------------------
    Advances and prints out an inside vertex if appropriate.
    ---------------------------------------------------------------------*/
  private cVertex Advance( cVertex a, String counter, boolean inside, cPointi v )
  {
    if ( inside ) 
      InsertInters(v.x, v.y);
    if (counter.equals("aa"))
      aa++;
    else if (counter.equals("ba"))
      ba++;
    return  a.next;
  }

  /*---------------------------------------------------------------------
    a - b ==> c.
    ---------------------------------------------------------------------*/
  private void    SubVec( cPointi a, cPointi b, cPointi c )
  {
    c.x = a.x - b.x;
    c.y = a.y - b.y;
  }

  /*---------------------------------------------------------------------
    Returns the dot product of the two input vectors.
    ---------------------------------------------------------------------*/
  private double  Dot( cPointi a, cPointi b )
  {
    int i;
    double sum = 0.0;
    
    sum = a.x * b.x + a.y * b.y;

    return  sum;
  }
  
  public void  InsertSharedSeg( cPointd p, cPointd q )
  {
    InsertInters((int)p.x, (int)p.y);
    InsertInters((int)q.x, (int)q.y);
  }

  public void DrawIntersection(Graphics g, int w, int h, Color fillColor)
  {
    if (!intersection)
      return;
    else {
      inters.DrawPolygon(g,w,h,fillColor, Color.red, true);
    }
  }
}

class cInFlag {
  int f;
  static final int Pin     = -1;
  static final int Qin     =  1;
  static final int Unknown =  0;
  
  cInFlag()
  {
    f = Unknown;
  }
}
