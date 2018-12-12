import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.management.DescriptorRead;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author Addison Smith 
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    // Desired load is the number of children to move to a new internal node when splitting,
    // as well as the number of values to move to a new leaf node when splitting, and will
    // be derived from branchingFactor once branchingFactor is set.
    private int desiredLoad;
    
    // Pointer back to the first leaf, which will always stay in leftmost position because we always branch to the right
    private final Node originalLeaf;
    
    // Pointer up to the last leaf, which we update when splitting the rightmost leaf and use for optimizing rangeSearch
    
    // Tracker for how many levels the tree has (including leaf level).  Should only be updated when root is split.
    // Helps with calculations for when optimizations should be used that only help when depth is large.
    private int depth;
    
    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor = branchingFactor;
        this.desiredLoad = Math.round((float)(branchingFactor/2.0));
        root = new LeafNode();
        originalLeaf = root;
        depth=1;
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        root.insert(key, value);
        if(root.isOverflow()) {
        	Node temporary = root;
        	InternalNode newRoot = new InternalNode();
        	newRoot.children.add(root);
        	newRoot.children.add(root.split());
        	newRoot.keys.add(newRoot.children.get(1).getFirstLeafKey());
        	root=newRoot;
        	depth++;
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && 
            !comparator.contentEquals("==") && 
            !comparator.contentEquals("<=") )
            return new ArrayList<V>();
        return root.rangeSearch(key, comparator);
    }
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        
        /**
         * Package constructor
         */
        Node() {
            keys = new ArrayList<K>(branchingFactor);
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();
        
        public String toString() {
            return keys.toString();
        }
        
        abstract boolean isLeafNode();
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            children = new ArrayList<Node>();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            if(children.get(0)==null) return null;
        	return children.get(0).getFirstLeafKey();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return (children.size()>branchingFactor);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
        	if(key==null) return;
        	Node recipient;
        	int insertionPoint=0;
        	while(insertionPoint < keys.size() && key.compareTo(keys.get(insertionPoint))>=0 ) {
        		insertionPoint++;
        	}
        	// there are two cases:
        	// 1) we found a key greater than the key to insert, so we stopped incrementing
        	// 2) we ran out of keys in our keys array, so it goes in the topmost child
        	// but if our assumptions about our bookkeeping are correct, there is always 1 child
        	// beyond the number of keys on an internal node, so these can be treated as the same
        	// case
        	recipient = children.get(insertionPoint);
        	recipient.insert(key, value);
        	
        	if(recipient.isOverflow()) {
        	
	        	Node newChild = recipient.split();
	        	children.add(insertionPoint+1,newChild); //add to the spot just after the node that was split
	        	keys.add(insertionPoint, newChild.getFirstLeafKey());
	        	// there are two cases:
	        	// 1) this is a bottomPointerNode, so we will be inserting to a LeafNode
	        	// 2) this is not a bottomPointerNode, so we will be inserting to an InternalNode
	        	// ...but since we rely on the child's split() method, we can also treat these cases
	        	// as the same
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() { // assumption: keys.size() == branchingFactor && children.size() == branchingFactor+1
            InternalNode newSibling = new InternalNode();
            while(newSibling.children.size() < desiredLoad) {
            	Node shiftedChild=this.children.remove(this.children.size()-1);
            	newSibling.children.add(0,shiftedChild);
            	
            	K shiftedKey=this.keys.remove(this.keys.size()-1);
            	newSibling.keys.add(0,shiftedKey);
            }
            newSibling.keys.remove(0); // we will have shifted one more key into the new node than it needs
            return newSibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            if(key==null) return null;
            int lookupPoint=0;
            List<V> returnList;
            
            switch(comparator) {
            	case "==":			// this is the simple case, we can just do one call downstack and pass back what it returns
            		while(lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>=0) {
            			lookupPoint++;
            		}
            		if(lookupPoint==0) { // just go straight to the child without doing list index arithmetic and having to handle nullPointerException cases
            			returnList=children.get(0).rangeSearch(key, comparator);
            		} else {
            			returnList=children.get(lookupPoint).rangeSearch(key, comparator);
            		}
            		break;       		
            	case ">=":			// this requires finding a range of children to search on and aggregating their result
            		returnList=new LinkedList<V>();
            		while(lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>=0) {
            			lookupPoint++;
            		}
            		while(lookupPoint < children.size()) {
            			returnList.addAll(children.get(lookupPoint).rangeSearch(key, comparator));
            			lookupPoint++;
            		}
            		break;
            	case "<=":			// this requires (potentially) aggregate the results from searching a range of children
            		returnList=new LinkedList<V>();
            		while(lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>=0) {
            			returnList.addAll(children.get(lookupPoint).rangeSearch(key, comparator));
            			lookupPoint++;
            		}
            		returnList.addAll(children.get(lookupPoint).rangeSearch(key, comparator)); // since children always has one more subscript than keys does in an internalNode
            		break;
            	default:
            		returnList=null;
            }
            return returnList;
        }
              
        boolean isLeafNode() {
        	return false;
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
        // List of buckets to use when multiple values have been inserted with the same key
        // When an entry in the values List is explicitly set to null, that is the sentinel value that says to consult the bucket instead
        List<List<V>> valueBuckets;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            super();
            values = new ArrayList<V>();
            valueBuckets = new ArrayList<List<V>>();
            previous=null;
            next=null;
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return (values.size()>=branchingFactor);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
        	if(key==null) return;
        	// first find the first key greater than or equal to the input key
        	int insertionPoint=0;
        	while(insertionPoint < keys.size() && key.compareTo(keys.get(insertionPoint))>0) {
        		insertionPoint++;
        	}
        	if(insertionPoint == keys.size()) { // it's greater than all existing keys, so insert it at the end
        		keys.add(key);
        		values.add(value);
        		valueBuckets.add(null);
        	} else if(key.equals(keys.get(insertionPoint))) { // it's a duplicate key, so we have to bucket the values
        		keys.set(insertionPoint, key); // overwrite old key in case its a type for which .equals() allows different internal data; we want the newest-submitted key Object
        		// for dealing with values, there are two cases:
        		// 1) there is a value in the values List, in which case we need to make a new bucket,
        		//     move that value into it (setting its spot in values to null), and add the new value to it
        		// 2) that spot in the values list is already null, in which case we need to simply ignore it and add the new value to the bucket
        		if(values.get(insertionPoint)!=null) {
        			V previousValue=values.get(insertionPoint);
        			LinkedList<V> bucket = new LinkedList<V>();
        			bucket.add(previousValue); // add the value previously stored in the simple list
        			bucket.add(value); // add the newly inserted value
        			valueBuckets.set(insertionPoint, bucket); // this point should be empty, so we're pointing it to the new bucket object
        			values.set(insertionPoint, null);
        		} else {
        			valueBuckets.get(insertionPoint).add(value);
        		}
        	} else { // there isn't a matching key already in, and we've found a spot to insert between two existing keys
        		keys.add(insertionPoint,key);
        		values.add(insertionPoint,value);
        		valueBuckets.add(insertionPoint,null);
        	}
        	// There are 3 cases:
        	// 1) We found a key greater than our new key, and stopped.
        	// 2) We didn't find a key to stop on because they were all less than our new key,
        	//    so we made it to the end of the list.
        	// 3) We found a key exactly equal to our new key, and stopped.
        	// 1 & 2 can be treated the same because they both call for adding to the lists,
        	// but 3 calls for overwriting part of the values list.
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() { // assumption: keys.size() == branchingFactor && values.size() == branchingFactor
            LeafNode tempNext = next;
            next = new LeafNode();
            next.previous=this;
            next.next=tempNext;
            if(tempNext!=null) tempNext.previous=next;
            while(next.keys.size()<desiredLoad) {
            	next.keys.add(0,this.keys.remove(this.keys.size()-1)); // remove last key of this node and insert it in front of first key of next node
            	next.values.add(0,this.values.remove(this.values.size()-1)); // then do the same thing with values that we just did with keys
            	next.valueBuckets.add(0,this.valueBuckets.remove(this.valueBuckets.size()-1)); // then repeat with valueBuckets
            }
            return next;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            if(key==null) return null;
            int lookupPoint=0;
            List<V> returnList=null;
            
            switch(comparator) {
            case "==":
            	while (lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>0) {
            		lookupPoint++;
            	}
            	if(keys.get(lookupPoint)==null) break; // that key isn't in this node; return empty list instead of null to differentiate from erroring out
            	if(key.equals(keys.get(lookupPoint))) { 
            		returnList=valuesFromOneIndex(lookupPoint);
            	} else returnList=new LinkedList<V>();// otherwise, that key isn't in this node; return empty list instead of null to differentiate from erroring out
            	break;
            case ">=":
            	returnList=new LinkedList<V>();
            	while (lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>0) { // this loop is just to find the offset from which we start the loop that actually adds stuff
            		lookupPoint++;
            	}
            	while(lookupPoint < keys.size() && keys.get(lookupPoint)!=null) { // reuse end lookupPoint of prior loop as starting point for this loop
            		returnList.addAll(valuesFromOneIndex(lookupPoint));
            		lookupPoint++;
            	}
            	break;
            case "<=":
            	returnList=new LinkedList<V>();
            	while(lookupPoint < keys.size() && key.compareTo(keys.get(lookupPoint))>=0) {
            		returnList.addAll(valuesFromOneIndex(lookupPoint));
            		lookupPoint++;
            	}
            	break;
            default:
            	returnList=null; // just return null, to represent broken input
            }
            return returnList;
        }
        
        private List<V> valuesFromOneIndex(int index) {
        	LinkedList<V> returnList;
        	if(values.get(index)==null) { // this is the sentinel value to tell us to look in the bucket
    			returnList=new LinkedList<V>(valueBuckets.get(index));
        	} else {
    			returnList=new LinkedList<V>(); //start with it empty
    			returnList.add(values.get(index)); //stock it with the only matching value
        	}
        	return returnList;
        }
        
        boolean isLeafNode() {
        	return true;
        }
        
    } // End of class LeafNode
    
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);
        
        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree