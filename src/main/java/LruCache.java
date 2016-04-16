/**
 * Simple implementation of an LRU-cache (Least Recently Used).
 *
 * The head of the cache is the least recently used, and the tail is the
 * most recently used. So the order of access frequency is from the tail to head.
 *
 * Access Time: O(n) worst case. Could be improved if we used some sort of hashing.
 * Insert Time: O(1)
 * Remove Time: O(1)
 * To Array: O(min(n,m)) where n is the size of the cache, m is size of the array.
 * Memory usage: O(n) worst case when capacity is reached. Memory is allocated dynamically
 *
 * Created by Lucas Crawford on 4/15/16.
 */
@SuppressWarnings("unused")
public class LruCache<T>{
    private static final int DEFAULT_SIZE = 10;
    private final int capacity; //capacity of cache cannot be changed.
    private int size = 0;

    /* Internal pointers: Head represents next eviction, tail is the most recently accessed */
    private Node<T> head;
    private Node<T> tail;

    /**
     * Default constructor, capacity set to 10 (the default).
     */
    public LruCache(){
        capacity = DEFAULT_SIZE;
    }

    /**
     * Constructor to instantiate the cache with a custom capacity.
     *
     * @param capacity - the capacity the user wishes to allocate
     */
    public LruCache(int capacity){
        this.capacity = capacity;
    }

    public void add(T value){
        insertNode(new Node<>(value), true);
    }

    /**
     * Insert a node to the end of the cache. If the node is new we check if we must evict
     * the least frequently used node, otherwise just add to the end of the cache.
     *
     * The tail of the cache represents the most recently used node.
     * @param value - The new node to insert
     * @param isNew - true if the node is new, false if we are just updating to the most recently accessed.
     */
    private void insertNode(Node<T> value, boolean isNew){
        if(head == null){
            head = value;
            tail = head;
        }else{
            if(isNew && size == capacity) {
                evict();
            }
            tail.setNext(value);
            tail = value;
        }
        ++size;
    }

    /**
     * Remove the least recently used node from the cache.
     *
     * This node is the least recently used.
     *
     * @return returns the least recently used node just removed from the cache.
     */
    @SuppressWarnings("unchecked")
    public T evict(){
        if(head == null){
            return null;
        }else{
            Node<T> prevHead = head;
            head = prevHead.getNext();
            --size;
            return prevHead.getValue();
        }
    }

    /**
     * Get the value at the desired index. Accessing this particular value will update the
     * node's access state to being at the end of the cache (or the most recently used).
     *
     * @param index - index we wish to access in the cache.
     * @return - Value found at the desired index.
     */
    @SuppressWarnings("unchecked")
    public T get(int index){
        Node<T> current = head;
        Node<T> prev = head;

        /* If the request was for a value that doesn't exist */
        if(index > size){
            throw new IndexOutOfBoundsException("Index " + index + " is invalid. " + "The size of the cache is: " + size);
        }

        int idx = 0;
        while(current != null){

            /* Once we reach the desired index, update the structure */
            if(idx == index){
                Node<T> currentNext = current.getNext();

                /* Update the head if necessary */
                if(current == head){
                    this.head = currentNext;
                }

                /* Update new tail if necessary */
                if(currentNext == null){
                    this.tail = prev;
                }

                insertNode(current, false);
                return current.getValue();
            }

            /* Move the pointers */
            prev = current;
            current = current.getNext();
            ++idx;
        }
        return null;
    }

    /**
     * Return current size of the cache.
     * (you cannot set the size, it is only updated internally)
     *
     * @return the current number of objects in the cache
     */
    public int getSize(){
        return this.size;
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
        if(this.size == 0){
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
        if(this.size == 0){
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
     * Private class, non-accessible by anything but this data structure.
     *
     * Used to internally keep track of the objects allocated in memory.
     */
    private class Node<R> {
        private R value;
        private Node next;

        public Node(){}
        public Node(R t){
            this.value = t;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public R getValue() {
            return value;
        }

        public void setValue(R value) {
            this.value = value;
        }

    }
}
