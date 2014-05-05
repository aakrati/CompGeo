
/*----------------------------------------------------------------------
  class cChain (class polygon, integer).
  
  Doesn't have any direct correspondent in the C code as a class. 
  Nevertheless, it consists of a circular list (instance of cVertexList) 
  called "list", which contains all vertices in the chain.

  It has specific methods. The main one is Solven, corresponding to its 
  homonymous in the C code. This method determines if a certain point in the 
  plane is reachable by the arm formed from the chain that was introduced. 
  It also permits the normal editing for the points: deletion, moving, adding. 
  Most of the methods I used here are similar to the ones from the C code and 
  pretty straightfoerward. 

  For drawing, there is an extra feathure: the applet will display all the 
  points introduced at the previous step, although the C code just 
  "straightens" the arm in two or three links (depends on the position of the 
  point). The extra points dissapear after one editing, providing the fact 
  that the chain is "cleaned" after each edit operation. 

  There is also a pop-up window that warns the user when the point is out
  of reach. 



*********************************************************************/
import java.lang.Math;
import java.awt.*;

public class cChain
{	 
  static final int SCREENWIDTH = 350;	//max number of intersections 
  cVertexList list, listcopy;		//vertices of polygon
                                        // or pt of intersection
  private int inters[] = new int [SCREENWIDTH];
  
  private int linklen[] = new int[1000];
  private int linklenback[] = new int[1000];
  
  private cVertex newpoint, c, q;
  
  cVertex v1, v2;
  
  int L1, L2, L3;		//length of links between kinks
  int totlength;		//total length of all links
  int halflength;		//floor of half os total
  
  int i=0;
  int m; 			//index of median link
  int firstlinks=0;
  int nlinks;
  int nlinksback=0;
  private boolean toClose=false;
  int r1, r2;
  public int intersection = 0; 
  /*intersection is used for determining the number of intersections of the
  two circles: 0 for point out of reach, 1 for two tangent circles, 
  2 for 2 intersections, 3 for identical circles. */

  /*constructor*/
  public cChain( cVertexList list) 
    {
      this.list = list;
    }
  
  
  public void SetAChain(cPointd Jk, cPointi target)
    {
      cPointi vaux1;
      cPointi vaux2;
      vaux1 = new cPointi(list.head.v.x, list.head.v.y);
      vaux2 = new cPointi(target.x, target.y);
      list.ClearVertexList();
      list.SetVertex(vaux1.x, vaux1.y);
      list.SetVertex((int)(Jk.x+.5), (int)(Jk.y+.5));
      list.SetVertex(vaux2.x, vaux2.y);
      
    }
  
  public void ClearChain()
  /*for cleaning the chain after each edit operation*/
    {
      
      nlinksback=0;
      firstlinks=0;
      listcopy = new cVertexList();
      
    }

  public double Length (cPointi point1, cPointi point2)
  /*Computes the length of the link between two points
    Used for the Solven (and the subsequent) methods.*/
  
    {
      
      return(Math.sqrt((double)Math.abs(((point1.x-point2.x)*(point1.x-point2.x)
					     +(point1.y-point2.y)*(point1.y-point2.y)))));
      
    }
  
  
  //****************************************************

  public double   Length2( cPointi v )
  /* Returns the squared distance in between two points*/ 
    {
      double   ss;
      ss = 0;
      ss=(double)(v.x*v.x+v.y*v.y);
      return ss;
    }
  
  //********************************
  
  public void  SubVec( cPointi a, cPointi b, cPointi c )
  /* has the same result as  the SubVec method in the C code.*/
    {
      
      c.x = a.x - b.x;
      c.y = a.y - b.y;
      
    } 
  //**************************************
  
  public boolean Solven(int x, int y)
  
  /*Is called when the user drags the last point of the link or releases it. 
    Corresponds to the Solven method in C*/
  
    {
      int halflength;	//floor of half os total
      cPointi target;   //point for storing the target
      cPointd Jk;      // coords of kinked joint returned by Solve2 
      cPointi J1;      // Joint1 on x-axis 



      //create target
      target = new cPointi(x, y);
      
      //Compute length array and # of links
      cVertex v0;
      v1 = list.head;
      
      for (i=0; i<list.n-1; i++)
	{
	  linklen[i]=(int)(Length(v1.v, v1.next.v)+.5);
	  v1=v1.next;
	}
      nlinks=list.n-1;
      
      
      //Compute total&half length
      
      totlength=0;
      
      for (i=0; i<nlinks; i++)
	totlength+=linklen[i];
      halflength=totlength/2;
      
      //Find median link
      if (nlinks>2)
	{
	  L1=0;
	  for (m=0; m<nlinks; m++)
	    {
	      if ( (L1 + linklen[m])> halflength)
		break;
	      L1+=linklen[m];
	    }//end for
	  
	  L2=linklen[m];
	  L3=totlength-L1-L2;
	  firstlinks=m;
	  for(i=0;i<nlinks;i++)
	    linklenback[i]=linklen[i];
	  nlinksback=nlinks;
	  
	}
      else if (nlinks==2) 
	{
	  L1=linklen[0];
	  L2=linklen[1];
	  L3=0;
	}
      else  
	{
	  System.out.println("Just one link!!!");
	  L1=L2=L3=0;
	}
      
      if ((nlinks==3)&&(nlinksback==0)) nlinksback=3;
      if (nlinks == 2)
	{
	  Jk=new cPointd(0,0);
	  if ( Solve2( L1, L2, target, Jk ) ) 
	    {
	      System.out.println("Solve2 for 2 links: link1= "+L1+", link2= "+L2+", joint=\n");
	      LineTo_d( Jk );
	      SetAChain(Jk, target);
	      return true;
	    }
	  else return false;
	}//end if nlinks==2
      else 
	{
	  if (Solve3 (L1, L2, L3, target))
	    return true;    
	  else return false;
	}
      
    }//end Solve
  
  public boolean Solve3 (int L1, int L2, int L3, cPointi target0)
  
    {
      cPointi target;
      cPointd Jk;      // coords of kinked joint returned by Solve2 
      cPointi J1;      // Joint1 on x-axis 
      cPointi Ttarget; // translated target
      
      cPointi vaux1;
      cPointi vaux2;
      
      target= new cPointi(target0.x, target0.y);
      
      
      System.out.println("==>Solve3: links = "+ L1+", "+ L2+", "+L3);
      
      Jk = new cPointd (0,0);
      
      if ( Solve2( L1 + L2, L3, target, Jk ) ) {
	firstlinks++;  
	nlinks=2;
	
	System.out.println("Solve3: link1="+(L1+L2)+", link2="+L3+", joint=\n");
	LineTo_d( Jk );
	SetAChain(Jk,target);
	return true;
      }
      else if ( Solve2( L1, L2 + L3, target, Jk ) ) {
	System.out.println("Solve3: link1= "+L1+", link2= "+(L2+L3)+", joint=\n");
	nlinks=2;
	LineTo_d( Jk );
	SetAChain(Jk,target);
	return true;
      }                                                                  
      else {   // pin J0 to 0. 
	// Shift so J1 is origin. 
	//J1.x = L1;   J1.y = 0;
	J1=new cPointi(L1, 0);
	Ttarget = new cPointi (0,0);
	SubVec( target, J1, Ttarget );
	if ( Solve2( L2, L3, Ttarget, Jk ) ) {
	  // Shift solution back to origin. 
	  Jk.x += L1;
	  System.out.println("Solve3: link1="+L1+", link2= "+L2+", link3= "+L1+", joints=\n");
	  nlinks=3;
	  LineTo_i( J1 );
	  LineTo_d( Jk );
	  SetAChain(Jk,target);
	  cVertex VJ1 = new cVertex(list.head.v.x+J1.x, list.head.v.y);
	  list.InsertBefore(VJ1, list.head.next);
	  return true;
	}
	else
	  return false;
      }
    }//end Solve3
  
  public boolean Solve2( int L1, int L2, cPointi target, cPointd J )
    {
      cPointi c1 = new cPointi(list.head.v.x, list.head.v.y);  // center of circle 1 
      int nsoln;           // # of solns: 0,1,2,3(infinite) 
      nsoln = TwoCircles( c1, L1, target, L2, J );
      return   (nsoln != 0);
    }// end Solve2


  //---------------------------------------------------------------------
  //TwoCircles finds an intersection point between two circles.
  //General routine: no assumptions. Returns # of intersections; point in p.
  //---------------------------------------------------------------------
  
  public int TwoCircles( cPointi c1, int r1, cPointi c2, int r2, cPointd p )
    {
      cPointi c;
      cPointd q;
      int nsoln = -1;     
      // Translate so that c1={0,0}. 
      c = new cPointi (0,0);
      SubVec( c2, c1, c );
      q = new cPointd (0,0);
      nsoln = TwoCircles0a( r1, c, r2, p );//p instead of 
      
      // Translate back. 
      p.x = p.x + c1.x;
      p.y = p.y + c1.y;
      return nsoln;
    }
  
  //---------------------------------------------------------------------
  //TwoCircles0a assumes that the first circle is centered on the origin.
  //Returns # of intersections: 0, 1, 2, 3 (inf); point in p.
  //----------------------------------------------------------------------- 
  public int TwoCircles0a( int r1, cPointi c2, int r2, cPointd p )
    {
      double dc2;              // dist to center 2 squared 
      double rplus2, rminus2;  // (r1 +/- r2)^2 
      double f;                // fraction along c2 for nsoln=1 
      
      // Handle special cases. 
      dc2 = Length2( c2 );
      rplus2  = (r1 + r2) * (r1 + r2);
      rminus2 = (r1 - r2) * (r1 - r2);                    
      
      // No solution if c2 out of reach + or -. 
      if ( ( dc2 > rplus2 ) || ( dc2 < rminus2 ) )
	return   0;
      
      // One solution if c2 just reached. 
      // Then solution is r1-of-the-way (f) to c2. 
      if ( dc2 == rplus2 ) {
	f = r1 / (double)(r1 + r2);
	p.x = f * c2.x;   p.y = f * c2.y;
	return 1;
      }                                                
      if ( dc2 == rminus2 ) {
	if ( rminus2 == 0 ) {   // Circles coincide. 
	  p.x = r1;    p.y = 0;
	  return 3;
	}
	f = r1 / (double)(r1 - r2);
	p.x = f * c2.x;   p.x = f * c2.y;
	return 1;
      }                                                       
      // Two intersections. 
      
      int auxint = TwoCircles0b( r1, c2, r2, p );
      return auxint;
    }//end TwoCircles0a
  
  //---------------------------------------------------------------------
  //TwoCircles0b also assumes that the 1st circle is origin-centered.
  //---------------------------------------------------------------------         
  public int TwoCircles0b( int r1, cPointi c2, int r2, cPointd p )
    {
      double a2;          // center of 2nd circle when rotated to x-axis  
      cPointd q;          // one solution when c2 on x-axis  
      double cost, sint;  // sine and cosine of angle of c2  
      
      // Rotate c2 to a2 on x-axis.  
      a2 = Math.sqrt( Length2( c2 ) );
      cost = c2.x / a2;
      sint = c2.y / a2;                                                      
      q= new cPointd(0,0);
      TwoCircles00( r1, a2, r2, q );
      
      // Rotate back  
      p.x =  cost * q.x + -sint * q.y;
      p.y =  sint * q.x +  cost * q.y;
      
      return 2;
    }                
                             
  //---------------------------------------------------------------------
  //TwoCircles00 assumes circle centers are (0,0) and (a2,0).
  //--------------------------------------------------------------------- 
  public void  TwoCircles00( int r1, double a2, int r2, cPointd p )
    {
      double r1sq, r2sq;
      r1sq = r1*r1;
      r2sq = r2*r2;
      
      // Return only positive-y soln in p.  
      p.x = ( a2 + ( r1sq - r2sq ) / a2 ) / 2;
      p.y = Math.sqrt( r1sq - p.x*p.x );
    }//end TwoCircles00
  

  /*Method used for cretaing the "extra-points" that will be displayed
    on the screen after the arm is straightened. Called from DrawPoints */

  public cVertexList MakePoints (int lo, int hi1, int hi2, cVertex first, cVertex last, cVertexList listcopy)

    {
      
      double xaux;     //auxiliary variable for storin the info
      int lenaux=0;    //auxiliary variable for storing the length of the
      //current link
      cPointi v1 = new cPointi(0,0);   //aux variable for computing the values 
      //of the new points
      int sum=0;   //the sum of the previous link lengths
      
      for (i=lo;i<hi1;i++)
	{
	  lenaux+=linklenback[i];
	}
      sum=0;
      
      for (i=lo;i<hi2;i++)
	{
	  sum+=linklenback[i];
	  xaux=sum/(double)lenaux;
	  v1.x=(int)(.5+(1-xaux)*first.v.x+xaux*last.v.x);
	  v1.y=(int)(.5+(1-xaux)*first.v.y+xaux*last.v.y);
	  listcopy.SetVertex(v1.x, v1.y);
	  
	}//end for
            
      return listcopy;
    }
  
  public void DrawDots(Graphics gContext, int w, int h)
    {
      
      cVertexList listcopy = new cVertexList();
      listcopy.SetVertex(list.head.v.x, list.head.v.y);
      
      if (nlinks==3)
	{
	  /*for the first link:*/
	  listcopy = MakePoints(0, firstlinks, firstlinks-1, list.head, list.head.next, listcopy);
	  /*set the middle link*/
	  listcopy.SetVertex(list.head.next.v.x,list.head.next.v.y );
	  listcopy.SetVertex(list.head.next.next.v.x, list.head.next.next.v.y);
	  /* for the last link, the third one*/
       	  listcopy = MakePoints(firstlinks+1, nlinksback, nlinksback,  list.head.next.next, list.head.prev, listcopy);       
	  
	}
      
      else
	{  
	  if (nlinksback>2)
	    /*if we have any extra - points*/
	    {
	      /*first link:*/
	      listcopy = MakePoints(0, firstlinks, firstlinks-1, list.head, list.head.next, listcopy);
	      /*set the middle point:*/
	      listcopy.SetVertex(list.head.next.v.x,list.head.next.v.y );
	      /*set the last link*/
	      listcopy = MakePoints(firstlinks, nlinksback+1, nlinksback-1, list.head.prev.prev, list.head.prev, listcopy);
	      
	      
	    }//end if nlinksback > 2
	}// end else
      /*set the last point and draw everything*/
      listcopy.SetVertex(list.head.prev.v.x,list.head.prev.v.y );
      listcopy.DrawPoints(gContext, w, h);  
      
    }
  
  
  //********************************************************
  /*series of methods corresponding to the C code; just printing on the
    standard output */
  public void LineTo_i(cPointi p)
    {
      System.out.println(" Line to i"+p.x+", "+p.y);
    }
  
  public void MoveTo_i(cPointi p)
    {
      System.out.println(" Move to i"+p.x+", "+p.y);
    }
  
  public void LineTo_d(cPointd p)
    {
      System.out.println(" Line to d"+p.x+", "+p.y);
    }
  
  
}//end class

