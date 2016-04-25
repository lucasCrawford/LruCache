import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple implementation of an LRU-cache (Least Recently Used).
 *
 * The head of the cache is the least recently used, and the tail is the
 * most recently used. So the order of access frequency is from the tail to head.
 *
 * Access Time: O(1)
 * Insert Time: O(1)
 * Remove Time: O(1)
 * Memory usage: O(3n) => O(n). In the worst case we have a doubly linked listed
 * To Array: O(min(n,m)) where n is the size of the cache, m is size of the array.
 * with a reference in the HashMap as well.
 *
 * Created by Lucas Crawford on 4/15/16.
 */
@SuppressWarnings("unused")
public class LruCache<T>{
    private static final int DEFAULT_SIZE = 10;
    private final int capacity; //capacity of cache cannot be changed.
    private AtomicInteger size = new AtomicInteger();

    /* Internal pointers: Head represents next eviction, tail is the most recently accessed */
    private Node<T> head;
    private Node<T> tail;
    private Map<T, Node<T>> dataMap;

    /**
     * Default constructor, capacity set to 10 (the default).
     */
    public LruCache(){
        this.capacity = DEFAULT_SIZE;
        dataMap = new HashMap<>(this.capacity);
    }

    /**
     * Constructor to instantiate the cache with a custom capacity.
     *
     * @param capacity - the capacity the user wishes to allocate
     */
    public LruCache(int capacity){
        this.capacity = capacity;
        dataMap = new HashMap<>(this.capacity);
    }

    /**
     * Add a particular value to the cache.
     * @param value - New value to add.
     */
    public final void add(T value){
        insertNode(value, true);
    }

    /**
     * Insert a node to the end of the cache. If the node is new we check if we must evict
     * the least frequently used node, otherwise just add to the end of the cache.
     *
     * The tail of the cache represents the most recently used node.
     * @param value - The new node to insert
     * @param isNew - true if the node is new, false if we are just updating to the most recently accessed.
     */
    protected final void insertNode(T value, boolean isNew){
        Node<T> newNode = new Node<>(value);

        synchronized (this) {
            if (head == null) {
                head = newNode;
                tail = head;
            } else {
                if (isNew && size.get() == capacity) {
                    evict();
                }
                boolean exists = dataMap.containsKey(value);
                if (exists) {
                    Node<T> previous = tail.getPrevious();
                    newNode.setPrevious(previous);
                    previous.setNext(newNode);
                } else {
                    newNode.setPrevious(tail);
                    tail.setNext(newNode);
                }
                tail = newNode;
            }
            dataMap.put(value, newNode);
        }
        if(isNew){
            size.incrementAndGet();
        }
    }

    /**
     * Remove the least recently used node from the cache.
     *
     * This node is the least recently used.
     *
     * @return returns the least recently used node just removed from the cache.
     */
    @SuppressWarnings("unchecked")
    public  T evict(){
        if(head == null){
            return null;
        }else{
            synchronized (this) {
                T previousKey = head.getValue();
                Node<T> prevHead = dataMap.remove(previousKey);
                head = prevHead.getNext();
                if (head != null) head.setPrevious(null);
                size.decrementAndGet();
                return previousKey;
            }
        }
    }

    /**
     * Get the value at the desired index. Accessing this particular value will update the
     * node's access state to being at the end of the cache (or the most recently used).
     *
     * @param key - value the user wishes to get. Should be found in the map.
     * @return - Value found at the desired index.
     */
    @SuppressWarnings("unchecked")
    public T get(T key) {
        if (key == null) {
            throw new NullPointerException("This key is null!");
        }

        synchronized (this) {
            Node<T> value = dataMap.remove(key);
            if (value == null) {
                return null;
            }
            Node<T> prev = value.getPrevious();
            Node<T> next = value.getNext();
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                head = value == head ? next : head;
                next.setPrevious(prev);
            }

            T result = value.getValue();
            insertNode(result, false);
            return result;
        }
    }

    /**
     * Return current size of the cache.
     * (you cannot set the size, it is only updated internally)
     *
     * @return the current number of objects in the cache
     */
    public int getSize(){
        return this.size.get();
    }

    /**
     * Return the capacity (or limit) to the size of the cache.
     * (you cannot update the capacity. This is set at creation)
     *
     * @return the current max capacity for the cache.
     */
    public int getCapacity(){
        return this.capacity;
    }

    /**
     * Peek which value will be removed next (does not remove or update it).
     *
     * @return value to be evicted next.
     */
    public T peekNextEviction(){
        return this.head == null? null : this.head.getValue();
    }

    /**
     * Peek which value was most recently accessed (does not remove or update it).
     *
     * @return value that was most recently used.
     */
    public T peekLastEviction(){
        return this.tail == null? null : this.tail.getValue();
    }

    /**
     * Convert the cache to an array. Takes an input array to output the values
     * into.
     *
     * @param input - Array we wish to output the values into.
     * @return The array in order from least recently used to most.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] input){
        if(this.size.get() == 0){
            return null;
        }

        Node<T> current = head;
        for(int i=0, len=input.length; i<len && current != null; ++i){
            input[i] = current.getValue();
            current = current.getNext();
        }

        return input;
    }

    /**
     * Convert the cache into the most recent accessed order.
     *
     * @param input - Array we wish to output the values into.
     * @return The array in reverse order from most recently used to least.
     */
    @SuppressWarnings("unchecked")
    public T[] toReverseArray(T[] input){
        if(this.size.get() == 0){
            return null;
        }

        Node<T> current = head;
        for(int i=input.length-1; i>-1 && current != null; --i){
            input[i] = current.getValue();
            current = current.getNext();
        }
        return input;
    }

    /**
     * This is a doubly-linked node that keeps track of both the next and previous
     * node in memory. Requires both links to maintain constant time operations.
     *
     * Private class, non-accessible by anything but this data structure.
     */
    private class Node<R> {
        private R value;
        private Node<R> next;
        private Node<R> previous;

        public Node(){}
        public Node(R t){
            this.value = t;
        }

        public Node<R> getNext() {
            return next;
        }

        public void setNext(Node<R> next) {
            this.next = next;
        }

        public Node<R> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<R> previous) {
            this.previous = previous;
        }

        public R getValue() {
            return value;
        }

        public void setValue(R value) {
            this.value = value;
        }

    }
}
