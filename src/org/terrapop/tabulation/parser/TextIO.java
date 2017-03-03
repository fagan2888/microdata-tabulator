 /*
  Microdata Tabulator
  https://github.com/mnpopcenter/microdata-tabulator
  Copyright (c) 2012-2017 Regents of the University of Minnesota

  Contributors:
    Alex Jokela, Minnesota Population Center, University of Minnesota
    Pranjul Yadav, Minnesota Population Center, University of Minnesota
 
  This project is licensed under the Mozilla Public License, version 2.0 (the
  "License"). A copy of the License is in the project file "LICENSE.txt",
  and is also available at https://www.mozilla.org/en-US/MPL/2.0/.
 */

package org.terrapop.tabulation.parser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextIO {

    // *************** Private Stuff To Follow *****************
    
    private BufferedReader in;
    
    private Matcher floatMtchr;   // Used for reading floating point numbers; created from the floatRegex Pattern.
    private final Pattern floatRegex = Pattern.compile("(\\+|-)?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?");
    
    private String buffer = null;  // One line read from input.
    private int pos = 0;           // Position of next char in input line that has not yet been processed.

    private void skipWhitespace() {
        char ch=_glance();
        while (ch != EOF && Character.isWhitespace(ch)) {
            readChar();
            ch = _glance();
        }
    }
    
    private String readRealString() {   // read chars from input following syntax of real numbers
        skipWhitespace();
        
        if (_glance() == EOF)
            return null;
        
        if (floatMtchr == null)
            floatMtchr = floatRegex.matcher(buffer);
        
        floatMtchr.region(pos,buffer.length());
        
        if (floatMtchr.lookingAt()) {
            String str = floatMtchr.group();
            pos = floatMtchr.end();
            return str;
        }
        else 
            return null;
    }
    
    private char _glance() {  // return next character from input
        if (buffer == null || pos > buffer.length())
            fillBuffer();
        if (buffer == null)
            return EOF;
        else if (pos == buffer.length())
            return '\n';
        else 
            return buffer.charAt(pos);
    }
    
    private char readChar() {  // return and discard next character from input
        char ch = _glance();
        if (buffer == null) {
            throw new IllegalArgumentException("Internal buffer is null");
        }
        pos++;
        return ch;
    }
        
    private void fillBuffer() {    // Wait for user to type a line and press return,
        try {
            buffer = in.readLine();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Errow while attempting to read form an input stream.");
        }
        pos = 0;
        floatMtchr = null;
    }
    
    private void emptyBuffer() {   // discard the rest of the current line of input
        buffer = null;
    }
    
    // ************ Public Facing Stuffs ************
	
    /**
     * The value returned by the glance() method when the input is at end-of-file.
     */
    public final static char EOF = (char)0xFFFF; 

    /**
     * The value returned by the glance() method when the input is at end-of-line.
     * The value of this constant is the character '\n'.
     */
    public final static char EOLN = '\n';          // The value returned by glance() when at end-of-line.

    public void closeInput() throws IOException {
      if(in != null) {
        in.close();
      }
    }

    public void readStream(InputStream inputStream) {
    	readStream(new InputStreamReader(inputStream));
    }
    
    public void readStream(Reader inputStream) {
        
        if ( inputStream instanceof BufferedReader)
            in = (BufferedReader)inputStream;
        else
            in = new BufferedReader(inputStream);
        
        emptyBuffer();

    }


    // *************************** Input Methods *********************************

    public char getAnyChar() { 
        return readChar(); 
    }

    public char glance() { 
        return _glance();
    }
    
    public void skipBlanks() { 
        char ch=_glance();
        while (ch != EOF && ch != '\n' && Character.isWhitespace(ch)) {
            readChar();
            ch = _glance();
        }
    }


    public String getLine() {
        StringBuffer s = new StringBuffer(168);
        char ch = readChar();
        while (ch != '\n') {
            s.append(ch);
            ch = readChar();
        }
        return s.toString();
    }
    
    public char getChar() { 
        skipWhitespace();
        return readChar();
    }
    
    public double getDouble() {
        double x = 0.0;
        while (true) {
            String str = readRealString();
            if (str == null) {
            	
            }
            else {
                try { 
                    x = Double.parseDouble(str); 
                }
                catch (NumberFormatException e) {
                	System.err.println("The value, " + x + ", is not a valid number.");
                    continue;
                }
                if (Double.isInfinite(x)) {
                    System.err.println("The value, " + x + ", is too large.");
                	continue;
                }
                break;
            }
        }
        
        return x;
    }
    
    public String getWord() {
        skipWhitespace();
        StringBuffer str = new StringBuffer(128);
        char ch = _glance();
        while (ch == EOF || !Character.isWhitespace(ch)) {
            str.append(readChar());
            ch = _glance();
        }
        return str.toString();
    }
    

} // end of class TextIO
