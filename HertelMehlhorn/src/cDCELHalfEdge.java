
public class cDCELHalfEdge {
	cDCELVertex origin;
	cDCELHalfEdge twin;
	cDCELHalfEdge next,prev;
	boolean isDiagonal;
	boolean isEssential=true;
	public boolean isEssential() {
		return isEssential;
	}
	public void setEssential(boolean isEssential) {
		this.isEssential = isEssential;
	}
	public cDCELVertex getOrigin() {
		return origin;
	}
	public void setOrigin(cDCELVertex origin) {
		this.origin = origin;
	}
	public cDCELHalfEdge getTwin() {
		return twin;
	}
	public void setTwin(cDCELHalfEdge twin) {
		this.twin = twin;
	}
	public cDCELHalfEdge getNext() {
		return next;
	}
	public void setNext(cDCELHalfEdge next) {
		this.next = next;
	}
	public cDCELHalfEdge getPrev() {
		return prev;
	}
	public void setPrev(cDCELHalfEdge prev) {
		this.prev = prev;
	}
	public boolean isDiagonal() {
		return isDiagonal;
	}
	public void setDiagonal(boolean isDiagonal) {
		this.isDiagonal = isDiagonal;
	}

	@Override
	public String toString(){
		return origin.toString() +" -> " + twin.origin.toString();
	}
	
}
