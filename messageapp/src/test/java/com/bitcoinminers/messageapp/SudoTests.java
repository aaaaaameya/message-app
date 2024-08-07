package com.bitcoinminers.messageapp;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Names:
 * Alice
 * Bob
 * Carol
 * Dan
 * Erin
 * Frank
 */

public class SudoTests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    /**
     * Change out and err location to an easy-to-read byte stream.
     * Courtesy of https://stackoverflow.com/a/1119559
     */
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * Revert out and err location to the original streams.
     * Courtesy of https://stackoverflow.com/a/1119559
     */
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testCreateUsers() {
        Session.main(new String[] {"src/test/resources/testCreateUsers.txt"});

        assertEquals(outContent.toString(), """
                User Alice created with id 0
                User Bob created with id 1
                """);
    }

    @Test
    public void testShowUsers() {
        Session.main(new String[] {"src/test/resources/testShowUsers.txt"});

        assertEquals(outContent.toString(), """
                User Alice created with id 0
                User Bob created with id 1
                0: Alice
                1: Bob
                """);
    }
}
