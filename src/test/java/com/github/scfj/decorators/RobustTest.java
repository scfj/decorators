package com.github.scfj.decorators;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RobustTest {
    NotRobust notRobust;

    @Before
    public void decorateNotRobust() {
        notRobust = new NotRobust.Fake(2);
    }

    @Test
    public void shouldNotFail() throws IOException {
        NotRobust robust = Robust.decorate(notRobust, 3);
        assertEquals(42, robust.canFail());
    }

    @Test(expected = IOException.class)
    public void shouldFailWhenLimitExceeded() throws IOException {
        NotRobust robust = Robust.decorate(notRobust, 2);
        assertEquals(42, robust.canFail());
    }

    interface NotRobust {
        int canFail() throws IOException;

        class Fake implements NotRobust {
            int failsLeft;

            Fake(int failsBeforeSuccess) {
                this.failsLeft = failsBeforeSuccess;
            }

            @Override
            public int canFail() throws IOException {
                if (failsLeft > 0) {
                    failsLeft -= 1;
                    throw new IOException();
                }
                return 42;
            }
        }
    }
}
