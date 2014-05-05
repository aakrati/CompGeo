import java.util.ArrayList;
import java.util.List;


public class cDCELVertex {
	cPointi coord;
	List<cDCELHalfEdge> incidentEdges = new ArrayList<>();
	public cPointi getCoord() {
		return coord;
	}
	public void setCoord(cPointi coord) {
		this.coord = coord;
	}
	
	public void addIncidentEdge(cDCELHalfEdge edge){
		incidentEdges.add(edge);
	}
	
	public cDCELHalfEdge findIncidentHalfEdge(cDCELVertex vertex2){
		for(cDCELHalfEdge head: incidentEdges){
			cDCELHalfEdge temp = head;
			do{
				if(temp.getOrigin()==vertex2){
					return head;
				}
				temp = temp.getNext();
			}while(temp.getOrigin()!=this);
		}
		
		return null;
	}
	@Override 
	public String toString(){
		return coord.x + "," + coord.y; 
	}
}
