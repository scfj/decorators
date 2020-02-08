package com.github.scfj.decorators;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CachedTest {
    @Test(timeout = 130 /* ms */)
    public void shouldBeFastWhenInvokedManyTimes() throws IOException {
        File slow = new File.Fake();
        File fast = Cached.decorate(slow);
        for (int i = 0; i < 10_000; i++) {
            assertEquals("", fast.content());
        }
    }
}

interface File {
    String content() throws IOException;

    class Fake implements File {
        @Override
        public String content() throws IOException {
            try {
                Thread.sleep(100 /* ms */);
            } catch (InterruptedException e) {
                throw new IOException("Failed to read file content.");
            }
            return "";
        }
    }
}
