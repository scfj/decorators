package com.github.scfj.decorators.cache;

import com.github.scfj.decorators.Cache;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CacheTest {
    File slow = new File.EmptyFile();
    File fast = Cache.decorate(slow);

    @Test(timeout = 200 /* ms */)
    public void shouldBeFastWhenInvokedManyTimes() throws IOException {
        for (int i = 0; i < 10; i++) {
            assertEquals("", fast.content());
        }
    }

    public interface File {
        String content() throws IOException;

        class EmptyFile implements File {
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
