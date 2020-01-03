import java.util.List;

/**
 * A generic B+ Tree interface
 * (Abstract Data Type)
 */
public interface BPTreeADT<K, V> {

    /**
     * Inserts the key and value in the appropriate nodes in the tree
     * If the key is null, throw IllegalArgumentException
     * 
     * Note: key-value pairs with duplicate keys can be inserted into the tree.
     * 
     * @param key
     * @param value
     */
    public void insert(K key, V value) throws IllegalArgumentException;
    
    
    /**
     * Gets the values that satisfy the given range 
     * search arguments.
     * 
     * Value of comparator can be one of these: 
     * "<=", "==", ">="
     * 
     * Example:
     *     If given key = 2.5 and comparator = ">=":
     *         return all the values with the corresponding 
     *      keys >= 2.5
     *      
     * If key is null or not found, return empty list.
     * If comparator is null, empty, or not according
     * to required form, return empty list.
     * 
     * @param key to be searched
     * @param comparator is a string
     * @return list of values that are the result of the 
     * range search; if nothing found, return empty list
     */
    public List<V> rangeSearch(K key, String comparator);


    /**
     * Returns the value of the first leaf with a matching key.
     * If key is null, return null.
     * If key is not found, return null.
     *
     * @param key to find
     * @return value of the first leaf matching key
     */
     public V get(K key);


     /**
      * Return the number of leaves in the tree.
      *
      * @return number of leaves
      */
      public int size();
    
    
    /**
     * Returns a string representation for the tree
     * This method is provided to students in the implementation.
     * @return a string representation
     */
    public String toString();
}