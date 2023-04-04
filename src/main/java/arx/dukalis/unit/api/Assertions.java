package arx.dukalis.unit.api;

/**
 * Various assertions.
 *
 * Mirrors a part of {@link org.junit.jupiter.api.Assertions}.
 */
public class Assertions {
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }

    public static void assertFalse(boolean condition) {
        assertTrue(!condition);
    }

    public static void assertEquals(Object expected, Object got) {
        assertTrue(expected.equals(got));
    }

    public static void assertEquals(byte expected, byte got) {
        assertTrue(expected == got);
    }
    public static void assertEquals(short expected, short got) {
        assertTrue(expected == got);
    }

    public static void assertEquals(int expected, int got) {
        assertTrue(expected == got);
    }

    public static void assertEquals(Integer expected, int got) {
        assertEquals(expected.intValue(), got);
    }

    public static void assertEquals(int expected, Integer got) {
        assertEquals(got, expected);
    }

    public static void assertEquals(long expected, long got) {
        assertTrue(expected == got);
    }

    public static void assertArrayEquals(Object[] expected, Object[] got) {
        if (expected == got) {
            return;
        }

        assertTrue(expected != null);
        assertTrue(got != null);
        assertTrue(expected.length == got.length);

        for (int i = 0; i < expected.length; i++) {
            Object expectedElement = expected[i];
            Object gotElement = got[i];

            if (expectedElement == gotElement) {
                continue;
            }

            assertEquals(expectedElement, gotElement);
        }
    }
}
