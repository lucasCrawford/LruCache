# LruCache
A simple implementation of a generic Least Recently Used cache. Supports constant time access for get, insert, remove, and updates (happens internally). Order is maintained in memory from least recently used to most frequently used. 

# Usage
```
//create a cache with a capacity of 3
LruCache<Integer> cache = new LruCache(3);  

cache.add(1);
cache.add(2);
cache.add(3);

//removes the least recently used value since the cache is full
cache.add(4);

//result: 2 -> 3 -> 4

Integer value = cache.get(2);

//result: 3 -> 4 -> 2
```

# Example Use Case
- Recently viewed profiles for a social networking application. Keep track of a fixed number of profiles a user might have recently viewed. Access order is maintained so you get ordering for free making it a nice feature for your application. 
- Keep track of fixed number of reasonably static objects that take time to download via network. This would avoid unncessary network calls. Downloading some data you know won't change, and will be constantly accessed would be an ideal thing to store in a LRU cache.

#TODO
- Add UNIT testing for multithreaded access.
- Need to add to public repository after fully complete with further testing
