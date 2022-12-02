import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface EDGraph<E,W> {

	//returns number of nodes of the graph
	int getSize();
	
	//true if the graph has no nodes; false in other case
	boolean isEmpty();
	
	//inserts a new node with the label item
	int insertNode(E item);
	
	//inserts a new edge
	boolean insertEdge(EDEdge<W> edge);
	
	//given the label of a node, returns the index of the position
	//in the array where is stored
	int getNodeIndex(E item);
	
	//given the position of a node, returns the node element
	//returns null if an empty or wrong position
	E getNodeValue(int index);
	
	//given the indices of two nodes, returns the Edge that joins the
	//two nodes, if any. If there is no edge, then returns null
	//The direction source --> dest is important if the graph is directed
	EDEdge<W> getEdge(int source, int dest);
	
	//removes a node
	E removeNode(int index);
	
	//removes an edge
	EDEdge<W> removeEdge(int source, int dest, W weight);

	boolean insertEdge (E fromNode, E toNode, W label);

	//returns a Set with the index of the adjacent nodes
	Set<Integer> getAdyacentNodes(int index);
	
	
	//prints the structure
	void printGraphStructure();


	Set<E> getNodes();

	public int[] distanceToAll (E item);


	Set<E> followers(E item);

	Set<E> commonFollowers (E item1, E item2);

	Set<E> commonFollowed (E item1, E item2);

	Set<E> suggest(E item);

	E mostInfluencer();

	void saveGraphStructure(RandomAccessFile f);
	
}
