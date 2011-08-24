/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package javax.cache;

import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import javax.net.ssl.SSLEngineResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for CacheManager
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheManagerTest extends TestSupport {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());




    @Test
    public void createCacheBuilder_NullCacheName() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        try {
            cacheManager.createCacheBuilder(null);
            fail("should have thrown an exception - null cache name not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_Same() {
        String name = "c1";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache cache = cacheManager.createCacheBuilder(name).build();
        assertSame(cache, cacheManager.getCache(name));
    }

    @Test
    public void createCache_NameOK() {
        String name = "c1";
        Cache cache = CacheManagerFactory.INSTANCE.getCacheManager().createCacheBuilder(name).build();
        assertEquals(name, cache.getName());
    }

    @Test
    public void createCache_StatusOK() {
        String name = "c1";
        Cache cache = CacheManagerFactory.INSTANCE.getCacheManager().createCacheBuilder(name).build();
        assertSame(Status.STARTED, cache.getStatus());
    }

    @Test
    public void createCache_NullCacheConfiguration() {
        String name = "c1";
        CacheBuilder builder = CacheManagerFactory.INSTANCE.getCacheManager().createCacheBuilder(name);
        try {
            builder.setCacheConfiguration(null);
            fail("should have thrown an exception - null cache configuration not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_CacheConfiguration_NameOK() {
        String name = "c1";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache cache = cacheManager.createCacheBuilder(name).
                setCacheConfiguration(cacheManager.createCacheConfiguration()).build();
        assertEquals(name, cache.getName());
        assertSame(cache, cacheManager.getCache(name));
    }

    @Test
    public void createCache_CacheConfiguration_StatusOK() {
        String name = "c1";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache cache = cacheManager.createCacheBuilder(name).
                setCacheConfiguration(cacheManager.createCacheConfiguration()).build();
        assertSame(Status.STARTED, cache.getStatus());
    }

    @Test
    public void createCache_Different() {
        String name1 = "c1";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache<Integer, String> cache1 = cacheManager.<Integer, String>createCacheBuilder(name1).build();
        assertEquals(Status.STARTED, cache1.getStatus());

        String name2 = "c2";
        Cache<Integer, String> cache2 = cacheManager.<Integer, String>createCacheBuilder(name2).build();
        assertEquals(Status.STARTED, cache2.getStatus());

        assertEquals(cache1, cacheManager.getCache(name1));
        assertEquals(cache2, cacheManager.getCache(name2));
    }

    @Test
    public void createCache_DifferentSameName() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        String name1 = "c1";
        Cache<Integer, String> cache1 = cacheManager.<Integer, String>createCacheBuilder(name1).build();
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache1);

        Cache<Integer, String> cache2 = cacheManager.<Integer, String>createCacheBuilder(name1).build();
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache2);
        checkStopped(cache1);
    }

    @Test
    public void removeCache_Null() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        try {
            cacheManager.removeCache(null);
            fail("should have thrown an exception - cache name null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void removeCache() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        String name1 = "c1";
        CacheBuilder<Integer, String> builder1 = cacheManager.createCacheBuilder(name1);
        Cache<Integer, String> cache1 = builder1.build();
        assertTrue(cacheManager.removeCache(name1));
        checkStopped(cache1);
    }

    @Test
    public void removeCache_NotThere() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager(this.toString());
        assertFalse(cacheManager.removeCache("c1"));
    }

    /**
     * Checks that stop is called on all caches.
     * Also checks CacheManager status
     */
    @Test
    public void shutdown() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        assertEquals(Status.STARTED, cacheManager.getStatus());

        String name1 = "c1";
        CacheBuilder<Integer, String> builder1 = cacheManager.createCacheBuilder(name1);
        Cache<Integer, String> cache1 = builder1.build();

        String name2 = "c2";
        CacheBuilder<Integer, String> builder2 = cacheManager.createCacheBuilder(name2);
        Cache<Integer, String> cache2 = builder2.build();

        cacheManager.shutdown();
        assertEquals(Status.STOPPED, cacheManager.getStatus());

        checkStopped(cache1);
        checkStopped(cache2);
    }

    @Test
    public void getUserTransaction() {
        boolean transactions = CacheManagerFactory.INSTANCE.isSupported(OptionalFeature.JTA);
        try {
            CacheManagerFactory.INSTANCE.getCacheManager().getUserTransaction();
            if (!transactions) {
                fail();
            }
        } catch (UnsupportedOperationException e) {
            assertFalse(transactions);
        }
    }


    //todo Fails sometimes. We need to define what happens on shutdown to the singleton.
//    @Test
    public void lifecycle() {
        CacheManagerFactory factory = CacheManagerFactory.INSTANCE;
        assertEquals(Status.STARTED, factory.getStatus());
        factory.shutdown();
        assertEquals(Status.STOPPED, factory.getStatus());
    }


    @Test
    public void createCacheConfiguration() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration cacheConfiguration = cacheManager.createCacheConfiguration();
        assertNotNull(cacheConfiguration);
        assertNotSame(cacheConfiguration, cacheManager.createCacheConfiguration());
    }

    // ---------- utilities ----------

    private void checkStarted(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STARTED || status == Status.STARTING);
    }

    private void checkStopped(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STOPPED || status == Status.STOPPING);
    }
}
