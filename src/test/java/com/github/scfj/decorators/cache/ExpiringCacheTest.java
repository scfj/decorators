package com.github.scfj.decorators.cache;

import com.github.scfj.decorators.ExpiringCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpiringCacheTest {
    Counter.Simple original = new Counter.Simple(1);
    Counter decorated = ExpiringCache.decorate(original, 10);

    @Test
    public void test() throws InterruptedException {
        assertEquals(1, decorated.value());
        decorated.inc();
        assertEquals(1, decorated.value());
        Thread.sleep(11);
        assertEquals(2, decorated.value());
    }

    public interface Counter {
        int value();

        void inc();

        class Simple implements Counter {
            int value;

            Simple(int initialValue) {
                value = initialValue;
            }

            @Override
            public int value() {
                return value;
            }

            @Override
            public void inc() {
                value += 1;
            }
        }
    }
}
