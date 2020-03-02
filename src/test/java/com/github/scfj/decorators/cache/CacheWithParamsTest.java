package com.github.scfj.decorators.cache;

import com.github.scfj.decorators.Cache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheWithParamsTest {
    CalculatorApi slow = new CalculatorApi.Fake();
    CalculatorApi fast = Cache.decorate(slow);

    @Test(timeout = 200 /* ms */)
    public void shouldBeFastWhenInvokedManyTimes() {
        for (int i = 0; i < 10; i++) {
            assertEquals(
                    "The result is 7",
                    fast.add(3, 4, "The result is %d")
            );
        }
    }

    public interface CalculatorApi {
        String add(int a, int b, String formatString);

        class Fake implements CalculatorApi {
            @Override
            public String add(int a, int b, String formatString) {
                try {
                    Thread.sleep(100 /* ms */);
                } catch (InterruptedException e) {
                    throw new ArithmeticException("Can't add.");
                }
                return String.format(formatString, a + b);
            }
        }
    }
}
