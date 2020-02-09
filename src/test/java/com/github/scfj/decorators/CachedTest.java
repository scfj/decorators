package com.github.scfj.decorators;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CachedTest {
    File slow = new File.Empty();
    File fast = Cached.decorate(slow);

    @Test(timeout = 200 /* ms */)
    public void shouldBeFastWhenInvokedManyTimes() throws IOException {
        for (int i = 0; i < 10; i++) {
            assertEquals("", fast.content());
        }
    }

    interface File {
        String content() throws IOException;

        class Empty implements File {
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
}