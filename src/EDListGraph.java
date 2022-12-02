import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;


/** Implementation of interface Graph using adjacency lists
 * @param <T> The base type of the nodes
 * @param <W> The base type of the weights of the edges
 */
public class EDListGraph<T,W> implements EDGraph<T,W> {
	@SuppressWarnings("hiding")
	private class Node<U> {
		U data;
		List< EDEdge<W> > lEdges;
		
		Node (U data) {
			this.data = data;
			this.lEdges = new LinkedList<>();
		}
		public boolean equals (Object other) {
			if (this == other) return true;
			if (!(other instanceof Node)) return false;
			Node<U> anotherNode = (Node<U>) other;
			return data.equals(anotherNode.data);
		}
	}
	
	// Private data
	private ArrayList<Node<T>> nodes;
	private int size; //real number of nodes
	private boolean directed;
	


	public EDListGraph() {
		directed = true; //directed
		nodes =  new ArrayList<>();
		size =0;
	}

	/** Constructor
	 * @param direct <code>true</code> for directed edges;
	 * <code>false</code> for non directed edges.
	 */
	public EDListGraph (boolean direct) {
		directed = direct;
		nodes =  new ArrayList<>();
		size =0;
	}
	
	public int getSize() {
		return size;
	}

	public int nodesSize() {
		return nodes.size();
	}
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int insertNode(T item) {
			
	    int i = 0;
	    while (i<nodes.size() && nodes.get(i).data != null) i++;
				
	    Node<T> newNode = new Node<>(item);
	    if (i<nodes.size()) nodes.set(i,newNode);
	    else nodes.add(newNode);
	    size++;

	    return i;
	}
	@Override
	public int getNodeIndex(T item) {
		Node<T> aux = new Node<>(item);
		return nodes.indexOf(aux);
	}

	@Override
	public T getNodeValue(int index) throws IndexOutOfBoundsException{
		
		return nodes.get(index).data;
		
	}
	
	@Override
	public boolean insertEdge(EDEdge<W> edge) {
		int sourceIndex = edge.getSource();
		int targetIndex = edge.getTarget();
		if (sourceIndex >=0 && sourceIndex<nodes.size() && targetIndex >=0 && targetIndex<nodes.size()) {
			Node<T> nodeSr = nodes.get(sourceIndex);
			Node<T> nodeTa = nodes.get(targetIndex);
			if (nodeSr.data!=null && nodeTa.data != null) {
			   if (!nodeSr.lEdges.contains(edge)) {
				   nodeSr.lEdges.add(edge);
				   nodes.set(sourceIndex,nodeSr); 
				   if (!directed) {//no dirigido
					  EDEdge<W> reverse = new EDEdge<>(targetIndex,sourceIndex,edge.getWeight());
					  nodeTa.lEdges.add(reverse);
					  nodes.set(targetIndex, nodeTa);
				   }
				   return true;
			    }
			   else System.out.println("The graph has already this edge: "+edge.toString());
			}
		}
		return false;
	}


	public EDEdge<W> getEdge (int source, int dest) {
		if (source <0 || source >= nodes.size()) return null;

		Node<T> node = nodes.get(source);
		if (node.data == null ) return null;
		for (EDEdge<W> edge: node.lEdges)
			if (edge.getTarget() == dest) return edge;

		return null;
	}
	
	
	
	@Override
	public EDEdge<W> removeEdge(int source, int target, W weight) {
		if (source <0 || source >= nodes.size() || target<0 || target >= nodes.size()) return null;
		if (nodes.get(source).data!=null && nodes.get(target).data!=null) {
			EDEdge<W> edge = new EDEdge<>(source, target, weight);
			Node<T> node = nodes.get(source);
			int i = node.lEdges.indexOf(edge);
			if (i != -1) {
				edge = node.lEdges.remove(i);
				if (!directed) {
					EDEdge<W> reverse = new EDEdge<>(target,source,weight);
					nodes.get(target).lEdges.remove(reverse);
				}
				return edge;
			}	
		}
		return null;	
	}

	@Override
	public T removeNode(int index) {
		if (index >=0 && index < nodes.size()){
			if (!directed) {
				Node<T> node = nodes.get(index);
				for (EDEdge<W> edge: node.lEdges ) {
					int target = edge.getTarget();
					W label = edge.getWeight();
					EDEdge<W> other = new EDEdge<>(target,index,label);
					nodes.get(target).lEdges.remove(other);
				}
			}
			else { //directed
				for (int i=0; i<nodes.size(); i++) {
					if (i!=index && nodes.get(i).data !=null) {
						Node<T> node = nodes.get(i);
						for (EDEdge<W> edge: node.lEdges) {
							if (index == edge.getTarget()) //any weight/label
								node.lEdges.remove(edge);
						}
					}
				}
			}
			
			Node<T> node = nodes.get(index);
			node.lEdges.clear();
			T ret = node.data;
			node.data = null; //It is not remove, data is set to null
			nodes.set(index, node);
			size--;
			return ret;
		}
		return null;
	}

	@Override
	public Set<Integer> getAdyacentNodes(int index) {
		if (index < 0 || index >= nodes.size()) return new HashSet<>();
		
		Set<Integer> ret = new HashSet<>();
		for (EDEdge<W> ed: nodes.get(index).lEdges) {
			ret.add(ed.getTarget());
		}
		
		return ret;
	}

	//Ejercicio 1
	public boolean insertEdge (T fromNode, T toNode, W label) {
		Node<T> aux_source = new Node<>(fromNode);
		int source =nodes.indexOf(aux_source);
		Node<T> aux_target = new Node<>(toNode);
		int target =nodes.indexOf(aux_target);
		if(target<0)
			target=insertNode(toNode);
		if (source<0){
			source=insertNode(fromNode);

		}
		EDEdge<W> edge= new EDEdge(source, target, label);
		nodes.get(source).lEdges.add(target,edge);
		return insertEdge(edge);
	}

	//Ejercicio 8
	public int[] distanceToAll (T item) {

	}
	
	

	//Ejercicio 3: Dado un item, etiqueta de un nodo, devuelve su conjunto de seguidores. Devuelve null si item no
	//está en el grafo
	public Set<T> followers(T item) {

	}
	//Ejercicio 4: conjunto común de seguidores de item1 e item2
	public Set<T> commonFollowers(T item1, T item2) {

	}

	//Ejercicio 5: conjunto común de seguidos por item1 e item2
	public Set<T> commonFollowed (T item1, T item2) {

	}
	

	/**Ejercicio 6
	 * Sugiere a item personas a las que no sigue pero son seguidas por las personas a las que item sí sigue*/

	public Set<T> suggest(T item) {


	}

	/**Ejercicio 7
	 * Devuelve la persona con más seguidores de la red
	 * @return etiqueta con mayor número de arcos de entrada
	 */
	public T mostInfluencer() {


	}

	
	
	public void printGraphStructure() {
		//System.out.println("Vector size= " + nodes.length);
		System.out.println("Vector size " + nodes.size());
		System.out.println("Nodes: "+ this.getSize());
		for (int i=0; i<nodes.size(); i++) {
			System.out.print("pos "+i+": ");
	        Node<T> node = nodes.get(i);
			System.out.print(node.data+" -- ");
			Iterator<EDEdge<W>> it = node.lEdges.listIterator();
			while (it.hasNext()) {
					EDEdge<W> e = it.next();
					System.out.print("("+e.getSource()+","+e.getTarget()+", "+e.getWeight()+")->" );
			}
			System.out.println();
		}
	}
	
	
	@Override
	public void saveGraphStructure(RandomAccessFile f) {
		
		
			try {
				f.writeInt(this.size);
			 //numero de nodos
				//System.out.println("tama�o grafo "+this.size);
				//f.seek(0);
				//System.out.println("leido: "+f.readInt());
				for (int i=0; i<nodes.size();i++) {
				if (nodes.get(i)!=null) {
					f.writeUTF((String) nodes.get(i).data);
					f.writeInt(nodes.get(i).lEdges.size());
					for (EDEdge<W> edge: nodes.get(i).lEdges)
						f.writeInt(edge.getTarget());
				}
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public Set<T> getNodes() {
		Set<T> s = new HashSet<>();
		for (int i=0; i<nodes.size(); i++) {
			if (nodes.get(i).data!=null) 
				s.add(nodes.get(i).data);
		}
		return s;
	}


}
