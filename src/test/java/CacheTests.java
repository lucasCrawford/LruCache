import org.junit.Assert;
import org.junit.Test;

/**
 * Test the basic functionality of the LruCache. Could be cleaner
 * but tests that the output is as expected given a specific input.
 *
 * TODO: Need to test multi-threaded access.
 *
 * Created by lcrawford on 4/16/16.
 */
public class CacheTests {

    @Test
    public void testBasicInsert(){

        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();

        /* Ensure the cache properly was built */
        Assert.assertSame(cache.peekLastEviction(), 3);
        Assert.assertSame(cache.peekNextEviction(), 1);
    }

    @Test
    public void testEviction(){
        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();
        int nextEviction = cache.peekNextEviction();

        //Expected final state of cache: 2 -> 3
        int evicted = cache.evict();

        /* Ensure the proper value was evicted and the state of the cache is correct afterwards */
        Assert.assertEquals(evicted, nextEviction);
        Assert.assertTrue(cache.peekNextEviction() == 2);
        Assert.assertTrue(cache.peekLastEviction() == 3);
    }

    @Test
    public void testStateInsertWithEviction(){
        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();

        //Expected final state of cache: 2 -> 3 -> 4
        cache.add(4);

        /* Ensure the state of the cache is proper after an insert requiring an eviction */
        Assert.assertTrue(cache.peekNextEviction() == 2);
        Assert.assertTrue(cache.peekLastEviction() == 4);
    }

    @Test
    public void testMultiInsertEvictions(){
        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();

        //Expected final state of cache: 4 -> 5 -> 6
        cache.add(4);
        cache.add(5);
        cache.add(6);

        Assert.assertTrue(cache.peekLastEviction() == 6);
        Assert.assertTrue(cache.peekNextEviction() == 4);
    }

    @Test
    public void testMultiOps(){
        LruCache<Integer> cache = baseCache();
        cache.get(2);

        Assert.assertTrue(true);
    }

    @Test
    public void testCacheAccessNull(){

        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();
        Integer value = cache.get(0);

        //Expected state of cache: 2 -> 3 -> 1
        Assert.assertNull(value);
    }

    @Test
    public void testCacheAccess(){
        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();
        Integer value = cache.get(1);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.intValue(), 1);
    }


    @Test
    public void testComplicatedCacheUsage(){

        //Expected final state of cache: 1 -> 2 -> 3
        LruCache<Integer> cache = baseCache();

        //Expected final state of cache: 2 -> 3 -> 4
        Integer newInsert = 4;
        cache.add(newInsert);
        Assert.assertEquals(newInsert,cache.peekLastEviction());

        //Expected final state of cache: 2 -> 4 -> 3
        Integer accessValue = cache.get(3); //get the cache item with a value of 3.
        Assert.assertEquals(accessValue, cache.peekLastEviction()); //ensure getting the value updates it as most recent

        //Expected final state of cache: 4 -> 3 -> 2
        Integer value = 2;
        accessValue = cache.get(value);
        Assert.assertEquals(accessValue, cache.peekLastEviction());

        //Expected final state of cache: 3 -> 2 -> 1337
        Integer finalInsert = 1337;
        cache.add(finalInsert);
        Assert.assertTrue(3 == cache.peekNextEviction());
        Assert.assertEquals(finalInsert, cache.peekLastEviction());
    }

    /**
     * Test the output of cache array is correct. The expected outcome is
     * that the array is from least to most frequently access, and the cache is as well.
     *
     * Can configure the output array size to be variable.
     * - If the user inputs less than the cache's size, only output the values for as
     * large as the array they gave it.
     * - If the user inputs greater than the cache's size, the rest of the values are null.
     */
    @Test
    public void testToArray(){
        LruCache<Integer> cache = baseCache();
        Integer[] cacheArray = cache.toArray(new Integer[10]);

        /* Verify the output array is the same order as the cache. */
        for(Integer value : cacheArray){
            if(value != null){
                Assert.assertSame(value, cache.evict());
            }
        }
    }

    /**
     * Test the output of cache reverse array is correct. The expected outcome is
     * that the array is from most frequently access to least, and the cache is least
     * frequent to most.
     *
     * Can configure the output array size to be variable.
     * - If the user inputs less than the cache's size, only output the values for as
     * large as the array they gave it.
     * - If the user inputs greater than the cache's size, the rest of the values are null.
     */
    @Test
    public void testToReverseArray(){
        LruCache<Integer> cache = baseCache();
        Integer[] cacheArray = cache.toReverseArray(new Integer[2]);

        /* Verify the output array is the reverse order of the cache.
         * Cache: Least frequent to most frequent.
         * Array: Most frequent to least frequent.
         */
        int arrayIdx = cacheArray.length - 1;
        Integer current = cache.evict();
        while(current != null && arrayIdx != -1){
            Assert.assertSame(current, cacheArray[arrayIdx]);
            arrayIdx -= 1;
            current = cache.evict();
        }
    }

    /**
     * Utility method to build a simple, full cache.
     * @return LruCache fill with 3 values (1, 2, 3)
     */
    private LruCache<Integer> baseCache(){
        LruCache<Integer> cache = new LruCache<>(3);

        cache.add(1);
        cache.add(2);
        cache.add(3);

        return cache;
    }

}
