//package edu.iastate.cs228.hw5;

/**
 * @author xuanlu
 * 
 * The class is to implement the Dijkstra algorithm to solve the maze shortest path
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Dijkstra {

	/**
	 * First, computes a shortest path from a source vertex to a destination
	 * vertex in a graph by using Dijkstra's algorithm. Second, visits and saves
	 * (in a stack) each vertex in the path, in reverse order starting from the
	 * destination vertex, by using the map object pred. Third, uses a
	 * StringBuilder object to generate the return String object by poping up
	 * the vertices from the stack; the vertices in the String object are in the
	 * right order. Note that the get_index() method is called from a
	 * Graph.Vertex object to get its oringinal integer name.
	 *
	 * @param G
	 *            - The graph in which a shortest path is to be computed
	 * @param source
	 *            - The first vertex of the shortest path
	 * @param dest
	 *            - The last vertex of the shortest path
	 * @return a String object with three lines (separated by a newline
	 *         character) such that line 1 shows the length of the shortest
	 *         path, line 2 shows the cost of the path, and line 3 gives a list
	 *         of the vertices (in the path) with a space between adjacent
	 *         vertices.
	 *
	 *         The contents of an example String object: Path Length: 5 Path
	 *         Cost: 4 Path: 0 4 2 5 7 9
	 *
	 * @throws NullPointerException
	 *             - If any arugment is null
	 *
	 * @throws RuntimeException
	 *             - If the given source or dest vertex is not in the graph
	 *
	 */
	public static String Dijkstra(Graph G, Graph.Vertex source, Graph.Vertex dest) {

		// situation of throwing exceptions
		if (G == null || source == null || dest == null)
			throw new NullPointerException();
		if (!G.check_vertex(source) || !G.check_vertex(dest))
			throw new RuntimeException();

		// check the distance of vertex
		HashMap<Graph.Vertex, Integer> dist = new HashMap<Graph.Vertex, Integer>();

		// heap set to get miniHeap
		Heap<Vpair<Graph.Vertex, Integer>> vHeap = new Heap<Vpair<Graph.Vertex, Integer>>();
		// visited set
		HashSet<Graph.Vertex> vSet = new HashSet<Graph.Vertex>();
		// the predecessor of the vertex
		HashMap<Graph.Vertex, Graph.Vertex> pred = new HashMap<Graph.Vertex, Graph.Vertex>();

		// initialization the source vertex
		Graph.Vertex vet = source;
		Vpair<Graph.Vertex, Integer> startVP = new Vpair<Graph.Vertex, Integer>(vet, 0);

		dist.put(source, 0);
		vHeap.add(startVP);
		while (!vHeap.isEmpty()) {
			Vpair<Graph.Vertex, Integer> rev = vHeap.removeMin();
			Graph.Vertex u = rev.getVertex();
			// if there are vertex has not visited
			if (!vSet.contains(u)) {
				vSet.add(u);
				Iterator<Graph.Edge> iter = u.get_edges().iterator();
				// look for the close to vertex
				while (iter.hasNext()) {
					Graph.Edge curEdge = iter.next();
					Graph.Vertex curVex = curEdge.to;
					if (curVex != null) {
						// compare the distance from the previous vertex and
						// current index
						Integer altdist = dist.get(u) + curEdge.get_weight();
						Integer vdist = dist.get(curVex);
						if (vdist == null || vdist > altdist) {
							dist.put(curVex, altdist);
							pred.put(curVex, u);
							vHeap.add(new Vpair<Graph.Vertex, Integer>(curVex, altdist));
						}
					}
				}

			}
		}
		// if there is no path from source to destination
		if (pred.get(dest) == null)
			return null;
		// visits and saves (in a stack) each vertex in the path, in reverse
		// order starting from the destination vertex, by using the map object
		// pred.
		LinkedStack<Graph.Vertex> stack = new LinkedStack<Graph.Vertex>();
		stack.push(dest);

		for (Graph.Vertex temp = pred.get(dest); temp != source; temp = pred.get(temp)) {
			stack.push(temp);
		}
		stack.push(source);

		// StringBuilder object to generate the return String object by poping
		// up the vertices from the stack
		StringBuilder sb = new StringBuilder();
		int pathLen = stack.size() - 1;
		sb.append("Path Length: " + pathLen + " Path" + "\n");
		// cost of last step
		Integer cost = dist.get(dest);
		sb.append("Cost: " + cost + "\n");
		sb.append("Path:");
		while (!stack.isEmpty()) {
			Graph.Vertex vetString = stack.pop();
			String result = " " + vetString.get_index();
			sb.append(result);
		}
		return sb.toString();
	}

	/**
	 * A pair class with two components of types V and C, where V is a vertex
	 * type and C is a cost type.
	 */

	private static class Vpair<V, C extends Comparable<? super C>> implements Comparable<Vpair<V, C>> {
		private V node;
		private C cost;

		Vpair(V n, C c) {
			node = n;
			cost = c;
		}

		public V getVertex() {
			return node;
		}

		public C getCost() {
			return cost;
		}

		public int compareTo(Vpair<V, C> other) {
			return cost.compareTo(other.getCost());
		}

		public String toString() {
			return "<" + node.toString() + ", " + cost.toString() + ">";
		}

		public int hashCode() {
			return node.hashCode();
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if ((obj == null) || (obj.getClass() != this.getClass()))
				return false;
			// object must be Vpair at this point
			Vpair<?, ?> test = (Vpair<?, ?>) obj;
			return (node == test.node || (node != null && node.equals(test.node)));
		}
	}

}
