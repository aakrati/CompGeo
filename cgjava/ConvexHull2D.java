

/*-------------------------------------------------------------------
  Class for computing the CH in 2D
  No direct class in the C code. 
  Takes as input a list of points and produces a list called "top" 
  which contains the points on the CH.
  Methods:
  * RunHull corresponds to the "main" function in the C code; all the
  other methods are "translations" from C to Java of the code. 
  * qsort implements a QuickSort Algorithm in Java so that it uses the 
  Compare function. 
------------------------------------------------------------------*/

import java.awt.*;
import java.lang.Math;

class ConvexHull2D{
  
  private cVertexList list, top;
  private cVertex newpoint;
  private int ndelete = 0;
  private int i = 0;
  
  public ConvexHull2D( cVertexList list) 
    {
      this.list = list;
    }
  
  public void ClearHull()
    {
      top = new cVertexList();
      
    }
  
  public void RunHull()
    {
      
      //initialization:
      
      cVertex v = new cVertex();
      v=list.head;
      for (i=0;i<list.n;i++)
	{
	  v.vnum=i;
	  v=v.next;
	}
      //*********************
      FindLowest();
      qsort(list);
      if (ndelete>0) Squash();
      top = Graham();
      top.PrintVertices();
    }
  


    /*---------------------------------------------------------------------
      Performs the Graham scan on an array of angularly sorted points P.
      ---------------------------------------------------------------------*/

  private cVertexList Graham()
    {
      cVertexList top;
      int i;
      cVertex p1, p2;  /* Top two points on stack. */
      
      
      /* Initialize stack. */
      top = new cVertexList();
      cVertex v1 = new cVertex(list.head.v.x, list.head.v.y);
      v1.vnum = list.head.vnum;
      v1.mark = list.head.mark;
      
      cVertex v2 = new cVertex(list.head.next.v.x, list.head.next.v.y);
      v2.vnum = list.head.next.vnum;
      v2.mark = list.head.next.mark;
      
      
      Push ( v1, top );
      Push ( v2, top );
      
      // Bottom two elements will never be removed. 
      i = 2;
      
      while ( i < list.n ) {
	cVertex v3 = new cVertex(list.GetElement(i).v.x,list.GetElement(i).v.y);
	v3.mark = list.GetElement(i).mark;
	v3.vnum = list.GetElement(i).vnum;
	
	if ( v1.v.Left( top.head.prev.v , top.head.prev.prev.v, v3.v ) ) {
	  Push ( v3, top );
	  i++;
	} else    
	  {
	    if (top.n>2)
	      {
		Pop( top );
	      }
	  }
	
      }
      
      return top;
      
    }
  
  /*---------------------------------------------------------------------
    Squash removes all elements from list marked delete.
    ---------------------------------------------------------------------*/
  private void   Squash()
    {
      cVertex v=new cVertex();
      v=list.head;
      for (i=0; i<list.n;i++)
	{
	  if (v.mark) list.Delete(v);
	  v=v.next;
	}
    }

  
  private void Sort(cVertexList a, int lo0, int hi0) 
    {
      if (lo0 >= hi0) {
	return;
      }
      cVertex mid = new cVertex();
      mid = a.GetElement(hi0);
      int lo = lo0;
      int hi = hi0-1;
      while (lo <= hi) 
	{
	  while (lo<=hi && ((Compare(a.GetElement(lo), mid)==1)||(Compare(a.GetElement(lo), mid)==0))) {
	    lo++;
	  }
	  
	  while (lo<=hi && ((Compare(a.GetElement(hi), mid)==-1)||(Compare(a.GetElement(hi), mid)==0))) {
	    hi--;
	  }
	  
	  if (lo < hi)
	    {
	      Swap(a.GetElement(lo),a.GetElement(hi));
	    }
	  
	}
      Swap(a.GetElement(lo),a.GetElement(hi0));
      Sort(a, lo0, lo-1);
      Sort(a, lo+1, hi0);
    }
  
  
  private void qsort(cVertexList a) {
    Sort(a, 1, a.n-1);
  }
  
/*---------------------------------------------------------------------
Compare: returns -1,0,+1 if p1 < p2, =, or > respectively;
here "<" means smaller angle.  Follows the conventions of qsort.
---------------------------------------------------------------------*/
  private int Compare(  cVertex tpi, cVertex tpj )
    {
      int a;             //area 
      int x, y;          //projections of ri & rj in 1st quadrant 
      cVertex pi, pj;
      pi = tpi;
      pj = tpj;
      cVertex myhead = new cVertex();
      myhead = list.head;
      a = myhead.v.AreaSign( myhead.v, pi.v, pj.v );
      if (a > 0)
	return -1;
      else if (a < 0)
	return 1;
      else { // Collinear with list.head
	x =  Math.abs( pi.v.x -  list.head.v.x ) - Math.abs( pj.v.x -  list.head.v.x );
	y =  Math.abs( pi.v.y -  list.head.v.y ) - Math.abs( pj.v.y -  list.head.v.y );
	ndelete++;
	
	if ( (x < 0) || (y < 0) ) {
	  pi.mark = true;
	  return -1;
	}
	else if ( (x > 0) || (y > 0) ) {
	  pj.mark = true;
	  return 1;
	}
	else { // points are coincident 
	  
	  if (pi.vnum > pj.vnum)
	    pj.mark = true;
	  else 
	    pi.mark = true;
	  return 0;
	  
	}
      }
    }
  
/*---------------------------------------------------------------------
FindLowest finds the rightmost lowest point and swaps with 0-th.
The lowest point has the min y-coord, and amongst those, the
max x-coord: so it is rightmost among the lowest.
---------------------------------------------------------------------*/
  private  void   FindLowest()
    {
      int i;
      int m = 0;   // Index of lowest so far. 
      cVertex v1;
      v1=list.head.next;
      
      for ( i = 1; i <list.n; i++ )
	{
	  if ( (list.head.v.y <  v1.v.y) ||
	       ((v1.v.y == list.head.v.y) && (v1.v.x > list.head.v.x)) ) 
	    {
	      Swap(list.head, v1); 
	    }
	  v1=v1.next;
	  
	}
    }
  
  private void Swap (cVertex first, cVertex second )
    {
      cVertex temp=new cVertex();
      
      temp=new cVertex(first.v.x, first.v.y);
      temp.vnum = first.vnum;
      temp.mark = first.mark;
      
      list.ResetVertex(first, second.v.x, second.v.y, second.vnum, second.mark);
      list.ResetVertex(second, temp.v.x, temp.v.y, temp.vnum, temp.mark);
      
    }
  
  private void Push(cVertex p, cVertexList top)
    {
      //simulating a stack behavior for cVertexList list
      //Push procedure
      top.InsertBeforeHead(p);
    }
  
  private void Pop(cVertexList top)
    {
      //simulating a stack behavior for cVertexList list
      //Pop procedure
      cVertex last=new cVertex();
      //last=top0.head.prev;
      top.Delete(top.head.prev);
    }
  
  public void DrawHull(Graphics gContext, int w, int h)
    {
      
      if (list.head!=null)
	list.DrawPoints(gContext, w, h);
      
      if (top.n == 0 || top.head==null)
	System.out.println("No drawing is possible.");
      else {
	cVertex v1 = top.head;
	
      	if(top.n>2)  
	  {
	    do {
	      gContext.drawLine(v1.v.x, v1.v.y, v1.next.v.x, v1.next.v.y);
	      v1 = v1.next;
	    } while (v1 != top.head);
	    
	  }
	
      }//end else     
    }//end draw
  
}//end class 
