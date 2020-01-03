import java.util.*;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items. Duplicates
 * are allowed.
 * 
 * @author Felix Jiang
 *
 * @param <K> key - expect a comparable type for indexing
 * @param <V> value - expect a user-defined type
 */

/*
 * BPTree Invariants:
 * All children at index i have keys less than key at index i
 * Child at last index of children has keys >= to last key in keys
 * Internal node has 1 more child than key
 * Leaf node has same number of values as keys
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    // Leaves of this tree have branchingFactor values and keys
    private int branchingFactor;

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
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        if(key == null)
        {
            throw new IllegalArgumentException();
        }
        if(root == null)
        {
            root = new LeafNode();
        }
        root.insert(key, value);

        // is overflow if the insert overflows bumped all the way up to the root
        // or if root was the only node and it's full
        if(root.isOverflow())
        {
            InternalNode nRoot = new InternalNode();
            Node temp = root.split();
            K nkey = temp.keys.get(0);
            nRoot.keys.add(nkey);
            nRoot.children.add(root);
            nRoot.children.add(temp);
            // add temp second because it's larger than root
            if(root instanceof BPTree.InternalNode)
            {
                temp.keys.remove(0);
            }
            root = nRoot;
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
        if(key == null)
        {
            return new ArrayList<V>();
        }
        return root.rangeSearch(key, comparator);
    }
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#get(java.lang.Object)
     */
     @Override
     public V get(K key) {
         if(key == null || root.keys.isEmpty())
         {
             return null;
         }
         List<V> temp = root.rangeSearch(key, "==");
         if(temp.isEmpty())
         {
             return null;
         }
         return temp.get(0);
     }

    /*
     * (non-Javadoc)
     * @see BPTreeADT#size()
     */
     @Override
     public int size() {
         // number of leaves in the tree
         LeafNode start = (LeafNode) root.getFirstLeafNode();
         // could've also made getFirstLeafNode have return type LeafNode
         // unsure if it's better to have it's return type be Node
         int size = 0;
         while(start != null)
         {
             size++;
             start = start.next;
         }
         return size;
     }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     * Only method with provided implementation
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
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        
        /**
         * Package constructor
         */
        Node() {
            keys = new ArrayList<K>();
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
         * Gets the first leaf node of the tree
         * 
         * @return Node
         */
        abstract Node getFirstLeafNode();
        
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
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     *
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            children = new ArrayList<>();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafNode()
         */
        Node getFirstLeafNode() {
            if(children.isEmpty())
            {
                return null;
            }
            return children.get(0).getFirstLeafNode();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            if(branchingFactor < children.size())
            {
                return true;
            }
            return false;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            int i;
            for(i = 0; i < keys.size(); i++)
            {
                if(key.compareTo(keys.get(i)) < 0)
                {
                    break;
                }
            }
            children.get(i).insert(key, value);
            if(children.get(i).isOverflow())
            {
                // internal node reformatting requires removing middle term
                // middle term becomes key in parent node
                // otherwise there aren't enough children for each key
                if(children.get(i) instanceof BPTree.InternalNode)
                {
                    Node temp = children.get(i).split();
                    // temp is greater than children.get(i);
                    K nKey = temp.keys.remove(0);
                    // smallest key in greater child
                    // need to remove because internal node split

                    keys.add(i, nKey);
                    children.add(i + 1, temp);
                    // i + 1 because temp is greater than children.get(i);
                }
                if(children.get(i) instanceof  BPTree.LeafNode)
                {
                    Node temp = children.get(i).split();
                    K nkey = temp.keys.get(0);
                    // only difference between leaf/internal child is you don't
                    // remove the key if it's a leaf
                    keys.add(i, nkey);
                    children.add(i + 1, temp);
                }
            }
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            // split convention for this tree:
            // new node (that is returned) is greater
            int mid = branchingFactor / 2;
            InternalNode temp = new InternalNode();
            List<K> nKey = new ArrayList<K>();
            List<Node> nChildren = new ArrayList<Node>();
            // temp will have same number of keys and children for a bit
            // node that called split will know to remove key and add it to parent

            // nKey is larger since
            // for loop will be backwards in order to remove items
            for(int i = keys.size() - 1; i >= mid; i--)
            {
                nKey.add(0, keys.get(i));
                nChildren.add(0, children.get(i + 1));
                // adding at 0 keeps new lists sorted
                keys.remove(i);
                children.remove(i + 1);
                // index for children is i + 1 because there's 1 more children than key
                // also the new temp node will have same number of keys as children
            }
            temp.keys = nKey;
            temp.children = nChildren;
            return temp;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            int i;
            for(i = 0; i < keys.size(); i++)
            {
                if(key.compareTo(keys.get(i)) < 0)
                {
                    break;
                }
            }
            return children.get(i).rangeSearch(key, comparator);
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     *
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
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
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafNode()
         */
        Node getFirstLeafNode() {
            return this;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            if(branchingFactor < values.size())
            {
                return true;
            }
            return false;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            int i;
            for(i = 0; i < keys.size(); i++)
            {
                if(key.compareTo(keys.get(i)) < 0)
                {
                    break;
                }
            }
            keys.add(i, key);
            values.add(i, value);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            int mid = branchingFactor / 2;
            LeafNode temp = new LeafNode();
            List<K> nKey = new ArrayList<K>();
            List<V> nValues = new ArrayList<V>();
            // nKey and nValues contain larger values since
            // for loop will be backwards in order to remove items
            for(int i = keys.size() - 1; i >= mid; i--)
            {
                nKey.add(0, keys.get(i));
                nValues.add(0, values.get(i));
                // adding at 0 keeps new lists sorted
                keys.remove(i);
                values.remove(i);
            }
            temp.keys = nKey;
            temp.values = nValues;
            // changing pointers to other leaf nodes
            if(this.next != null)
            {
                this.next.previous = temp;
            }
            temp.next = this.next;
            this.next = temp;
            temp.previous = this;
            return temp;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            // returns list of values in sorted order based on their corresponding keys
            // values with same keys are in the order based on how they were added to tree
            List<V> out = new ArrayList<V>();
            int idx = 0; // index of first element in keys greater than or equal to key
            int lastIdx = 0; // index of first element (from end of list) in keys smaller than or equal to key

            for(int i = 0; i < keys.size(); i++)
            {
                if(key.compareTo(keys.get(i)) <= 0)
                {
                    idx = i;
                    break;
                }
            }
            for(int i = keys.size() - 1; i >= 0; i--)
            {
                if(key.compareTo(keys.get(i)) >= 0)
                {
                    lastIdx = i;
                    break;
                }
            }

            if(comparator.contentEquals("=="))
            {
                if(!keys.contains(key))
                {
                    // lastIdx < idx
                    return out;
                }
                for(int i = idx; i <= lastIdx; i++ )
                {
                    out.add(values.get(i));
                }
                return out;
            }
            if(comparator.contentEquals("<="))
            {
                LeafNode curr = previous;
                if(curr != null)
                {
                    while (curr.previous != null)
                    {
                        curr = curr.previous;
                    }
                    while (curr != this)
                    {
                        // adds all values less than key in BPTree to out
                        out.addAll(curr.values);
                        curr = curr.next;
                        // traversed to start of linked list instead of adding at index 0 of out
                        // that would've shifted values each time
                        // this is still linear but 2-pass
                    }
                }

                for(int i = 0; i <= lastIdx; i++)
                {
                    // adds all values less than or equal to key in this node to out
                    out.add(values.get(i));
                }

                // possible that some values == to key are in next node
                if(lastIdx == keys.size() - 1)
                {
                    LeafNode temp = next;
                    while(temp.keys.get(temp.keys.size() - 1).compareTo(key) == 0)
                    {
                        // if entire node "next" has the same key value as key
                        out.addAll(temp.values);
                    }
                    for(int i = 0; i < temp.keys.size(); i++)
                    {
                        if(temp.keys.get(i).compareTo(key) == 0)
                        {
                            out.add(temp.values.get(i));
                        }
                    }
                }
                return out;
            }
            if(comparator.contentEquals(">="))
            {
                for(int i = idx; i < keys.size(); i++)
                {
                    out.add(values.get(i));
                }
                LeafNode curr = next;
                while(curr != null)
                {
                    out.addAll(curr.values);
                    curr = curr.next;
                }
                return out;
            }
            return null;
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
        // generic method print allows easy editing of keys and values data type
        // don't need to change type of list and BPTree everytime the keys and values data types are changed
        Double[] keys = {0.0d, 0.5d, 0.2d, 0.8d};
        Double[] values = {0.0d, 0.5d, 0.2d, 0.8d};

        //String[] keys = {"Felix", "Jack", "Bailey", "Hannah", "Aaron", "Joe", "Zach", "Jack"};
        //Integer[] values = {18, 20, 45, 50, 13, 25, 70, 33};

        print(keys, values);

        // in the printed tree structure, being in the same curly bracket {} means you have the same parent node
        // being in the same square bracket [] means in the same node
        // [] represents nodes
        // {} represents same parent node
    }

    /*
    * Allows for minimal edits if type for keys/values array changes
    * No need to change the type of every variable now
     */
    public static <A extends Comparable<A>, B> void print(A[] keys, B[] values)
    {
        // create empty BPTree with branching factor of 3
        BPTree<A, B> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // building an ArrayList for values added to BPTree
        // allows for comparing the contents of the ArrayList
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<B> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = rnd1.nextInt(keys.length);
            A k = keys[index];
            B v = values[index];
            list.add(v);
            bpTree.insert(k, v);
            System.out.println("\nInsert " + k + "\nTree structure:\n" + bpTree.toString());
        }
        List<B> filteredValues = bpTree.rangeSearch(keys[2], "<=");
        System.out.println("Filtered values: " + filteredValues.toString());
        System.out.println("All values: " + list.toString());
        System.out.println("Size (Number of LeafNodes): " + bpTree.size());
    }

} // End of class BPTree
