public class EDEdge<T> {
	private int target;  //destination vertex of the edge
	private int source;  //source vertex of the edge
	private T weight;    //weigth of the edge, by default 1
	
	//constructors
	public EDEdge(int source, int target) {
		this.source = source;
		this.target = target;
		this.weight = null;
	}
	
	public EDEdge(int source, int target, T weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	//methods
	//two edges are equal if source and target are equal ***and they have the same label or weight***
	public boolean equals (Object other) {
		if (this == other) return true;
		if (!(other instanceof EDEdge)) return false;
		//System.out.println("equals de edge");
		EDEdge<T> localEdge = (EDEdge<T>) other;
		if (this.source == localEdge.source && this.target == localEdge.target) {
			//System.out.println("mismos nodos, etiquetas: "+this.weight+", "+localEdge.weight);
			if (this.weight!=null)
				return this.weight.equals(localEdge.weight);
			else
				return true;
		}
		else return false;
	}
	
	//returns destination vertex
	public int getTarget() {
		return this.target;
	}
	
	//returns source vertex
	public int getSource() {
		return this.source;
	}
	
	//returns weight
	public T getWeight() {
		return this.weight;
	}
	
	public String toString() {
		String s = "["+this.source+"->"+this.target+":"+this.weight+"]";
		return s;		
	}
}
