package com.github.scfj.decorators.robust;

import com.github.scfj.decorators.Robust;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TimedRobustTest {
    Patient notRobust;

    @Before
    public void decorateNotRobust() {
        notRobust = new Patient.Fake(100);
    }

    @Test
    public void shouldNotFail() throws IOException {
        Patient robust = Robust.decorate(notRobust, 2, 105);
        assertEquals(42, robust.bePatient());
    }

    @Test(expected = IOException.class)
    public void shouldFailWhenTimeLimitExceeded() throws IOException {
        Patient robust = Robust.decorate(notRobust, 2, 40);
        assertEquals(42, robust.bePatient());
    }

    public interface Patient {
        int bePatient() throws IOException;

        class Fake implements Patient {
            private final long respondAfter;

            public Fake(int waitMs) {
                this.respondAfter = System.currentTimeMillis() + waitMs;
            }

            @Override
            public int bePatient() throws IOException {
                if (System.currentTimeMillis() > respondAfter) {
                    return 42;
                } else {
                    throw new IOException("It's too early. Wait.");
                }
            }
        }
    }
}
