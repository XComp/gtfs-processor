package com.mapohl.nyckafka.genericproducer.utils;

import com.google.common.collect.AbstractIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class LineIterator extends AbstractIterator<String> implements Iterator<String> {

    private final BufferedReader reader;

    private final int initialLinesToIgnore;
    private final long lineLimit;

    private int lineCount = 0;

    public LineIterator(Reader reader) {
        this(reader, Integer.MAX_VALUE);
    }

    public LineIterator(Reader reader, int lineLimit) {
        this(reader, lineLimit, 0);
    }

    public LineIterator(Reader reader, int lineLimit, int initialLinesToIgnore) {
        this.reader = new BufferedReader(reader);
        // casting the limit to long to avoid overflows
        this.lineLimit = (long) lineLimit + initialLinesToIgnore;
        this.initialLinesToIgnore = initialLinesToIgnore;
    }

    private String readLine() {
        try {
            return this.reader.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected String computeNext() {
        while (true) {
            String nextLine = this.readLine();
            this.lineCount++;

            if (nextLine == null || this.lineCount > this.lineLimit) {
                return this.endOfData();
            }

            if (this.lineCount <= this.initialLinesToIgnore) {
                continue;
            }

            return nextLine;
        }
    }
}
