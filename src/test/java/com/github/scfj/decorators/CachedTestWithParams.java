package com.github.scfj.decorators;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CachedTestWithParams {
    CalculatorApi slow = new CalculatorApi.Fake();
    CalculatorApi fast = Cached.decorate(slow);

    @Test(timeout = 200 /* ms */)
    public void shouldBeFastWhenInvokedManyTimes() {
        for (int i = 0; i < 10; i++) {
            assertEquals(
                    "The result is 7",
                    fast.add(3, 4, "The result is %d")
            );
        }
    }

    interface CalculatorApi {
        String add(Integer a, Integer b, String formatString);

        class Fake implements CalculatorApi {
            @Override
            public String add(Integer a, Integer b, String formatString) {
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
