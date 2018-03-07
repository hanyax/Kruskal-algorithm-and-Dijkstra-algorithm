package misc.graphs;

import java.util.Iterator;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NotYetImplementedException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.
    
    IDictionary<V, ChainedHashSet<E>> graph;
    IList<V> vertices;
    IList<E> edges;
    
    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        // TODO: Your code here
        this.graph = new ChainedHashDictionary<V, ChainedHashSet<E>>();
        for (V vertice : vertices) {
            graph.put(vertice, new ChainedHashSet<E>());
        }
        for (E edge :edges ) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException("The vertice weight is less than 0");
            }
            if (!graph.containsKey(edge.getVertex1()) || !graph.containsKey(edge.getVertex2())) {
                throw new IllegalArgumentException("Vertice is not in the graph");
            }
            graph.get(edge.getVertex1()).add(edge);
            graph.get(edge.getVertex2()).add(edge);
        }
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mst = new ChainedHashSet<E>();
        
        ArrayDisjointSet<V> mstFinder = new ArrayDisjointSet<V>();
        for (V v: this.vertices) {
            mstFinder.makeSet(v);
        }
        
        IList<E> sortedEdges = topKSort(this.numEdges(), this.edges);
        
        for (E edge : sortedEdges) {
            if (mstFinder.findSet(edge.getVertex1()) != mstFinder.findSet(edge.getVertex2())) {
                mstFinder.union(edge.getVertex1(), edge.getVertex2());
                mst.add(edge);
            }
        }
        return mst;
    }
    
    private static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        // Implementation notes:
        //
        // - This static method is a _generic method_. A generic method is similar to
        //   the generic methods we covered in class, except that the generic parameter
        //   is used only within this method.
        //
        //   You can implement a generic method in basically the same way you implement
        //   generic classes: just use the 'T' generic type as if it were a regular type.
        //
        // - You should implement this method by using your ArrayHeap for the sake of
        //   efficiency.
        if (k < 0) {
                throw new IllegalArgumentException("K can not be negeative number");
            } else if (k == 0) {
                return new DoubleLinkedList<T>();
            } else {
                IPriorityQueue<T> sortHeap = new ArrayHeap<T>(); 
                if (input.size() < k) {
                    k = input.size();
                }
                Iterator<T> iter = input.iterator();
                for (int i = 0; i < k; i++) {
                    sortHeap.insert(iter.next());
                }
                for (int i = k; i < input.size(); i++) {
                    T element = iter.next();
                    if (element.compareTo(sortHeap.peekMin()) > 0) {
                        sortHeap.insert(element);
                        sortHeap.removeMin();
                    }
                }
                IList<T> result = new DoubleLinkedList<T>();
                for (int i = 0; i < k; i++) {
                    result.add(sortHeap.removeMin());
                }
                return result;
            }
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        throw new NotYetImplementedException();
    }
}
