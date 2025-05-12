package org.jfrog.buildinfo.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Tests for file path handling utilities, especially for paths containing spaces.
 */
public class FilePathHandlingTest {

    @Test
    public void testQuotePathIfNeeded() {
        // Test path without spaces
        String pathWithoutSpaces = "/path/to/file.txt";
        Assert.assertEquals(pathWithoutSpaces, Utils.quotePathIfNeeded(pathWithoutSpaces));

        // Test path with spaces on Unix-like systems
        String unixPathWithSpaces = "/path/to/my file.txt";
        if (File.separatorChar == '/') {
            Assert.assertEquals("\"" + unixPathWithSpaces + "\"", Utils.quotePathIfNeeded(unixPathWithSpaces));
        }

        // Test Windows path with spaces - we can simulate this with a mock
        if (File.separatorChar == '\\') {
            String windowsPathWithSpaces = "C:\\path\\to\\my file.txt";
            String expected = "\"C:\\\\path\\\\to\\\\my file.txt\"";
            Assert.assertEquals(expected, Utils.quotePathIfNeeded(windowsPathWithSpaces));
        }

        // Test null path is handled gracefully
        Assert.assertNull(Utils.quotePathIfNeeded(null));
    }
    
    @Test
    public void testQuotedPathsContainQuotes() {
        // Simple test to verify that paths with spaces are quoted
        String pathWithSpaces = "/path/to/file with spaces.txt";
        String result = Utils.quotePathIfNeeded(pathWithSpaces);
        Assert.assertTrue("Path with spaces should be quoted", result.startsWith("\""));
        Assert.assertTrue("Path with spaces should be quoted", result.endsWith("\""));
    }

    @Test
    public void testPrepareFilePathForCli() {
        // Test path without spaces
        String pathWithoutSpaces = "/path/to/file.txt";
        String normalizedPath = pathWithoutSpaces.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        Assert.assertEquals(normalizedPath, Utils.prepareFilePathForCli(pathWithoutSpaces));

        // Test path with spaces
        String pathWithSpaces = "/path/to/my file.txt";
        String normalizedPathWithSpaces = pathWithSpaces.replace('/', File.separatorChar).replace('\\', File.separatorChar);

        if (File.separatorChar == '\\') {
            // Windows testing
            String windowsPathWithSpaces = "C:\\path\\to\\my file.txt";
            String expected = "\"C:\\\\path\\\\to\\\\my file.txt\"";
            Assert.assertEquals(expected, Utils.prepareFilePathForCli(windowsPathWithSpaces));
        } else {
            // Unix testing
            Assert.assertEquals("\"" + normalizedPathWithSpaces + "\"", Utils.prepareFilePathForCli(pathWithSpaces));
        }

        // Test null path
        Assert.assertNull(Utils.prepareFilePathForCli(null));
    }

    @Test
    public void testWindowsPathEdgeCases() {
        // These tests will only run on Windows
        if (File.separatorChar == '\\') {
            // Test UNC path with spaces
            String uncPath = "\\\\server\\share\\path with spaces\\file.txt";
            String expected = "\"\\\\\\\\server\\\\share\\\\path with spaces\\\\file.txt\"";
            Assert.assertEquals(expected, Utils.prepareFilePathForCli(uncPath));

            // Test path with a mix of forward and backslashes
            String mixedPath = "C:/path\\to/file with spaces.txt";
            String normalizedExpected = "\"C:\\\\path\\\\to\\\\file with spaces.txt\"";
            Assert.assertEquals(normalizedExpected, Utils.prepareFilePathForCli(mixedPath));
        }
    }
} 