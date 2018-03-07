package datastructures.concrete;

import java.util.Iterator;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import misc.exceptions.NotYetImplementedException;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> repItemLookup;
    int repCount;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    public ArrayDisjointSet() {
        pointers = this.makeArrayOfT(20);
        repItemLookup = new ChainedHashDictionary<T, Integer>();
        repCount = 0;
    }
    
    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private int[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return new int[arraySize];
    }
    
    @SuppressWarnings("unchecked")
    private void ensureCapacity(int newRep) {
            if ( newRep >= this.pointers.length) {
            int[] newSet = new int[this.pointers.length * 2];
                for (int i = 0; i < this.pointers.length; i++) {
                    newSet[i] = pointers[i];
                }
                this.pointers = newSet;
            }
    }

    @Override
    public void makeSet(T item) {
        if (!repItemLookup.containsKey(item)) {
            ensureCapacity(repCount);
            pointers[repCount] = -1;
            repItemLookup.put(item, repCount);
            repCount++;
        } else {
            throw new IllegalArgumentException("Item already in the set");
        }
    }

    @Override
    public int findSet(T item) {
        if (repItemLookup.containsKey(item)) {
            DoubleLinkedList<Integer> links = new DoubleLinkedList<Integer>();
            findSetHelper(repItemLookup.get(item), links);
            int root = links.get(links.size() - 1);
            int i = 0;
            Iterator<Integer> iter = links.iterator();
            while (i < links.size() - 1) {
                pointers[iter.next()] = root;
                i++;
            }
            return root;
        } else {
            throw new IllegalArgumentException("Item is not in the set");
        }
    }
    
    private void findSetHelper(int rep, DoubleLinkedList<Integer> nextLink) {
        nextLink.add(rep);
        if (pointers[rep] >= 0) {
            findSetHelper(pointers[rep], nextLink);
        } 
    }

    @Override
    public void union(T item1, T item2) {
        if (repItemLookup.containsKey(item1) && repItemLookup.containsKey(item2)) {
            int root1 = findSet(item1);
            int root2 = findSet(item2);
            if (root1 == root2) {
                throw new IllegalArgumentException("Items already have the same root");
            } 
            if ((-pointers[root1]) >= (-pointers[root2])) {
                unionHelper(root1, root2);
                if ((-pointers[root1]) == (-pointers[root2])) {
                    pointers[root1]--;
                }
            } else {
                unionHelper(root2, root1);
            }
        } else {
            throw new IllegalArgumentException("Item/Items is not in the set");
        }
    }
    
    private void unionHelper(int largerR, int smallerR) {
        pointers[smallerR] = largerR;
    }
}
