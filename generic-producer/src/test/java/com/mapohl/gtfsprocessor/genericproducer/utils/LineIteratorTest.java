package com.mapohl.gtfsprocessor.genericproducer.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;

class LineIteratorTest {

    @Test
    public void testIteration() throws IOException {
        String[] lines = new String[]{
                "line0",
                "line1",
                "line2",
                "line3",
                "line4"
        };

        String fileContent = String.join("\n", lines);
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(fileContent.getBytes()))) {
            LineIterator testInstance = new LineIterator(reader, 3, 1);

            assertTrue(testInstance.hasNext());
            assertEquals("line1", testInstance.next());

            assertTrue(testInstance.hasNext());
            assertEquals("line2", testInstance.next());

            assertTrue(testInstance.hasNext());
            assertEquals("line3", testInstance.next());

            assertFalse(testInstance.hasNext());
        }
    }

}