/*
class InPoly

Determines if a point is inside or outside a polyhedron. 
The code is really close to the one in C. The structures are also similar. 
The input is very strict though.
*/


import java.awt.*;
import java.io.*;

public class InPolyh {


  private static final int EXIT_FAILURE=1;
  private static final int X = 0;
  private static final int Y = 1;
  private static final int Z = 2;

  private static final int PMAX = 10000;
  private final int MAX_INT = Integer.MAX_VALUE;
  private static final int SAFE = 1000000;
  private static final int DIM = 3;                  /* Dimension of points */
  tPointi Vertices[];        /* All the points */
  tPointi Faces[];           /* Each triangle face is 3 indices */
  int check = 0;
  tPointi Box[][];          /* Box around each face */
  int n, f;
  int m;
  double D = 0;


 public static void main(String args[]) throws IOException {
    InPolyh ip = new InPolyh();
 }


InPolyh() throws IOException {

  int  F = 0;
  tPointi q, bmin, bmax;
  int radius;

  String s;
  char c;
  char line[] = new char[20];
  int i = 0;
  int x, y, z;
  boolean flag;
  int counter;
  
  Vertices = new tPointi[PMAX];
  Faces = new tPointi[PMAX];
  Box = new tPointi[PMAX][2];

  n = ReadVertices();
  F = ReadFaces();
  VerifVertices();

  /* Initialize the bounding box */
  bmin = new tPointi();
  bmax = new tPointi();

  for ( i = 0; i < DIM; i++ )
    bmin.p[i] = bmax.p[i] = Vertices[0].p[i];

  radius = ComputeBox( n, bmin, bmax );

  System.out.println("radius=" + radius);

 System.out.println("Please input query point");
 i =0;
 counter = 0;
 flag = false;

 try{
          System.out.println("\n\nInput query point:\nCoord-s must be seperated by a *tab*\n"+
		       "ENTER after each point"+"\nTo finish input type end + "+
		       "ENTER at the end"+
		       "\nExample:\n17      23      123\n34      5      1\nend\n"+
		       "-----------------start entering data-------------------");
    do {

      do {
	c = (char) System.in.read();
	line[i] = c;
	i++;
      } while (c !='\n' );
      s = new String(line);
      s = s.substring(0,i-1);
      if (s.equals("end"))
	break;
      flag = false;
      counter = 0;
      for (int j=0; j < s.length(); j++) {
	if (s.charAt(j) == '\t') 
	  counter++; 
	if (counter == 2) {
	  flag = true; break; 
	}
      }
      if (flag) {
	int t = s.indexOf('\t');
	int t1 = s.lastIndexOf('\t');
	x = Integer.parseInt(s.substring(0,t));
	y = Integer.parseInt(s.substring(t+1,t1));
	z = Integer.parseInt(s.substring(t1+1,s.length()));
	q = new tPointi(x, y, z);

	//	q.x = x;
	//	q.y = y;
	//	q.z = z;
	i=0;
      }
      else 
	break;

      char ans = InPolyhedron(F, q, bmin, bmax, radius);
      System.out.println("InPolyhedron returned:  "+ans);

    } while (!s.equals("end"));
 }
catch (NumberFormatException e) {System.out.println ("Invalid input"); System.exit(1);}
}


//**********************
 public void VerifVertices() 
  {
   
    for (int v = 0; v < n; v++)
      {
	if (( Math.abs(Vertices[v].p[X]) > SAFE ) || ( Math.abs(Vertices[v].p[Y]) > SAFE ) || ( Math.abs(Vertices[v].p[Z]) > SAFE ) ) 
	  {
	    System.out.println("Coordinate of vertex below might be too large...");
	    System.out.println(v);
	  }
      }
  }

//***********************
/*
  This function returns a char:
    'V': the query point a coincides with a Vertex of polyhedron P.
    'E': the query point a is in the relative interior of an Edge of polyhedron P.
    'F': the query point a is in the relative interior of a Face of polyhedron P.
    'i': the query point a is strictly interior to polyhedron P.
    'o': the query point a is strictly exterior to( or outside of) polyhedron P.
*/

char InPolyhedron( int F, tPointi q, tPointi bmin, tPointi bmax, int radius )
{
   tPointi r;  /* Ray endpoint. */
   tPointd p;  /* Intersection point; not used. */
   int f, k = 0, crossings = 0;
   char code = '?';
 
   r  = new tPointi();
   p  = new tPointd();

   /* If query point is outside bounding box, finished. */
   if ( !InBox( q, bmin, bmax ) )
      return 'o';
  
   LOOP:
   while( k++ < F ) {
      crossings = 0;
  
      RandomRay( r, radius ); 
      AddVec( q, r ); 
      System.out.println("Ray endpoint: ("+ r.p[0]+" , "+r.p[1]+" , "+r.p[2]+" )" );
  
      for ( f = 0; f < F; f++ ) {  /* Begin check each face */
         if ( BoxTest( f, q, r ) == '0' ) {
              code = '0';
	      System.out.println("BoxTest = 0!");
         }
         else code = SegTriInt( Faces[f], q, r, p );
	 System.out.println( "Face = "+f+": BoxTest/SegTriInt returns "+ code );

         /* If ray is degenerate, then goto outer while to generate another. */
         if ( code == 'p' || code == 'v' || code == 'e' ) {
	    System. out.println("Degenerate ray");
            continue LOOP;
         }
   
         /* If ray hits face at interior point, increment crossings. */

         else if ( code == 'f' ) {
            crossings++;
            System. out.println( "crossings = "+ crossings );
         }

         /* If query endpoint q sits on a V/E/F, return that code. */
         else if ( code == 'V' || code == 'E' || code == 'F' )
            return( code );

         /* If ray misses triangle, do nothing. */
         else if ( code == '0' )
            ;

         else 
	   {
	     System. out.println ("Error" );
	     System.exit(1);
	   }

      } /* End check each face */

      /* No degeneracies encountered: ray is generic, so finished. */
      break;

   } /* End while loop */
 
   System. out.println( "Crossings at the end = "+ crossings );
   /* q strictly interior to polyhedron iff an odd number of crossings. */
   if( ( crossings % 2 ) == 1 )
      return   'i';
   else return 'o';
}
  

public int ComputeBox( int F, tPointi bmin, tPointi bmax )
{
  int i, j, k;
  double radius;
  
  for( i = 0; i < F; i++ )
    for( j = 0; j < DIM; j++ ) {
      if( Vertices[i].p[j] < bmin.p[j] )
	bmin.p[j] = Vertices[i].p[j];
      if( Vertices[i].p[j] > bmax.p[j] ) 
	bmax.p[j] = Vertices[i].p[j];
    }
  
  radius = Math.sqrt( Math.pow( (double)(bmax.p[X] - bmin.p[X]), 2.0 ) +
                 Math.pow( (double)(bmax.p[Y] - bmin.p[Y]), 2.0 ) +
                 Math.pow( (double)(bmax.p[Z] - bmin.p[Z]), 2.0 ) );
  System. out.println("radius =  "+radius);

  return (int)(( radius +1 +.5)) + 1;
}

/* Return a random ray endpoint */

void RandomRay( tPointi ray, int radius )
{
  double x, y, z, w, t;

  /* Generate a random point on a sphere of radius 1. */
  /* the sphere is sliced at z, and a random point at angle t
     generated on the circle of intersection. */

  z = 2.0 * (double) Math.random() - 1.0;
  t = 2.0 * Math.PI * (double) Math.random();
  w = Math.sqrt( 1 - z*z );
  x = w * Math.cos( t );
  y = w * Math.sin( t );
  
  ray.p[X] = (int) ( radius * x + .5);
  ray.p[Y] = (int) ( radius * y + .5);
  ray.p[Z] = (int) ( radius * z +.5);
  
  System. out.println( "RandomRay returns"+ ray.p[X]+" , "+ ray.p[Y]+" , "+ ray.p[Z] );
}

public void AddVec( tPointi q, tPointi ray )
{
  int i;
  
  for( i = 0; i < DIM; i++ )
    ray.p[i] = q.p[i] + ray.p[i];
}

public boolean InBox( tPointi q, tPointi bmin, tPointi bmax )
{
  int i;

  if( ( bmin.p[X] <= q.p[X] ) && ( q.p[X] <= bmax.p[X] ) &&
      ( bmin.p[Y] <= q.p[Y] ) && ( q.p[Y] <= bmax.p[Y] ) &&
      ( bmin.p[Z] <= q.p[Z] ) && ( q.p[Z] <= bmax.p[Z] ) )
    return true;
  return false;
}
    

/*---------------------------------------------------------------------
    'p': The segment lies wholly within the plane.
    'q': The q endpoint is on the plane (but not 'p').
    'r': The r endpoint is on the plane (but not 'p').
    '0': The segment lies strictly to one side or the other of the plane.
    '1': The segement intersects the plane, and 'p' does not hold.
---------------------------------------------------------------------*/
public char	SegPlaneInt( tPointi T, tPointi q, tPointi r, tPointd p, int m)
{
    tPointd N; 
    int D0 = 0;
    tPointi rq;
    double num, denom, t;
    int i;
    
    N  = new tPointd();
    rq  = new tPointi();
    
    m = PlaneCoeff( T, N, D0 );
    
    System.out.println("m= "+m+"; plane=( "+N.p[X]+" , "+N.p[Y]+" , "+N.p[Z]+" , "+D+" )");
    num = D - Dot( q, N );
    SubVec( r, q, rq );
    denom = Dot( rq, N );
    
    System.out.println("SegPlaneInt: num="+num+" , denom= "+denom );

    if ( denom == 0.0 ) {  /* Segment is parallel to plane. */
       if ( num == 0.0 )   /* q is on plane. */
           return 'p';
       else
           return '0';
    }
    else
       t = num / denom;
    System.out.println("SegPlaneInt: t= "+ t );

    System.out.println("p in seg plane int is: p=()");
    for( i = 0; i < DIM; i++ )
      {
	p.p[i] = q.p[i] + t * ( r.p[i] - q.p[i] );
	System.out.println(p.p[i]);
      }
    
    
    if ( (0.0 < t) && (t < 1.0) )
      return '1';
    else if ( num == 0.0 )   /* t == 0 */
      return 'q';
    else if ( num == denom ) /* t == 1 */
      return 'r';
    else return '0';
}
/*---------------------------------------------------------------------
Computes N & D and returns index m of largest component.
---------------------------------------------------------------------*/
public int	PlaneCoeff( tPointi T, tPointd N, double D0 )
{
    int i;
    double t;              /* Temp storage */
    double biggest = 0.0;  /* Largest component of normal vector. */
    m = 0;             /* Index of largest component. */

    NormalVec( Vertices[T.p[0]], Vertices[T.p[1]], Vertices[T.p[2]], N );
    System.out.println("PlaneCoeff: N=()"+ N.p[X]+" , "+N.p[Y]+" , "+N.p[Z]);
    D = Dot( Vertices[T.p[0]], N );
    System.out.println("D should be in planecoeff"+D);

    /* Find the largest component of N. */
    for ( i = 0; i < DIM; i++ ) {
      t = (float)(Math.abs( N.p[i] ));
      if ( t > biggest ) {
        biggest = t;
        m = i;
      }
    }
    return m;
}
/*---------------------------------------------------------------------
Reads in the number and coordinates of the vertices of a polyhedron
from stdin, and returns n, the number of vertices.
---------------------------------------------------------------------*/
public int ReadVertices ()
{


  int  n = 0;
  String s;
  char c;
  char line[] = new char[20];
  int i = 0;
  int x, y, z;
  boolean flag;
  int counter;

  try{

   //*************
 System.out.println("\n\nInput points:\nCoord-s must be seperated by a *tab*\n"+
		       "ENTER after each point"+"\nTo finish input type end + "+
		       "ENTER at the end"+
		       "\nExample:\n17      23      123\n34      5      1\nend\n"+
		       "-----------------start entering data-------------------");
    do {
      do {
	c = (char) System.in.read();
	line[i] = c;
	i++;
      } while (c !='\n' );
      s = new String(line);
      s = s.substring(0,i-1);
      if (s.equals("end"))
	break;
      flag = false;
      counter = 0;
      for (int j=0; j < s.length(); j++) {
	if (s.charAt(j) == '\t') 
	  counter++; 
	if (counter == 2) {
	  flag = true; break; 
	}
      }
      if (flag) {
	int t = s.indexOf('\t');
	int t1 = s.lastIndexOf('\t');
	x = Integer.parseInt(s.substring(0,t));
	y = Integer.parseInt(s.substring(t+1,t1));
	z = Integer.parseInt(s.substring(t1+1,s.length()));
	Vertices[n] = new tPointi(x,y,z);
	n++;
	i=0;
      }
      else 
	break;
    } while (!s.equals("end"));
  }
  catch (IOException e){System.out.println("Invalid input");System.exit(1);};
   //*************
   return n;
}

/*---------------------------------------------------------------------
a - b ==> c.
---------------------------------------------------------------------*/
public void    SubVec( tPointi a, tPointi b, tPointi c )
{
   int i;

   for( i = 0; i < DIM; i++ )
      c.p[i] = a.p[i] - b.p[i];
}


/*---------------------------------------------------------------------
Returns the dot product of the two input vectors.
---------------------------------------------------------------------*/
public double	Dot( tPointi a, tPointd b )
{
    int i;
    double sum = 0.0;

    for( i = 0; i < DIM; i++ )
       sum += a.p[i] * b.p[i];

    return  sum;
}


/*---------------------------------------------------------------------
Compute the cross product of (b-a)x(c-a) and place into N.
---------------------------------------------------------------------*/
public void	NormalVec( tPointi a, tPointi b, tPointi c, tPointd N )
{
    N.p[X] = ( c.p[Z] - a.p[Z] ) * ( b.p[Y] - a.p[Y] ) -
           ( b.p[Z] - a.p[Z] ) * ( c.p[Y] - a.p[Y] );
    N.p[Y] = ( b.p[Z] - a.p[Z] ) * ( c.p[X] - a.p[X] ) -
           ( b.p[X] - a.p[X] ) * ( c.p[Z] - a.p[Z] );
    N.p[Z] = ( b.p[X] - a.p[X] ) * ( c.p[Y] - a.p[Y] ) -
           ( b.p[Y] - a.p[Y] ) * ( c.p[X] - a.p[X] );
}

/* Reads in the number of faces of the polyhedron and their indices from stdin,
    and returns the number n. */


public int ReadFaces(  )
{

  int	j,k, f = 0;
  int   w = 0; 
  String s;
  char c;
  char line[] = new char[20];
  int i = 0;
  int x, y, z;
  boolean flag = false;
  int counter = 0;
  
 System.out.println("\n\nInput faces:\nIndices must be seperated by a *tab* and start from 0 to n-1\n"+
		       "ENTER after each point"+"\nTo finish input type end + "+
		       "ENTER at the end"+
		       "\nExample:\n17      23      123\n34      5      1\nend\n"+
		       "-----------------start entering data-------------------");

  
  try{
    do {
      do {
	c = (char) System.in.read();
	line[i] = c;
	i++;
      } while (c !='\n' );
      s = new String(line);
      s = s.substring(0,i-1);
      if (s.equals("end"))
	break;
      flag = false;
      counter = 0;
      for (j=0; j < s.length(); j++) {
	if (s.charAt(j) == '\t') 
	  counter++; 
	if (counter == 2) {
	  flag = true; break; 
	}
      }
      if (flag) {
	int t = s.indexOf('\t');
	int t1 = s.lastIndexOf('\t');
	x = Integer.parseInt(s.substring(0,t));
	y = Integer.parseInt(s.substring(t+1,t1));
	z = Integer.parseInt(s.substring(t1+1,s.length()));
	Faces[f] = new tPointi(x,y,z);
	
	Box[f][0] = new tPointi(Vertices[ Faces[f].p[0] ].p[0], Vertices[ Faces[f].p[0] ].p[1], Vertices[ Faces[f].p[0] ].p[2]);
	Box[f][1] = new tPointi(Vertices[ Faces[f].p[0] ].p[0], Vertices[ Faces[f].p[0] ].p[1], Vertices[ Faces[f].p[0] ].p[2]);

	//	for ( j=0; j < 3; j++ ) {
	// Box[f][0].p[j] = Vertices[ Faces[f].p[0] ].p[j];
	// Box[f][1].p[j] = Vertices[ Faces[f].p[0] ].p[j];
	//	}


	/* Check k=1,2 vertices of face. */
	for ( k=1; k < 3; k++ )
	  for ( j=0; j < 3; j++ ) {
	    w = Vertices[ Faces[f].p[k] ].p[j];
	    if ( w < Box[f][0].p[j] ) Box[f][0].p[j] = w;
	    if ( w > Box[f][1].p[j] ) Box[f][1].p[j] = w;
	  }
	
	f++;
	i=0;
      }
      else 
	break;
    } while (!s.equals("end"));
  }
  catch (IOException e){};
    //*****************************

    /* Compute bounding box. */
    /* Initialize to first vertex. */


    //***************************
  System.out.println("faces read");
  return f;
}

/* Assumption: p lies in the plane containing T.
    Returns a char:
     'V': the query point p coincides with a Vertex of triangle T.
     'E': the query point p is in the relative interior of an Edge of triangle T.
     'F': the query point p is in the relative interior of a Face of triangle T.
     '0': the query point p does not intersect (misses) triangle T.
*/

public char 	InTri3D( tPointi T, int m, tPointi p )
{
   int i;           /* Index for X,Y,Z           */
   int j;           /* Index for X,Y             */
   int k;           /* Index for triangle vertex */
   tPointi pp;      /* projected p */
   tPointi Tp[];   /* projected T: three new vertices */

   pp  = new tPointi();
   Tp = new tPointi[3];
   Tp[0] = new tPointi();
   Tp[1] = new tPointi();
   Tp[2] = new tPointi();

   
   /* Project out coordinate m in both p and the triangular face */
   j = 0;
   for ( i = 0; i < DIM; i++ ) {
     if ( i != m ) {    /* skip largest coordinate */
       pp.p[j] = p.p[i];
       for ( k = 0; k < 3; k++ )
	 Tp[k].p[j] = Vertices[T.p[k]].p[i];
       j++;
     }
   }

   return( InTri2D( Tp, pp ) );

}

public char 	InTri2D( tPointi Tp[], tPointi pp )
{
   int area0, area1, area2;

   /* compute three AreaSign() values for pp w.r.t. each edge of the face in 2D */
   System.out.println("In tri 2d pp, tp 0,1,2"+pp.p[0]+", "+pp.p[1]+" , "+pp.p[2]);
   for (int b=0;b<3;b++)
     for (int r=0;r<3;r++)
       System.out.println(Tp[b].p[r]);


   area0 = AreaSign( pp, Tp[0], Tp[1] );
   area1 = AreaSign( pp, Tp[1], Tp[2] );
   area2 = AreaSign( pp, Tp[2], Tp[0] );

   System.out.println("area0= "+area0+"  area1= "+area1+" area2= "+area2);

   if ( ( area0 == 0 ) && ( area1 > 0 ) && ( area2 > 0 ) ||
        ( area1 == 0 ) && ( area0 > 0 ) && ( area2 > 0 ) ||
        ( area2 == 0 ) && ( area0 > 0 ) && ( area1 > 0 ) ) 
     return 'E';

   if ( ( area0 == 0 ) && ( area1 < 0 ) && ( area2 < 0 ) ||
        ( area1 == 0 ) && ( area0 < 0 ) && ( area2 < 0 ) ||
        ( area2 == 0 ) && ( area0 < 0 ) && ( area1 < 0 ) )
     return 'E';                 
   
   if ( ( area0 >  0 ) && ( area1 > 0 ) && ( area2 > 0 ) ||
        ( area0 <  0 ) && ( area1 < 0 ) && ( area2 < 0 ) )
     return 'F';

   if ( ( area0 == 0 ) && ( area1 == 0 ) && ( area2 == 0 ) )
     {  
       System. out.println( "Error in InTriD" );
       System.exit(EXIT_FAILURE);
     }
   if ( ( area0 == 0 ) && ( area1 == 0 ) ||
        ( area0 == 0 ) && ( area2 == 0 ) ||
        ( area1 == 0 ) && ( area2 == 0 ) )
     return 'V';

   else  
     return '0';  
}

public int     AreaSign( tPointi a, tPointi b, tPointi c )  
{
    double area2;

    area2 = ( b.p[0] - a.p[0] ) * (double)( c.p[1] - a.p[1] ) -
            ( c.p[0] - a.p[0] ) * (double)( b.p[1] - a.p[1] );

    /* The area should be an integer. */
    if      ( area2 >  0.5 ) return  1;
    else if ( area2 < -0.5 ) return -1;
    else                     return  0;
}                            

public char    SegTriInt( tPointi T, tPointi q, tPointi r, tPointd p )
{
    char code = '?';
    m = -1;

    code = SegPlaneInt( T, q, r, p, m );
    System.out.println("****M is now after segplaneint: "+m);
    System.out.println("SegPlaneInt code= "+code+" , m= "+m+"; p=()"+p.p[X]+" , "+p.p[Y]+" , "+p.p[Z]);

    if      ( code == '0')
       return '0';
    else if ( code == 'q')
       return InTri3D( T, m, q );
    else if ( code == 'r')
       return InTri3D( T, m, r );
    else if ( code == 'p' )
       return InPlane( T, m, q, r, p );
    else if ( code == '1' )
       return SegTriCross( T, q, r );
    else /* Error */
       return code;
}

public char	InPlane( tPointi T, int m, tPointi q, tPointi r, tPointd p)
{
    /* NOT IMPLEMENTED */
    return 'p';
}

/*---------------------------------------------------------------------
The signed volumes of three tetrahedra are computed, determined
by the segment qr, and each edge of the triangle.  
Returns a char:
   'v': the open segment includes a vertex of T.
   'e': the open segment includes a point in the relative interior of an edge
   of T.
   'f': the open segment includes a point in the relative interior of a face
   of T.
   '0': the open segment does not intersect triangle T.
---------------------------------------------------------------------*/

public char SegTriCross( tPointi T, tPointi q, tPointi r )
{
   int vol0, vol1, vol2;
   
   vol0 = VolumeSign( q, Vertices[ T.p[0] ], Vertices[ T.p[1] ], r ); 
   vol1 = VolumeSign( q, Vertices[ T.p[1] ], Vertices[ T.p[2] ], r ); 
   vol2 = VolumeSign( q, Vertices[ T.p[2] ], Vertices[ T.p[0] ], r );
 
   System.out.println( "SegTriCross:  vol0 = "+vol0+" vol1 = "+vol1+" vol2 = "+ vol2 ); 
     
   /* Same sign: segment intersects interior of triangle. */
   if ( ( ( vol0 > 0 ) && ( vol1 > 0 ) && ( vol2 > 0 ) ) || 
        ( ( vol0 < 0 ) && ( vol1 < 0 ) && ( vol2 < 0 ) ))
      return 'f';
   
   /* Opposite sign: no intersection between segment and triangle */
   if ( ( ( vol0 > 0 ) || ( vol1 > 0 ) || ( vol2 > 0 ) ) &&
        ( ( vol0 < 0 ) || ( vol1 < 0 ) || ( vol2 < 0 ) ) )
      return '0';

   else if ( ( vol0 == 0 ) && ( vol1 == 0 ) && ( vol2 == 0 ) )
     {     
       System.out.println("Error 1 in SegTriCross" );
       System.exit(EXIT_FAILURE);
       return('b');
     }   

   /* Two zeros: segment intersects vertex. */
   else if ( ( ( vol0 == 0 ) && ( vol1 == 0 ) ) || 
             ( ( vol0 == 0 ) && ( vol2 == 0 ) ) || 
             ( ( vol1 == 0 ) && ( vol2 == 0 ) ) )
      return 'v';

   /* One zero: segment intersects edge. */
   else if ( ( vol0 == 0 ) || ( vol1 == 0 ) || ( vol2 == 0 ) )
      return 'e';
   
   else
     {
       System. out.println("Error 2 in SegTriCross " );
       System.exit(EXIT_FAILURE);
       return('b');
     }
}

public int 	VolumeSign( tPointi a, tPointi b, tPointi c, tPointi d )
{ 
   double vol;
   double ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz;
   double bxdx, bydy, bzdz, cxdx, cydy, czdz;

   ax = a.p[X];
   ay = a.p[Y];
   az = a.p[Z];
   bx = b.p[X];
   by = b.p[Y];
   bz = b.p[Z];
   cx = c.p[X]; 
   cy = c.p[Y];
   cz = c.p[Z];
   dx = d.p[X];
   dy = d.p[Y];
   dz = d.p[Z];

   bxdx=bx-dx;
   bydy=by-dy;
   bzdz=bz-dz;
   cxdx=cx-dx;
   cydy=cy-dy;
   czdz=cz-dz;
   vol =   (az-dz) * (bxdx*cydy - bydy*cxdx)
         + (ay-dy) * (bzdz*cxdx - bxdx*czdz)
         + (ax-dx) * (bydy*czdz - bzdz*cydy);


   /* The volume should be an integer. */
   if      ( vol > 0.5 )   return  1;
   else if ( vol < -0.5 )  return -1;
   else                    return  0;
}

/*
  This function returns a char:
    '0': the segment [ab] does not intersect (completely misses) the 
         bounding box surrounding the n-th triangle T.  It lies
         strictly to one side of one of the six supporting planes.
    '?': status unknown: the segment may or may not intersect T.
*/

public char BoxTest ( int n, tPointi a, tPointi b )
{
   int i; /* Coordinate index */
   int w;

   for ( i=0; i < DIM; i++ ) {
       w = Box[n][0].p[i]; /* min: lower left */
       if ( (a.p[i] < w) && (b.p[i] < w) ) return '0';
       w = Box[ n ][1].p[i]; /* max: upper right */
       if ( (a.p[i] > w) && (b.p[i] > w) ) return '0';
   }
   return '?';
}

//*********************************************
}//end class 

class tPointi 
{
  
  int p[];

  tPointi()
    {
      p = new int[3];
      p[0]=p[1]=p[2]=0;
    }
 
  tPointi(int x, int y, int z)
    {
      p = new int[3];
      p[0] = x;
      p[1] = y;
      p[2] = z;
    }

}

class tPointd 
{

  double p[];

  tPointd()
    {
      p = new double[3];
      p[0]=p[1]=p[2]=0.0;
    }
 
 tPointd(double x, double y, double z)
    {
      p = new double[3];
      p[0] = x;
      p[1] = y;
      p[2] = z;
    }

}

