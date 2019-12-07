package helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelperTest {

    @Test
    void randStringGen() {
        final int length = 12;
        final String s1 = Helper.randStringGen(length);

        assertNotEquals(s1, Helper.randStringGen(length));
        assertEquals(s1.length(), length);
    }
}