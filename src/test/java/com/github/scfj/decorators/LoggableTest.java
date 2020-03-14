package com.github.scfj.decorators;

import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class LoggableTest {
    FakeFile file = new FakeFile();
    CharSequence string = "object";
    CharSequence loggableString = Loggable.decorate(string, new PrintStream(file));

    @Test
    public void testLog() {
        loggableString.charAt(2);
        assertTrue(file.contains("charAt")
                && file.contains("object")
                && file.contains("2"));
    }

    @Test
    public void testError() {
        try {
            loggableString.charAt(loggableString.length());
        } catch (Throwable ignored) {
        }
        assertTrue(file.contains("object")
                && file.contains("IndexOutOfBoundsException")
                && file.contains("index out of range"));
    }

    static class FakeFile extends OutputStream {
        StringBuilder stringBuilder = new StringBuilder();

        @Override
        public void write(int b) {
            stringBuilder.append((char) b);
        }

        public boolean contains(String value) {
            return stringBuilder.toString().contains(value);
        }
    }
}
