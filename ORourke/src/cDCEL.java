/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Highly specialized DCEL class used to store vertices of a simple polygon and then be used
 * to create and store triangulations and convex decompositions.
 * <p>
 * Upon creation, the {@link #vertices}, {@link #edges}, and {@link #faces} lists are
 * populated.  The {@link #vertices} list will have the same indexing as the source {@link cPointi}[].
 * The {@link #edges} list will have edges with the same indexing as the source {@link cPointi}[]
 * with the exception that twin vertices are stored in odd indices.
 * <p>
 * Since this implementation only handles simple polygons, only one {@link Face} will be created
 * when the DCEL is created.  As more {@link HalfEdge}s are added the number of faces will
 * increase.
 * <p>
 * {@link HalfEdge}s are added to the DCEL via the {@link #addHalfEdges(Vertex, Vertex)} method.
 * It's the responsibility of the calling class(es) to store references to the DCEL vertices.  This
 * can be achieved since the indexing of the {@link #vertices} list is the same as the source {@link cPointi}[].
 * No check is performed to ensure that a pair of {@link HalfEdge}s are added that already exist.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class cDCEL {
  /**
   * Represents a node in the Doubly-Connected Edge List.
   * @author William Bittle
   * @version 2.2.0
   * @since 2.2.0
   */
  public class Vertex {
    /** The comparable data for this node */
    protected cPointi point;
    
    /** The the leaving edge */
    protected HalfEdge leaving;
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("VERTEX[")
      .append(this.point.x +", " + this.point.y)
      .append("]");
      return sb.toString();
    }
    
    /**
     * Returns the comparable data for this node.
     * @return T
     */
    public cPointi getPoint() {
      return this.point;
    }
    
    /**
     * Returns the leaving edge for this node.
     * @return {@link DoublyConnectedEdgeList.HalfEdge}
     */
    public HalfEdge getLeaving() {
      return this.leaving;
    }
    
    /**
     * Returns the edge from this node to the given node.
     * @param node the node to find an edge to
     * @return {@link DoublyConnectedEdgeList.HalfEdge}
     */
    public HalfEdge getEdgeTo(Vertex node) {
      if (leaving != null) {
        if (leaving.twin.origin == node) {
          return leaving;
        } else {
          HalfEdge edge = leaving.twin.next;
          while (edge != leaving) {
            if (edge.twin.origin == node) {
              return edge;
            } else {
              edge = edge.twin.next;
            }
          }
        }
      }
      return null;
    }
  }
  
  /**
   * Represents a half edge of the Doubly-Connected Edge List.
   * @author William Bittle
   * @version 2.2.0
   * @since 2.2.0
   */
  public class HalfEdge {
    /** The half edge origin */
    protected Vertex origin;
    
    /** The adjacent twin of this half edge */
    protected HalfEdge twin;
    
    /** The adjacent edge next in the list having the same face */
    protected HalfEdge next;
    
    /** The adjacent face of this half edge */
    protected Face face;
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("HALF_EDGE[")
      .append(this.origin).append("|")
      .append(this.next.origin)
      .append("]");
      return sb.toString();
    }
    
    /**
     * Returns this half edge's origin.
     * @return {@link DoublyConnectedEdgeList.Vertex}
     */
    public Vertex getOrigin() {
      return this.origin;
    }
    
    /**
     * Returns this half edge's destination.
     * @return {@link DoublyConnectedEdgeList.Vertex}
     */
    public Vertex getDestination() {
      return this.next.origin;
    }
    
    /**
     * Returns this half edge's twin half edge.
     * @return {@link DoublyConnectedEdgeList.HalfEdge}
     */
    public HalfEdge getTwin() {
      return twin;
    }
    
    /**
     * Returns this half edge's next half edge.
     * @return {@link DoublyConnectedEdgeList.HalfEdge}
     */
    public HalfEdge getNext() {
      return next;
    }
    
    /**
     * Returns the previous half edge having the same
     * face as this half edge.
     * @return {@link DoublyConnectedEdgeList.HalfEdge}
     */
    public HalfEdge getPrevious() {
      HalfEdge edge = twin.next.twin;
      // walk around the face
      while (edge.next != this) {
        edge = edge.next.twin;
      }
      return edge;
    }
    
    /**
     * Returns this half edge's face.
     * @return {@link DoublyConnectedEdgeList.Face}
     */
    public Face getFace() {
      return face;
    }
  }
  
  /**
   * Represents a face in the Doubly-Connected Edge List.
   * @author William Bittle
   * @version 2.2.0
   * @since 2.2.0
   */
  public class Face {
    /** An edge of the edge list enclosing this face */
    protected HalfEdge edge;
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("FACE["	)
      .append(this.edge)
      .append("]");
      return sb.toString();
    }
    
    /**
     * Returns the number of edges on this face.
     * @return int
     */
    public int getEdgeCount() {
      HalfEdge edge = this.edge;
      int count = 0;
      if (edge != null) {
        count++;
        while (edge.next != this.edge) {
          count++;
          edge = edge.next;
        }
      }
      return count;
    }
  }
  
  /** The list of nodes */
  protected List<Vertex> vertices = new ArrayList<Vertex>();
  
  /** The list of half edges */
  protected List<HalfEdge> edges = new ArrayList<HalfEdge>();
  
  /** The list of faces */
  protected List<Face> faces = new ArrayList<Face>();
  
  /**
   * Full constructor.
   * @param points the points of the simple polygon
   */
  public cDCEL(cPointi[] points) {
    this.initialize(points);
  }
  
  /**
   * Initializes the DCEL class given the points of the polygon.
   * @param points the points of the polygon
   */
  protected void initialize(cPointi[] points) {
    // get the number of points
	if(points==null)
		return;
    int size = points.length;
    
    // we will always have exactly one face at the beginning
    Face face = new Face();
    faces.add(face);
    
    HalfEdge prevLeftEdge = null;
    HalfEdge prevRightEdge = null;
    
    // loop over the points creating the vertices and
    // half edges for the data structure
    for (int i = 0; i < size; i++) {
      cPointi point = points[i];
      
      Vertex vertex = new Vertex();
      HalfEdge left = new HalfEdge();
      HalfEdge right = new HalfEdge();
      
      // create and populate the left
      // and right half edges
      left.face = face;
      left.next = null;
      left.origin = vertex;
      left.twin = right;
      
      right.face = null;
      right.next = prevRightEdge;
      right.origin = null;
      right.twin = left;
      
      // add the edges the edge list
      edges.add(left);
      edges.add(right);
      
      // populate the vertex
      vertex.leaving = left;
      vertex.point = point;
      
      // add the vertex to the vertices list
      vertices.add(vertex);
      
      // set the previous next edge to this left edge
      if (prevLeftEdge != null) {
        prevLeftEdge.next = left;
      }
      
      // set the previous right edge origin to this vertex
      if (prevRightEdge != null) {
        prevRightEdge.origin = vertex;
      }
      
      // set the new previous edges
      prevLeftEdge = left;
      prevRightEdge = right;
    }
    
    // set the last left edge's next pointer to the
    // first left edge we created
    HalfEdge firstLeftEdge = edges.get(0);
    prevLeftEdge.next = firstLeftEdge;
    
    // set the first right edge's next pointer
    // to the last right edge we created
    // (note that right edges are at odd indices)
    HalfEdge firstRightEdge = edges.get(1);
    firstRightEdge.next = prevRightEdge;
    // set the last right edge's origin to the first
    // vertex in the list
    prevRightEdge.origin = vertices.get(0);
    
    // set the edge of the only face to the first
    // left edge
    // (note that the interior of each face has CCW winding)
    face.edge = firstLeftEdge;
  }
  
  /**
   * Adds two half edges to this DCEL object given the vertices to connect.
   * <p>
   * This method assumes that no crossing edges will be added.
   * @param v1 the first vertex
   * @param v2 the second vertex
   */
  public void addHalfEdges(Vertex v1, Vertex v2) {
    // adding an edge splits the current face into two faces
    // so we need to create a new face
    Face face = new Face();
    
    // create the new half edges for the new edge
    HalfEdge left = new HalfEdge();
    HalfEdge right = new HalfEdge();
    
    // find the reference face for these two vertices
    // the reference face is the face on which both the given
    // vertices are on
    Face referenceFace = this.getReferenceFace(v1, v2);
    
    // get the previous edges for these vertices that are on
    // the reference face
    HalfEdge prev1 = this.getPreviousEdge(v1, referenceFace);
    HalfEdge prev2 = this.getPreviousEdge(v2, referenceFace);
    
    face.edge = left;
    referenceFace.edge = right;
    
    // setup both half edges
    left.face = face;
    left.next = prev2.next;
    left.origin = v1;
    left.twin = right;
    
    right.face = referenceFace;
    right.next = prev1.next;
    right.origin = v2;
    right.twin = left;
    
    // set the previous edge's next pointers to the new half edges
    prev1.next = left;
    prev2.next = right;
    
    // set the new face for all the edges in the left list
    HalfEdge curr = left.next;
    while (curr != left) {
      curr.face = face;
      curr = curr.next;
    }
    
    // add the new edges to the list
    this.edges.add(left);
    this.edges.add(right);
    
    // add the new face to the list
    this.faces.add(face);
  }
  
  /**
   * Walks around the given face and finds the previous edge
   * for the given vertex.
   * <p>
   * This method assumes that the given vertex will be on the given face.
   * @param vertex the vertex to find the previous edge for
   * @param face the face the edge should lie on
   * @return {@link HalfEdge} the previous edge
   */
  protected HalfEdge getPreviousEdge(Vertex vertex, Face face) {
    // find the vertex on the given face and return the
    // edge that points to it
    HalfEdge twin = vertex.leaving.twin;
    HalfEdge edge = vertex.leaving.twin.next.twin;
    // look at all the edges that have their
    // destination as this vertex
    while (edge != twin) {
      // we can't use the getPrevious method on the leaving
      // edge since this doesn't give us the right previous edge
      // in all cases.  The real criteria is to find the edge that
      // has this vertex as the destination and has the same face
      // as the given face
      if (edge.face == face) {
        return edge;
      }
      edge = edge.next.twin;
    }
    // if we get here then its the last edge
    return edge;
  }
  
  /**
   * Finds the face that both vertices are on.  
   * <p>
   * If the given vertices are connected then the first common face is returned.
   * <p>
   * If the given vertices do not have a common face the first vertex's leaving
   * edge's face is returned.
   * @param v1 the first vertex
   * @param v2 the second vertex
   * @return {@link Face} the face on which both vertices lie
   */
  protected Face getReferenceFace(Vertex v1, Vertex v2) {
    // find the face that both vertices are on
    
    // if the leaving edge faces are already the same then just return
    if (v1.leaving.face == v2.leaving.face) return v1.leaving.face;
    
    // loop over all the edges whose destination is the first vertex (constant time)
    HalfEdge e1 = v1.leaving.twin.next.twin;
    while (e1 != v1.leaving.twin) {
      // loop over all the edges whose destination is the second vertex (constant time)
      HalfEdge e2 = v2.leaving.twin.next.twin;
      while (e2 != v2.leaving.twin) {
        // if we find a common face, that must be the reference face
        if (e1.face == e2.face) return e1.face;
        e2 = e2.next.twin;
      }
      e1 = e1.next.twin;
    }
    
    // if we don't find a common face then return v1.leaving.face
    return v1.leaving.face;
  }
  
  /**
   * Removes the half edges specified by the given interior edge index.
   * <p>
   * This method removes both halves of the edge.
   * @param index the index of the interior half edge to remove
   */
  public void removeHalfEdges(int index) {
    HalfEdge e = this.edges.get(index);
    this.removeHalfEdges(index, e);
  }
  
  /**
   * Removes the given half edge and its twin.
   * @param edge the half edge to remove
   */
  public void removeHalfEdges(HalfEdge edge) {
    int index = this.edges.indexOf(edge);
    this.removeHalfEdges(index, edge);
  }
  
  /**
   * Removes the given half edge and its twin.
   * @param index the index of the given edge
   * @param edge the half edge to remove
   */
  protected void removeHalfEdges(int index, HalfEdge edge) {
    // wire up the two end points to remove the edge
    Face face = edge.twin.face;
    
    // we only need to re-wire the internal edges
    HalfEdge ePrev = edge.getPrevious();
    HalfEdge tPrev = edge.twin.getPrevious();
    HalfEdge eNext = edge.next;
    HalfEdge tNext = edge.twin.next;
    
    ePrev.next = tNext;
    tPrev.next = eNext;
    
    face.edge = eNext;
    
    // set the face
    HalfEdge te = eNext;
    while (te != tNext) {
      te.face = face;
      te = te.next;
    }
    
    // remove the unneeded face
    this.faces.remove(edge.face);
    
    // remove the edges
    this.edges.remove(index); // the edge
    this.edges.remove(index); // the edge's twin
  }
  
  /**
   * Returns the comparable data for this node.
   * @return T
   */
  public Vertex getVertex(cPointi point) {
	  for(Vertex v:vertices){
		  if(v.point==point){
			  return v;
		  }
	  }
    return null;
  }
  /**
   * Performs the Hertel-Mehlhorn algorithm on the given DCEL assuming that
   * it is a valid triangulation.
   * <p>
   * This method will remove unnecessary diagonals and remove faces that get merged
   * leaving a convex decomposition.
   * <p>
   * This method is guaranteed to produce a convex decomposition with no more than
   * 4 times the minimum number of convex pieces.
   */
  public void hertelMehlhorn() {
    // loop over all the edges and see which we can remove
    int vSize = this.vertices.size();
    
    // This method will remove any unnecessary diagonals (those that do not
    // form reflex vertices when removed).  This method is O(n) where n is the
    // number of diagonals added to the original DCEL.  We can start processing
    // diagonals after all the initial diagonals (the initial diagonals are the
    // edges of the original polygon).  We can also skip every other half edge
    // since each edge is stored with its twin in the next index.
    System.out.println("inside hertel mehlhorn");
    int i = vSize * 2;
    while (i < this.edges.size()) {
      
      // see if removing this edge creates a reflex vertex at the end points
      HalfEdge e = this.edges.get(i);
      // test the first end point
      Vertex v1 = e.origin;
      Vertex v0 = e.getPrevious().origin;
      Vertex v2 = e.twin.next.next.origin;
      
      System.out.println("Working on v0, v1, v2"+v0.toString()+", "+v1.toString()+", "+v2.toString());
      
      // check if removing this half edge creates a reflex vertex at the
      // origin vertex of this half edge
      if (isReflex(v0, v1, v2)) {
        // if it did, then we cannot remove this edge
        // so skip the next one and continue
        i+=2;
        continue;
      }
      
      // test the other end point
      v1 = e.twin.origin;
      v0 = e.twin.getPrevious().origin;
      v2 = e.next.next.origin;
      
      // check if removing this half edge creates a reflex vertex at the
      // origin of this half edge's twin
      if (isReflex(v0, v1, v2)) {
        // if it did, then we cannot remove this edge
        // so skip the next one and continue
        i+=2;
        continue;
      }
      
      // otherwise we can remove this edge
      System.out.println("Removed "+v1+", "+e.next.origin);
      
      this.removeHalfEdges(i, e);
    }
  }
  
  /**
   * Returns true if the given vertices create a reflex vertex.
   * @param v0 the previous vertex
   * @param v1 the vertex
   * @param v2 the next vertex
   * @return boolean
   */
  protected boolean isReflex(Vertex v0, Vertex v1, Vertex v2) {
    cPointi p0 = v0.point;
    cPointi p1 = v1.point;
    cPointi p2 = v2.point;
    
    if(p1.LeftOn( p1, p2, p0))
    	return false;
    
    return true;
  }
  public void DrawDiagonals(Graphics g, Color inColor)
  {
    System.out.println("Drawing diagonals");
    g.setColor(inColor);
    if(vertices==null)
    	return;
    
    int i = this.vertices.size() * 2;
    while (i < this.edges.size()) {
      
      // see if removing this edge creates a reflex vertex at the end points
      HalfEdge e = this.edges.get(i);
   // test the first end point
      Vertex vert1 = e.origin;
      Vertex vert2 = e.getNext().origin;
      g.drawLine(vert1.point.x, vert1.point.y, vert2.point.x, vert2.point.y);
      
      i+=2;
      
    }
  } 
}